import { AiProvider } from "../AiProvider.js";

export class FakeProvider extends AiProvider {
  async generateScenes(input) {
    return {
      provider: "fake",
      model: "fake-model",
      scenes: Array.from({ length: input.sceneCount }, (_, index) => ({
        position: index,
        title: `مشهد ${index + 1}`,
        narration: `نص تجريبي عن ${input.idea}`,
        visualPrompt: `مشهد بصري عن ${input.idea}`,
        durationSeconds: 5
      }))
    };
  }
}
