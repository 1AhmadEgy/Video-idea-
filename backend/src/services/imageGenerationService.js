export async function generateImagesForProject({ projectId, sceneIds, onProgress }) {
  for (let i = 0; i <= 100; i += 20) {
    await new Promise(resolve => setTimeout(resolve, 500));
    onProgress(i);
  }
}
