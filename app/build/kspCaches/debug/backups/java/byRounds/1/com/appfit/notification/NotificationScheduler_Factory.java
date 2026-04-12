package com.appfit.notification;

import androidx.work.WorkManager;
import com.appfit.data.repository.ActivityRepository;
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
public final class NotificationScheduler_Factory implements Factory<NotificationScheduler> {
  private final Provider<WorkManager> workManagerProvider;

  private final Provider<ActivityRepository> activityRepositoryProvider;

  public NotificationScheduler_Factory(Provider<WorkManager> workManagerProvider,
      Provider<ActivityRepository> activityRepositoryProvider) {
    this.workManagerProvider = workManagerProvider;
    this.activityRepositoryProvider = activityRepositoryProvider;
  }

  @Override
  public NotificationScheduler get() {
    return newInstance(workManagerProvider.get(), activityRepositoryProvider.get());
  }

  public static NotificationScheduler_Factory create(Provider<WorkManager> workManagerProvider,
      Provider<ActivityRepository> activityRepositoryProvider) {
    return new NotificationScheduler_Factory(workManagerProvider, activityRepositoryProvider);
  }

  public static NotificationScheduler newInstance(WorkManager workManager,
      ActivityRepository activityRepository) {
    return new NotificationScheduler(workManager, activityRepository);
  }
}
