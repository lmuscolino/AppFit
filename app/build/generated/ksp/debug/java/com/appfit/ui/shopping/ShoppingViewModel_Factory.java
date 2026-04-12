package com.appfit.ui.shopping;

import com.appfit.data.repository.ShoppingRepository;
import com.appfit.domain.usecase.GenerateShoppingListUseCase;
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
public final class ShoppingViewModel_Factory implements Factory<ShoppingViewModel> {
  private final Provider<ShoppingRepository> shoppingRepositoryProvider;

  private final Provider<GenerateShoppingListUseCase> generateShoppingListUseCaseProvider;

  public ShoppingViewModel_Factory(Provider<ShoppingRepository> shoppingRepositoryProvider,
      Provider<GenerateShoppingListUseCase> generateShoppingListUseCaseProvider) {
    this.shoppingRepositoryProvider = shoppingRepositoryProvider;
    this.generateShoppingListUseCaseProvider = generateShoppingListUseCaseProvider;
  }

  @Override
  public ShoppingViewModel get() {
    return newInstance(shoppingRepositoryProvider.get(), generateShoppingListUseCaseProvider.get());
  }

  public static ShoppingViewModel_Factory create(
      Provider<ShoppingRepository> shoppingRepositoryProvider,
      Provider<GenerateShoppingListUseCase> generateShoppingListUseCaseProvider) {
    return new ShoppingViewModel_Factory(shoppingRepositoryProvider, generateShoppingListUseCaseProvider);
  }

  public static ShoppingViewModel newInstance(ShoppingRepository shoppingRepository,
      GenerateShoppingListUseCase generateShoppingListUseCase) {
    return new ShoppingViewModel(shoppingRepository, generateShoppingListUseCase);
  }
}
