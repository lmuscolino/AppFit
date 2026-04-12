package com.appfit.ui.diet;

import androidx.lifecycle.SavedStateHandle;
import com.appfit.data.repository.DietRepository;
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
public final class MealDetailViewModel_Factory implements Factory<MealDetailViewModel> {
  private final Provider<DietRepository> repositoryProvider;

  private final Provider<SavedStateHandle> savedStateHandleProvider;

  public MealDetailViewModel_Factory(Provider<DietRepository> repositoryProvider,
      Provider<SavedStateHandle> savedStateHandleProvider) {
    this.repositoryProvider = repositoryProvider;
    this.savedStateHandleProvider = savedStateHandleProvider;
  }

  @Override
  public MealDetailViewModel get() {
    return newInstance(repositoryProvider.get(), savedStateHandleProvider.get());
  }

  public static MealDetailViewModel_Factory create(Provider<DietRepository> repositoryProvider,
      Provider<SavedStateHandle> savedStateHandleProvider) {
    return new MealDetailViewModel_Factory(repositoryProvider, savedStateHandleProvider);
  }

  public static MealDetailViewModel newInstance(DietRepository repository,
      SavedStateHandle savedStateHandle) {
    return new MealDetailViewModel(repository, savedStateHandle);
  }
}
