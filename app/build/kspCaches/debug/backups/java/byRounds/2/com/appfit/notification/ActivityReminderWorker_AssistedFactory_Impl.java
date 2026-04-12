package com.appfit.notification;

import android.content.Context;
import androidx.work.WorkerParameters;
import dagger.internal.DaggerGenerated;
import dagger.internal.InstanceFactory;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

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
public final class ActivityReminderWorker_AssistedFactory_Impl implements ActivityReminderWorker_AssistedFactory {
  private final ActivityReminderWorker_Factory delegateFactory;

  ActivityReminderWorker_AssistedFactory_Impl(ActivityReminderWorker_Factory delegateFactory) {
    this.delegateFactory = delegateFactory;
  }

  @Override
  public ActivityReminderWorker create(Context p0, WorkerParameters p1) {
    return delegateFactory.get(p0, p1);
  }

  public static Provider<ActivityReminderWorker_AssistedFactory> create(
      ActivityReminderWorker_Factory delegateFactory) {
    return InstanceFactory.create(new ActivityReminderWorker_AssistedFactory_Impl(delegateFactory));
  }

  public static dagger.internal.Provider<ActivityReminderWorker_AssistedFactory> createFactoryProvider(
      ActivityReminderWorker_Factory delegateFactory) {
    return InstanceFactory.create(new ActivityReminderWorker_AssistedFactory_Impl(delegateFactory));
  }
}
