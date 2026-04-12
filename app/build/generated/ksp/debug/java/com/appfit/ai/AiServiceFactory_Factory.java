package com.appfit.ai;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata("javax.inject.Singleton")
@QualifierMetadata
@DaggerGenerated
@Generated(
    value = "dagger.internal.codegen.ComponentProcessor",
    comments = "https://dagger.dev"
)
@SuppressWarnings({
    "unchecked",
    "rawtypes",
    "KotlinInternal",
    "KotlinInternalInJava",
    "cast"
})
public final class AiServiceFactory_Factory implements Factory<AiServiceFactory> {
  private final Provider<ApiKeyProvider> apiKeyProvider;

  private final Provider<AnthropicService> anthropicServiceProvider;

  private final Provider<GeminiService> geminiServiceProvider;

  public AiServiceFactory_Factory(Provider<ApiKeyProvider> apiKeyProvider,
      Provider<AnthropicService> anthropicServiceProvider,
      Provider<GeminiService> geminiServiceProvider) {
    this.apiKeyProvider = apiKeyProvider;
    this.anthropicServiceProvider = anthropicServiceProvider;
    this.geminiServiceProvider = geminiServiceProvider;
  }

  @Override
  public AiServiceFactory get() {
    return newInstance(apiKeyProvider.get(), anthropicServiceProvider.get(), geminiServiceProvider.get());
  }

  public static AiServiceFactory_Factory create(Provider<ApiKeyProvider> apiKeyProvider,
      Provider<AnthropicService> anthropicServiceProvider,
      Provider<GeminiService> geminiServiceProvider) {
    return new AiServiceFactory_Factory(apiKeyProvider, anthropicServiceProvider, geminiServiceProvider);
  }

  public static AiServiceFactory newInstance(ApiKeyProvider apiKeyProvider,
      AnthropicService anthropicService, GeminiService geminiService) {
    return new AiServiceFactory(apiKeyProvider, anthropicService, geminiService);
  }
}
