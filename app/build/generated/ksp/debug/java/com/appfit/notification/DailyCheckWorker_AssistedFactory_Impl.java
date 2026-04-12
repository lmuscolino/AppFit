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
public final class DailyCheckWorker_AssistedFactory_Impl implements DailyCheckWorker_AssistedFactory {
  private final DailyCheckWorker_Factory delegateFactory;

  DailyCheckWorker_AssistedFactory_Impl(DailyCheckWorker_Factory delegateFactory) {
    this.delegateFactory = delegateFactory;
  }

  @Override
  public DailyCheckWorker create(Context p0, WorkerParameters p1) {
    return delegateFactory.get(p0, p1);
  }

  public static Provider<DailyCheckWorker_AssistedFactory> create(
      DailyCheckWorker_Factory delegateFactory) {
    return InstanceFactory.create(new DailyCheckWorker_AssistedFactory_Impl(delegateFactory));
  }

  public static dagger.internal.Provider<DailyCheckWorker_AssistedFactory> createFactoryProvider(
      DailyCheckWorker_Factory delegateFactory) {
    return InstanceFactory.create(new DailyCheckWorker_AssistedFactory_Impl(delegateFactory));
  }
}
