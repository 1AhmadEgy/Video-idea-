import { createAiProvider } from "./createAiProvider.js";

export class AiRepository {
  constructor({ provider = createAiProvider() } = {}) {
    this.provider = provider;
  }
  async generateScenes(input) {
    return this.provider.generateScenes(input);
  }
}
