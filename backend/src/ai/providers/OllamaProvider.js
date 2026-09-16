import { AiProvider } from "../AiProvider.js";
import { AiProviderError } from "../AiProviderError.js";
import { generatedScenesResultSchema } from "../schemas/sceneSchema.js";
import { buildScenePrompt } from "../prompts/scenePrompt.js";

const OllamaSceneSchema = {
  type: "object",
  properties: {
    scenes: {
      type: "array",
      items: {
        type: "object",
        properties: {
          position: { type: "integer" },
          title: { type: "string" },
          narration: { type: "string" },
          visualPrompt: { type: "string" },
          durationSeconds: { type: "integer" }
        },
        required: ["position", "title", "narration", "visualPrompt", "durationSeconds"]
      }
    }
  },
  required: ["scenes"]
};

export class OllamaProvider extends AiProvider {
  constructor({
    baseUrl = process.env.OLLAMA_BASE_URL || "http://127.0.0.1:11434",
    model = process.env.OLLAMA_MODEL || "qwen2.5:7b"
  } = {}) {
    super();
    this.baseUrl = baseUrl;
    this.model = model;
  }

  async generateScenes(input) {
    try {
      const response = await fetch(`${this.baseUrl}/api/chat`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({
          model: this.model,
          stream: false,
          format: OllamaSceneSchema,
          messages: [
            {
              role: "system",
              content: `أعد JSON فقط وفق المخطط المطلوب. لا تضف Markdown أو شرحًا خارجه.`
            },
            {
              role: "user",
              content: buildScenePrompt(input)
            }
          ]
        })
      });

      if (!response.ok) {
        throw new AiProviderError(`Ollama HTTP error: ${response.status}`, {
          provider: "ollama",
          code: response.status === 429 ? "RATE_LIMITED" : "PROVIDER_UNAVAILABLE",
          retryable: response.status >= 500 || response.status === 429
        });
      }

      const data = await response.json();
      const text = data.message?.content;
      if (!text) {
        throw new AiProviderError("Ollama returned empty output", { provider: "ollama", code: "INVALID_OUTPUT" });
      }

      const parsed = JSON.parse(text);
      const validated = generatedScenesResultSchema.parse(parsed);

      return {
        ...validated,
        provider: "ollama",
        model: this.model
      };
    } catch (error) {
      if (error instanceof AiProviderError) throw error;
      throw new AiProviderError("Ollama request failed", {
        provider: "ollama",
        code: "PROVIDER_UNAVAILABLE",
        retryable: true,
        cause: error
      });
    }
  }
}
