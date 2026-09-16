import { z } from "zod";

export const generatedSceneSchema = z.object({
  position: z.number().int().min(0),
  title: z.string().min(1),
  narration: z.string().min(1),
  visualPrompt: z.string().min(1),
  durationSeconds: z.number().int().min(1).max(180)
});

export const generatedScenesResultSchema = z.object({
  scenes: z.array(generatedSceneSchema).min(1)
});
