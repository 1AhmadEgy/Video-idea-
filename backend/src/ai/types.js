/**
 * @typedef {Object} GenerateScenesInput
 * @property {string} projectId
 * @property {string} idea
 * @property {string} language
 * @property {string} template
 * @property {string} aspectRatio
 * @property {number} durationSeconds
 * @property {number} sceneCount
 */

/**
 * @typedef {Object} GeneratedScene
 * @property {number} position
 * @property {string} title
 * @property {string} narration
 * @property {string} visualPrompt
 * @property {number} durationSeconds
 */

/**
 * @typedef {Object} GeneratedScenesResult
 * @property {GeneratedScene[]} scenes
 * @property {string} provider
 * @property {string} model
 */
