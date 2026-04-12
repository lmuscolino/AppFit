package com.appfit.data.repository;

import com.appfit.data.local.dao.ActivityDao;
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
public final class ActivityRepository_Factory implements Factory<ActivityRepository> {
  private final Provider<ActivityDao> daoProvider;

  public ActivityRepository_Factory(Provider<ActivityDao> daoProvider) {
    this.daoProvider = daoProvider;
  }

  @Override
  public ActivityRepository get() {
    return newInstance(daoProvider.get());
  }

  public static ActivityRepository_Factory create(Provider<ActivityDao> daoProvider) {
    return new ActivityRepository_Factory(daoProvider);
  }

  public static ActivityRepository newInstance(ActivityDao dao) {
    return new ActivityRepository(dao);
  }
}
