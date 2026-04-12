package com.appfit.di;

import com.appfit.ai.AiService;
import com.appfit.ai.AnthropicService;
import com.appfit.ai.ApiKeyProvider;
import com.appfit.ai.GeminiService;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
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
public final class AiModule_ProvideAiServiceFactory implements Factory<AiService> {
  private final Provider<ApiKeyProvider> apiKeyProvider;

  private final Provider<AnthropicService> anthropicServiceProvider;

  private final Provider<GeminiService> geminiServiceProvider;

  public AiModule_ProvideAiServiceFactory(Provider<ApiKeyProvider> apiKeyProvider,
      Provider<AnthropicService> anthropicServiceProvider,
      Provider<GeminiService> geminiServiceProvider) {
    this.apiKeyProvider = apiKeyProvider;
    this.anthropicServiceProvider = anthropicServiceProvider;
    this.geminiServiceProvider = geminiServiceProvider;
  }

  @Override
  public AiService get() {
    return provideAiService(apiKeyProvider.get(), anthropicServiceProvider.get(), geminiServiceProvider.get());
  }

  public static AiModule_ProvideAiServiceFactory create(Provider<ApiKeyProvider> apiKeyProvider,
      Provider<AnthropicService> anthropicServiceProvider,
      Provider<GeminiService> geminiServiceProvider) {
    return new AiModule_ProvideAiServiceFactory(apiKeyProvider, anthropicServiceProvider, geminiServiceProvider);
  }

  public static AiService provideAiService(ApiKeyProvider apiKeyProvider,
      AnthropicService anthropicService, GeminiService geminiService) {
    return Preconditions.checkNotNullFromProvides(AiModule.INSTANCE.provideAiService(apiKeyProvider, anthropicService, geminiService));
  }
}
