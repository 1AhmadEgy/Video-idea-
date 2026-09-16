import { GeminiProvider } from "./providers/GeminiProvider.js";
import { OllamaProvider } from "./providers/OllamaProvider.js";
import { FakeProvider } from "./providers/FakeProvider.js";
import { OpenAiCompatibleProvider } from "./providers/OpenAiCompatibleProvider.js";

const providers = {
  gemini: () => new GeminiProvider(),
  ollama: () => new OllamaProvider(),
  fake: () => new FakeProvider(),
  openai: () => new OpenAiCompatibleProvider({
    providerName: "openai",
    apiKey: process.env.OPENAI_API_KEY,
    baseUrl: process.env.OPENAI_BASE_URL || "https://api.openai.com/v1",
    model: process.env.OPENAI_MODEL || "gpt-4o-mini"
  }),
  deepseek: () => new OpenAiCompatibleProvider({
    providerName: "deepseek",
    apiKey: process.env.DEEPSEEK_API_KEY,
    baseUrl: process.env.DEEPSEEK_BASE_URL || "https://api.deepseek.com/v1",
    model: process.env.DEEPSEEK_MODEL || "deepseek-chat"
  }),
  aimlapi: () => new OpenAiCompatibleProvider({
    providerName: "aimlapi",
    apiKey: process.env.AIMLAPI_API_KEY,
    baseUrl: process.env.AIMLAPI_BASE_URL || "https://api.aimlapi.com/v1",
    model: process.env.AIMLAPI_MODEL || "gpt-4o-mini"
  })
};

export function createAiProvider({
  provider = process.env.AI_PROVIDER || "gemini"
} = {}) {
  const name = provider.trim().toLowerCase();
  const factory = providers[name];
  if (!factory) {
    throw new Error(`Unsupported AI provider: ${provider}`);
  }
  return factory();
}
