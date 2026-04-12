package com.appfit.notification;

import android.content.Context;
import androidx.work.WorkerParameters;
import com.appfit.ai.UserProfileProvider;
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
public final class MonthlyUpdateWorker_Factory {
  private final Provider<UserProfileProvider> userProfileProvider;

  public MonthlyUpdateWorker_Factory(Provider<UserProfileProvider> userProfileProvider) {
    this.userProfileProvider = userProfileProvider;
  }

  public MonthlyUpdateWorker get(Context appContext, WorkerParameters workerParams) {
    return newInstance(appContext, workerParams, userProfileProvider.get());
  }

  public static MonthlyUpdateWorker_Factory create(
      Provider<UserProfileProvider> userProfileProvider) {
    return new MonthlyUpdateWorker_Factory(userProfileProvider);
  }

  public static MonthlyUpdateWorker newInstance(Context appContext, WorkerParameters workerParams,
      UserProfileProvider userProfileProvider) {
    return new MonthlyUpdateWorker(appContext, workerParams, userProfileProvider);
  }
}
