import { GoogleGenAI } from "@google/genai";
import { AiProvider } from "../AiProvider.js";
import { AiProviderError } from "../AiProviderError.js";
import { generatedScenesResultSchema } from "../schemas/sceneSchema.js";
import { buildScenePrompt } from "../prompts/scenePrompt.js";

const GeminiSceneSchema = {
  type: "OBJECT",
  properties: {
    scenes: {
      type: "ARRAY",
      items: {
        type: "OBJECT",
        properties: {
          position: { type: "INTEGER" },
          title: { type: "STRING" },
          narration: { type: "STRING" },
          visualPrompt: { type: "STRING" },
          durationSeconds: { type: "INTEGER" }
        },
        required: ["position", "title", "narration", "visualPrompt", "durationSeconds"]
      }
    }
  },
  required: ["scenes"]
};

export class GeminiProvider extends AiProvider {
  constructor({
    apiKey = process.env.GEMINI_API_KEY,
    model = process.env.GEMINI_MODEL || "gemini-2.5-flash"
  } = {}) {
    super();
    if (!apiKey) {
      throw new AiProviderError("GEMINI_API_KEY is missing", {
        provider: "gemini",
        code: "AUTHENTICATION_FAILED"
      });
    }
    this.client = new GoogleGenAI({ apiKey });
    this.model = model;
  }

  async generateScenes(input) {
    try {
      const response = await this.client.models.generateContent({
        model: this.model,
        contents: buildScenePrompt(input),
        config: {
          responseMimeType: "application/json",
          responseSchema: GeminiSceneSchema
        }
      });

      const text = response.text;
      if (!text) {
        throw new AiProviderError("Gemini returned empty output", { provider: "gemini", code: "INVALID_OUTPUT" });
      }

      const parsed = JSON.parse(text);
      const validated = generatedScenesResultSchema.parse(parsed);

      return {
        ...validated,
        provider: "gemini",
        model: this.model
      };
    } catch (error) {
      if (error instanceof AiProviderError) throw error;
      throw new AiProviderError("Gemini request failed", {
        provider: "gemini",
        code: "PROVIDER_UNAVAILABLE",
        retryable: true,
        cause: error
      });
    }
  }
}
