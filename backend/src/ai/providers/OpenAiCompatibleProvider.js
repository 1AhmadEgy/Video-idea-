import { AiProvider } from "../AiProvider.js";
import { AiProviderError } from "../AiProviderError.js";
import { generatedScenesResultSchema } from "../schemas/sceneSchema.js";
import { buildScenePrompt } from "../prompts/scenePrompt.js";

export class OpenAiCompatibleProvider extends AiProvider {
  constructor({
    providerName = "openai",
    apiKey = process.env.OPENAI_API_KEY,
    baseUrl = "https://api.openai.com/v1",
    model = "gpt-4o-mini"
  } = {}) {
    super();
    if (!apiKey) {
      throw new AiProviderError(`${providerName.toUpperCase()}_API_KEY is missing`, {
        provider: providerName,
        code: "AUTHENTICATION_FAILED"
      });
    }
    this.providerName = providerName;
    this.apiKey = apiKey;
    this.baseUrl = baseUrl.replace(/\/$/, "");
    this.model = model;
  }

  async generateScenes(input) {
    try {
      const prompt = buildScenePrompt(input);
      const systemPrompt = `You are an expert AI video scriptwriter and director. You must return valid JSON only matching this schema:
{
  "scenes": [
    {
      "position": 1,
      "title": "Scene title",
      "narration": "Voiceover script in the requested language",
      "visualPrompt": "Detailed visual description for video generation",
      "durationSeconds": 5
    }
  ]
}
Ensure the sum of scene durations approximately equals the requested total duration (${input.durationSeconds} seconds). Do NOT include markdown code fences or conversational text. Output pure JSON only.`;

      const response = await fetch(`${this.baseUrl}/chat/completions`, {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
          "Authorization": `Bearer ${this.apiKey}`
        },
        body: JSON.stringify({
          model: this.model,
          messages: [
            { role: "system", content: systemPrompt },
            { role: "user", content: prompt }
          ],
          response_format: { type: "json_object" },
          temperature: 0.7
        })
      });

      if (!response.ok) {
        const errText = await response.text();
        throw new Error(`HTTP ${response.status}: ${errText}`);
      }

      const data = await response.json();
      const content = data.choices?.[0]?.message?.content;
      if (!content) {
        throw new AiProviderError("Provider returned empty response", {
          provider: this.providerName,
          code: "INVALID_OUTPUT"
        });
      }

      const cleaned = content.replace(/^```json\s*/i, "").replace(/```$/, "").trim();
      const parsed = JSON.parse(cleaned);
      const validated = generatedScenesResultSchema.parse(parsed);

      return {
        ...validated,
        provider: this.providerName,
        model: this.model
      };
    } catch (error) {
      if (error instanceof AiProviderError) throw error;
      throw new AiProviderError(`${this.providerName} request failed: ${error.message}`, {
        provider: this.providerName,
        code: "PROVIDER_UNAVAILABLE",
        retryable: true,
        cause: error
      });
    }
  }
}
