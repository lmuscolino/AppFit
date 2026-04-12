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
public final class ClaudeToolExecutor_Factory implements Factory<ClaudeToolExecutor> {
  private final Provider<ActivityRepository> activityRepositoryProvider;

  private final Provider<DietRepository> dietRepositoryProvider;

  private final Provider<UserProfileProvider> userProfileProvider;

  public ClaudeToolExecutor_Factory(Provider<ActivityRepository> activityRepositoryProvider,
      Provider<DietRepository> dietRepositoryProvider,
      Provider<UserProfileProvider> userProfileProvider) {
    this.activityRepositoryProvider = activityRepositoryProvider;
    this.dietRepositoryProvider = dietRepositoryProvider;
    this.userProfileProvider = userProfileProvider;
  }

  @Override
  public ClaudeToolExecutor get() {
    return newInstance(activityRepositoryProvider.get(), dietRepositoryProvider.get(), userProfileProvider.get());
  }

  public static ClaudeToolExecutor_Factory create(
      Provider<ActivityRepository> activityRepositoryProvider,
      Provider<DietRepository> dietRepositoryProvider,
      Provider<UserProfileProvider> userProfileProvider) {
    return new ClaudeToolExecutor_Factory(activityRepositoryProvider, dietRepositoryProvider, userProfileProvider);
  }

  public static ClaudeToolExecutor newInstance(ActivityRepository activityRepository,
      DietRepository dietRepository, UserProfileProvider userProfileProvider) {
    return new ClaudeToolExecutor(activityRepository, dietRepository, userProfileProvider);
  }
}
