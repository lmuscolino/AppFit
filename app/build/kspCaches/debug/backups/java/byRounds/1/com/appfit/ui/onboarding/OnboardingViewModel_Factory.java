package com.appfit.ui.onboarding;

import androidx.work.WorkManager;
import com.appfit.ai.ApiKeyProvider;
import com.appfit.ai.UserProfileProvider;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata
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
public final class OnboardingViewModel_Factory implements Factory<OnboardingViewModel> {
  private final Provider<ApiKeyProvider> apiKeyProvider;

  private final Provider<UserProfileProvider> userProfileProvider;

  private final Provider<WorkManager> workManagerProvider;

  public OnboardingViewModel_Factory(Provider<ApiKeyProvider> apiKeyProvider,
      Provider<UserProfileProvider> userProfileProvider,
      Provider<WorkManager> workManagerProvider) {
    this.apiKeyProvider = apiKeyProvider;
    this.userProfileProvider = userProfileProvider;
    this.workManagerProvider = workManagerProvider;
  }

  @Override
  public OnboardingViewModel get() {
    return newInstance(apiKeyProvider.get(), userProfileProvider.get(), workManagerProvider.get());
  }

  public static OnboardingViewModel_Factory create(Provider<ApiKeyProvider> apiKeyProvider,
      Provider<UserProfileProvider> userProfileProvider,
      Provider<WorkManager> workManagerProvider) {
    return new OnboardingViewModel_Factory(apiKeyProvider, userProfileProvider, workManagerProvider);
  }

  public static OnboardingViewModel newInstance(ApiKeyProvider apiKeyProvider,
      UserProfileProvider userProfileProvider, WorkManager workManager) {
    return new OnboardingViewModel(apiKeyProvider, userProfileProvider, workManager);
  }
}
