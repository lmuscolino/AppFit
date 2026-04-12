package com.appfit.notification;

import android.content.Context;
import androidx.work.WorkerParameters;
import dagger.internal.DaggerGenerated;
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
public final class DailyCheckWorker_Factory {
  private final Provider<NotificationScheduler> notificationSchedulerProvider;

  public DailyCheckWorker_Factory(Provider<NotificationScheduler> notificationSchedulerProvider) {
    this.notificationSchedulerProvider = notificationSchedulerProvider;
  }

  public DailyCheckWorker get(Context appContext, WorkerParameters workerParams) {
    return newInstance(appContext, workerParams, notificationSchedulerProvider.get());
  }

  public static DailyCheckWorker_Factory create(
      Provider<NotificationScheduler> notificationSchedulerProvider) {
    return new DailyCheckWorker_Factory(notificationSchedulerProvider);
  }

  public static DailyCheckWorker newInstance(Context appContext, WorkerParameters workerParams,
      NotificationScheduler notificationScheduler) {
    return new DailyCheckWorker(appContext, workerParams, notificationScheduler);
  }
}
