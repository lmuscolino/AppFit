package com.appfit.notification;

import android.content.Context;
import androidx.work.WorkerParameters;
import dagger.internal.DaggerGenerated;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;

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
public final class ActivityReminderWorker_Factory {
  public ActivityReminderWorker_Factory() {
  }

  public ActivityReminderWorker get(Context appContext, WorkerParameters workerParams) {
    return newInstance(appContext, workerParams);
  }

  public static ActivityReminderWorker_Factory create() {
    return new ActivityReminderWorker_Factory();
  }

  public static ActivityReminderWorker newInstance(Context appContext,
      WorkerParameters workerParams) {
    return new ActivityReminderWorker(appContext, workerParams);
  }
}
