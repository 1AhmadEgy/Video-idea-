import express from "express";
import { getJob } from "../services/jobService.js";

const router = express.Router();

router.get("/jobs/:jobId", (req, res) => {
  const job = getJob(req.params.jobId);
  if (!job) {
    return res.status(404).json({ message: "Job not found" });
  }
  return res.json(job);
});

export default router;
