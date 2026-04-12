package com.appfit.ui.workouts;

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
public final class WorkoutsViewModel_Factory implements Factory<WorkoutsViewModel> {
  private final Provider<ActivityRepository> repositoryProvider;

  public WorkoutsViewModel_Factory(Provider<ActivityRepository> repositoryProvider) {
    this.repositoryProvider = repositoryProvider;
  }

  @Override
  public WorkoutsViewModel get() {
    return newInstance(repositoryProvider.get());
  }

  public static WorkoutsViewModel_Factory create(Provider<ActivityRepository> repositoryProvider) {
    return new WorkoutsViewModel_Factory(repositoryProvider);
  }

  public static WorkoutsViewModel newInstance(ActivityRepository repository) {
    return new WorkoutsViewModel(repository);
  }
}
