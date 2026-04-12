package com.appfit.di;

import com.appfit.data.local.AppDatabase;
import com.appfit.data.local.dao.ShoppingItemDao;
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
public final class DatabaseModule_ProvideShoppingItemDaoFactory implements Factory<ShoppingItemDao> {
  private final Provider<AppDatabase> dbProvider;

  public DatabaseModule_ProvideShoppingItemDaoFactory(Provider<AppDatabase> dbProvider) {
    this.dbProvider = dbProvider;
  }

  @Override
  public ShoppingItemDao get() {
    return provideShoppingItemDao(dbProvider.get());
  }

  public static DatabaseModule_ProvideShoppingItemDaoFactory create(
      Provider<AppDatabase> dbProvider) {
    return new DatabaseModule_ProvideShoppingItemDaoFactory(dbProvider);
  }

  public static ShoppingItemDao provideShoppingItemDao(AppDatabase db) {
    return Preconditions.checkNotNullFromProvides(DatabaseModule.INSTANCE.provideShoppingItemDao(db));
  }
}
