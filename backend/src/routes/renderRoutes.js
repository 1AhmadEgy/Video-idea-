import express from "express";
import { createJob } from "../services/jobService.js";
import { runImageJob, runAudioJob, runRenderJob } from "../services/mediaJobService.js";

const router = express.Router();

router.post("/projects/:projectId/generate-images", async (req, res) => {
  const { projectId } = req.params;
  const job = createJob({ projectId, type: "GENERATE_IMAGES" });
  runImageJob({
    jobId: job.jobId,
    projectId,
    sceneIds: req.body.sceneIds || []
  }).catch(console.error);
  res.status(202).json(job);
});

router.post("/projects/:projectId/generate-audio", async (req, res) => {
  const { projectId } = req.params;
  const job = createJob({ projectId, type: "GENERATE_AUDIO" });
  runAudioJob({
    jobId: job.jobId,
    projectId,
    voice: req.body.voice || "ar-female-1",
    language: req.body.language || "ar"
  }).catch(console.error);
  res.status(202).json(job);
});

router.post("/projects/:projectId/render", async (req, res) => {
  const { projectId } = req.params;
  const job = createJob({ projectId, type: "FINAL_RENDER" });
  runRenderJob({
    jobId: job.jobId,
    projectId,
    options: req.body
  }).catch(console.error);
  res.status(202).json(job);
});

export default router;
