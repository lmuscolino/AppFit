package com.appfit.di;

import com.appfit.data.local.AppDatabase;
import com.appfit.data.local.dao.ActivityDao;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
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
public final class DatabaseModule_ProvideActivityDaoFactory implements Factory<ActivityDao> {
  private final Provider<AppDatabase> dbProvider;

  public DatabaseModule_ProvideActivityDaoFactory(Provider<AppDatabase> dbProvider) {
    this.dbProvider = dbProvider;
  }

  @Override
  public ActivityDao get() {
    return provideActivityDao(dbProvider.get());
  }

  public static DatabaseModule_ProvideActivityDaoFactory create(Provider<AppDatabase> dbProvider) {
    return new DatabaseModule_ProvideActivityDaoFactory(dbProvider);
  }

  public static ActivityDao provideActivityDao(AppDatabase db) {
    return Preconditions.checkNotNullFromProvides(DatabaseModule.INSTANCE.provideActivityDao(db));
  }
}
