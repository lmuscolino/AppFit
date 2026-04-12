package com.appfit.ui.profile;

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
public final class ProfileViewModel_Factory implements Factory<ProfileViewModel> {
  private final Provider<UserProfileProvider> userProfileProvider;

  private final Provider<ApiKeyProvider> apiKeyProvider;

  private final Provider<WorkManager> workManagerProvider;

  public ProfileViewModel_Factory(Provider<UserProfileProvider> userProfileProvider,
      Provider<ApiKeyProvider> apiKeyProvider, Provider<WorkManager> workManagerProvider) {
    this.userProfileProvider = userProfileProvider;
    this.apiKeyProvider = apiKeyProvider;
    this.workManagerProvider = workManagerProvider;
  }

  @Override
  public ProfileViewModel get() {
    return newInstance(userProfileProvider.get(), apiKeyProvider.get(), workManagerProvider.get());
  }

  public static ProfileViewModel_Factory create(Provider<UserProfileProvider> userProfileProvider,
      Provider<ApiKeyProvider> apiKeyProvider, Provider<WorkManager> workManagerProvider) {
    return new ProfileViewModel_Factory(userProfileProvider, apiKeyProvider, workManagerProvider);
  }

  public static ProfileViewModel newInstance(UserProfileProvider userProfileProvider,
      ApiKeyProvider apiKeyProvider, WorkManager workManager) {
    return new ProfileViewModel(userProfileProvider, apiKeyProvider, workManager);
  }
}
