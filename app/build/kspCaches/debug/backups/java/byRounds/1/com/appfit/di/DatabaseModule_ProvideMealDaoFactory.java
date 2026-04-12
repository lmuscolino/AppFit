package com.appfit.di;

import com.appfit.data.local.AppDatabase;
import com.appfit.data.local.dao.MealDao;
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
public final class DatabaseModule_ProvideMealDaoFactory implements Factory<MealDao> {
  private final Provider<AppDatabase> dbProvider;

  public DatabaseModule_ProvideMealDaoFactory(Provider<AppDatabase> dbProvider) {
    this.dbProvider = dbProvider;
  }

  @Override
  public MealDao get() {
    return provideMealDao(dbProvider.get());
  }

  public static DatabaseModule_ProvideMealDaoFactory create(Provider<AppDatabase> dbProvider) {
    return new DatabaseModule_ProvideMealDaoFactory(dbProvider);
  }

  public static MealDao provideMealDao(AppDatabase db) {
    return Preconditions.checkNotNullFromProvides(DatabaseModule.INSTANCE.provideMealDao(db));
  }
}
