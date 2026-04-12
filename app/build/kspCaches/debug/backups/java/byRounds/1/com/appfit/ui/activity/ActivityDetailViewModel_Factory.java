package com.appfit.ui.activity;

import androidx.lifecycle.SavedStateHandle;
import com.appfit.data.repository.ActivityRepository;
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
public final class ActivityDetailViewModel_Factory implements Factory<ActivityDetailViewModel> {
  private final Provider<ActivityRepository> repositoryProvider;

  private final Provider<SavedStateHandle> savedStateHandleProvider;

  public ActivityDetailViewModel_Factory(Provider<ActivityRepository> repositoryProvider,
      Provider<SavedStateHandle> savedStateHandleProvider) {
    this.repositoryProvider = repositoryProvider;
    this.savedStateHandleProvider = savedStateHandleProvider;
  }

  @Override
  public ActivityDetailViewModel get() {
    return newInstance(repositoryProvider.get(), savedStateHandleProvider.get());
  }

  public static ActivityDetailViewModel_Factory create(
      Provider<ActivityRepository> repositoryProvider,
      Provider<SavedStateHandle> savedStateHandleProvider) {
    return new ActivityDetailViewModel_Factory(repositoryProvider, savedStateHandleProvider);
  }

  public static ActivityDetailViewModel newInstance(ActivityRepository repository,
      SavedStateHandle savedStateHandle) {
    return new ActivityDetailViewModel(repository, savedStateHandle);
  }
}
