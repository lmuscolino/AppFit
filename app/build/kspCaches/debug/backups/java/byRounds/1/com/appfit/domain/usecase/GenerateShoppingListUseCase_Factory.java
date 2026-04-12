package com.appfit.domain.usecase;

import com.appfit.data.repository.DietRepository;
import com.appfit.data.repository.ShoppingRepository;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
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
public final class GenerateShoppingListUseCase_Factory implements Factory<GenerateShoppingListUseCase> {
  private final Provider<DietRepository> dietRepositoryProvider;

  private final Provider<ShoppingRepository> shoppingRepositoryProvider;

  public GenerateShoppingListUseCase_Factory(Provider<DietRepository> dietRepositoryProvider,
      Provider<ShoppingRepository> shoppingRepositoryProvider) {
    this.dietRepositoryProvider = dietRepositoryProvider;
    this.shoppingRepositoryProvider = shoppingRepositoryProvider;
  }

  @Override
  public GenerateShoppingListUseCase get() {
    return newInstance(dietRepositoryProvider.get(), shoppingRepositoryProvider.get());
  }

  public static GenerateShoppingListUseCase_Factory create(
      Provider<DietRepository> dietRepositoryProvider,
      Provider<ShoppingRepository> shoppingRepositoryProvider) {
    return new GenerateShoppingListUseCase_Factory(dietRepositoryProvider, shoppingRepositoryProvider);
  }

  public static GenerateShoppingListUseCase newInstance(DietRepository dietRepository,
      ShoppingRepository shoppingRepository) {
    return new GenerateShoppingListUseCase(dietRepository, shoppingRepository);
  }
}
