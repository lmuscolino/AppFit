package com.appfit.domain.usecase;

import com.appfit.data.repository.ActivityRepository;
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
public final class GetDailyPlanUseCase_Factory implements Factory<GetDailyPlanUseCase> {
  private final Provider<ActivityRepository> activityRepositoryProvider;

  private final Provider<DietRepository> dietRepositoryProvider;

  public GetDailyPlanUseCase_Factory(Provider<ActivityRepository> activityRepositoryProvider,
      Provider<DietRepository> dietRepositoryProvider) {
    this.activityRepositoryProvider = activityRepositoryProvider;
    this.dietRepositoryProvider = dietRepositoryProvider;
  }

  @Override
  public GetDailyPlanUseCase get() {
    return newInstance(activityRepositoryProvider.get(), dietRepositoryProvider.get());
  }

  public static GetDailyPlanUseCase_Factory create(
      Provider<ActivityRepository> activityRepositoryProvider,
      Provider<DietRepository> dietRepositoryProvider) {
    return new GetDailyPlanUseCase_Factory(activityRepositoryProvider, dietRepositoryProvider);
  }

  public static GetDailyPlanUseCase newInstance(ActivityRepository activityRepository,
      DietRepository dietRepository) {
    return new GetDailyPlanUseCase(activityRepository, dietRepository);
  }
}
