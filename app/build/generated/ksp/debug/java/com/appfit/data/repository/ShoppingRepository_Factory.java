package com.appfit.data.repository;

import com.appfit.data.local.dao.ShoppingItemDao;
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
public final class ShoppingRepository_Factory implements Factory<ShoppingRepository> {
  private final Provider<ShoppingItemDao> daoProvider;

  public ShoppingRepository_Factory(Provider<ShoppingItemDao> daoProvider) {
    this.daoProvider = daoProvider;
  }

  @Override
  public ShoppingRepository get() {
    return newInstance(daoProvider.get());
  }

  public static ShoppingRepository_Factory create(Provider<ShoppingItemDao> daoProvider) {
    return new ShoppingRepository_Factory(daoProvider);
  }

  public static ShoppingRepository newInstance(ShoppingItemDao dao) {
    return new ShoppingRepository(dao);
  }
}
