import { updateJob } from "./jobService.js";
import { generateImagesForProject } from "./imageGenerationService.js";
import { generateAudioForProject } from "./audioGenerationService.js";
import { renderProject } from "./renderService.js";

export async function runImageJob({ jobId, projectId, sceneIds }) {
  try {
    updateJob(jobId, { status: "RUNNING", stage: "GENERATING_IMAGES" });
    await generateImagesForProject({
      projectId,
      sceneIds,
      onProgress: progress => {
        updateJob(jobId, { progress, stage: "GENERATING_IMAGES" });
      }
    });
    updateJob(jobId, { status: "COMPLETED", progress: 100, stage: "DONE" });
  } catch (error) {
    updateJob(jobId, { status: "FAILED", error: error.message });
  }
}

export async function runAudioJob({ jobId, projectId, voice, language }) {
  try {
    updateJob(jobId, { status: "RUNNING", stage: "GENERATING_AUDIO" });
    await generateAudioForProject({
      projectId,
      voice,
      language,
      onProgress: progress => {
        updateJob(jobId, { progress, stage: "GENERATING_AUDIO" });
      }
    });
    updateJob(jobId, { status: "COMPLETED", progress: 100, stage: "DONE" });
  } catch (error) {
    updateJob(jobId, { status: "FAILED", error: error.message });
  }
}

export async function runRenderJob({ jobId, projectId, options }) {
  try {
    updateJob(jobId, { status: "RUNNING", stage: "RENDERING" });
    const outputUrl = await renderProject({
      projectId,
      options,
      onProgress: progress => {
        updateJob(jobId, { progress, stage: "RENDERING" });
      }
    });
    updateJob(jobId, { status: "COMPLETED", progress: 100, stage: "DONE", outputUrl });
  } catch (error) {
    updateJob(jobId, { status: "FAILED", error: error.message });
  }
}
