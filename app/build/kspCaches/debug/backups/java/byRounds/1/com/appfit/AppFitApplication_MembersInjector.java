package com.appfit;

import androidx.hilt.work.HiltWorkerFactory;
import com.appfit.notification.NotificationChannels;
import dagger.MembersInjector;
import dagger.internal.DaggerGenerated;
import dagger.internal.InjectedFieldSignature;
import dagger.internal.QualifierMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

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
public final class AppFitApplication_MembersInjector implements MembersInjector<AppFitApplication> {
  private final Provider<HiltWorkerFactory> workerFactoryProvider;

  private final Provider<NotificationChannels> notificationChannelsProvider;

  public AppFitApplication_MembersInjector(Provider<HiltWorkerFactory> workerFactoryProvider,
      Provider<NotificationChannels> notificationChannelsProvider) {
    this.workerFactoryProvider = workerFactoryProvider;
    this.notificationChannelsProvider = notificationChannelsProvider;
  }

  public static MembersInjector<AppFitApplication> create(
      Provider<HiltWorkerFactory> workerFactoryProvider,
      Provider<NotificationChannels> notificationChannelsProvider) {
    return new AppFitApplication_MembersInjector(workerFactoryProvider, notificationChannelsProvider);
  }

  @Override
  public void injectMembers(AppFitApplication instance) {
    injectWorkerFactory(instance, workerFactoryProvider.get());
    injectNotificationChannels(instance, notificationChannelsProvider.get());
  }

  @InjectedFieldSignature("com.appfit.AppFitApplication.workerFactory")
  public static void injectWorkerFactory(AppFitApplication instance,
      HiltWorkerFactory workerFactory) {
    instance.workerFactory = workerFactory;
  }

  @InjectedFieldSignature("com.appfit.AppFitApplication.notificationChannels")
  public static void injectNotificationChannels(AppFitApplication instance,
      NotificationChannels notificationChannels) {
    instance.notificationChannels = notificationChannels;
  }
}
