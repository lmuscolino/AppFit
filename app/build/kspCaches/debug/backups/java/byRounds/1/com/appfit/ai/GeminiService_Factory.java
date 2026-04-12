package com.appfit.ai;

import com.appfit.data.repository.ActivityRepository;
import com.appfit.data.repository.DietRepository;
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
public final class GeminiService_Factory implements Factory<GeminiService> {
  private final Provider<ApiKeyProvider> apiKeyProvider;

  private final Provider<AiDebugLogger> debugLoggerProvider;

  private final Provider<ToolApprovalManager> toolApprovalManagerProvider;

  private final Provider<ActivityRepository> activityRepositoryProvider;

  private final Provider<DietRepository> dietRepositoryProvider;

  private final Provider<UserProfileProvider> userProfileProvider;

  public GeminiService_Factory(Provider<ApiKeyProvider> apiKeyProvider,
      Provider<AiDebugLogger> debugLoggerProvider,
      Provider<ToolApprovalManager> toolApprovalManagerProvider,
      Provider<ActivityRepository> activityRepositoryProvider,
      Provider<DietRepository> dietRepositoryProvider,
      Provider<UserProfileProvider> userProfileProvider) {
    this.apiKeyProvider = apiKeyProvider;
    this.debugLoggerProvider = debugLoggerProvider;
    this.toolApprovalManagerProvider = toolApprovalManagerProvider;
    this.activityRepositoryProvider = activityRepositoryProvider;
    this.dietRepositoryProvider = dietRepositoryProvider;
    this.userProfileProvider = userProfileProvider;
  }

  @Override
  public GeminiService get() {
    return newInstance(apiKeyProvider.get(), debugLoggerProvider.get(), toolApprovalManagerProvider.get(), activityRepositoryProvider.get(), dietRepositoryProvider.get(), userProfileProvider.get());
  }

  public static GeminiService_Factory create(Provider<ApiKeyProvider> apiKeyProvider,
      Provider<AiDebugLogger> debugLoggerProvider,
      Provider<ToolApprovalManager> toolApprovalManagerProvider,
      Provider<ActivityRepository> activityRepositoryProvider,
      Provider<DietRepository> dietRepositoryProvider,
      Provider<UserProfileProvider> userProfileProvider) {
    return new GeminiService_Factory(apiKeyProvider, debugLoggerProvider, toolApprovalManagerProvider, activityRepositoryProvider, dietRepositoryProvider, userProfileProvider);
  }

  public static GeminiService newInstance(ApiKeyProvider apiKeyProvider, AiDebugLogger debugLogger,
      ToolApprovalManager toolApprovalManager, ActivityRepository activityRepository,
      DietRepository dietRepository, UserProfileProvider userProfileProvider) {
    return new GeminiService(apiKeyProvider, debugLogger, toolApprovalManager, activityRepository, dietRepository, userProfileProvider);
  }
}
