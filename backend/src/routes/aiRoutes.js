import express from "express";
import { AiRepository } from "../ai/AiRepository.js";

const router = express.Router();
const aiRepository = new AiRepository();

router.post("/projects/:projectId/generate-scenes", async (req, res) => {
  try {
    const { projectId } = req.params;
    const { idea, language = "ar", template = "educational", aspectRatio = "9:16", durationSeconds = 30, sceneCount = 5 } = req.body;
    
    if (typeof idea !== "string" || idea.trim().length < 3) {
      return res.status(400).json({ code: "INVALID_INPUT", message: "الفكرة قصيرة أو فارغة" });
    }

    const result = await aiRepository.generateScenes({
      projectId, idea: idea.trim(), language, template, aspectRatio, durationSeconds, sceneCount
    });

    return res.json({
      projectId,
      provider: result.provider,
      model: result.model,
      scenes: result.scenes
    });
  } catch (error) {
    console.error(error);
    return res.status(500).json({
      code: error.code || "AI_PROVIDER_ERROR",
      provider: error.provider,
      message: error.message,
      retryable: Boolean(error.retryable)
    });
  }
});

export default router;
