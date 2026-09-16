export class AiProviderError extends Error {
  constructor(
    message,
    {
      provider,
      code = "AI_PROVIDER_ERROR",
      retryable = false,
      cause = undefined
    } = {}
  ) {
    super(message);
    this.name = "AiProviderError";
    this.provider = provider;
    this.code = code;
    this.retryable = retryable;
    this.cause = cause;
  }
}
