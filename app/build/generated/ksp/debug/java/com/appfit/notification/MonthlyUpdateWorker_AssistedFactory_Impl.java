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
public final class MonthlyUpdateWorker_AssistedFactory_Impl implements MonthlyUpdateWorker_AssistedFactory {
  private final MonthlyUpdateWorker_Factory delegateFactory;

  MonthlyUpdateWorker_AssistedFactory_Impl(MonthlyUpdateWorker_Factory delegateFactory) {
    this.delegateFactory = delegateFactory;
  }

  @Override
  public MonthlyUpdateWorker create(Context p0, WorkerParameters p1) {
    return delegateFactory.get(p0, p1);
  }

  public static Provider<MonthlyUpdateWorker_AssistedFactory> create(
      MonthlyUpdateWorker_Factory delegateFactory) {
    return InstanceFactory.create(new MonthlyUpdateWorker_AssistedFactory_Impl(delegateFactory));
  }

  public static dagger.internal.Provider<MonthlyUpdateWorker_AssistedFactory> createFactoryProvider(
      MonthlyUpdateWorker_Factory delegateFactory) {
    return InstanceFactory.create(new MonthlyUpdateWorker_AssistedFactory_Impl(delegateFactory));
  }
}
