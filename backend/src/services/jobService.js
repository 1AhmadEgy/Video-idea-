export const jobs = new Map();

export function createJob({ projectId, type }) {
  const jobId = `${type.toLowerCase()}-${Date.now()}`;
  const job = {
    jobId,
    projectId,
    type,
    status: "QUEUED",
    progress: 0,
    stage: "QUEUED",
    message: null,
    outputUrl: null,
    error: null
  };
  jobs.set(jobId, job);
  return job;
}

export function getJob(jobId) {
  return jobs.get(jobId);
}

export function updateJob(jobId, updates) {
  const job = jobs.get(jobId);
  if (!job) return null;
  Object.assign(job, updates);
  return job;
}
