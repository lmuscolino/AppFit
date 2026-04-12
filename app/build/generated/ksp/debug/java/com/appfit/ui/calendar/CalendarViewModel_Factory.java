package com.appfit.ui.calendar;

import com.appfit.data.repository.ActivityRepository;
import com.appfit.data.repository.DietRepository;
import com.appfit.domain.usecase.GetDailyPlanUseCase;
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
public final class CalendarViewModel_Factory implements Factory<CalendarViewModel> {
  private final Provider<GetDailyPlanUseCase> getDailyPlanUseCaseProvider;

  private final Provider<ActivityRepository> activityRepositoryProvider;

  private final Provider<DietRepository> dietRepositoryProvider;

  public CalendarViewModel_Factory(Provider<GetDailyPlanUseCase> getDailyPlanUseCaseProvider,
      Provider<ActivityRepository> activityRepositoryProvider,
      Provider<DietRepository> dietRepositoryProvider) {
    this.getDailyPlanUseCaseProvider = getDailyPlanUseCaseProvider;
    this.activityRepositoryProvider = activityRepositoryProvider;
    this.dietRepositoryProvider = dietRepositoryProvider;
  }

  @Override
  public CalendarViewModel get() {
    return newInstance(getDailyPlanUseCaseProvider.get(), activityRepositoryProvider.get(), dietRepositoryProvider.get());
  }

  public static CalendarViewModel_Factory create(
      Provider<GetDailyPlanUseCase> getDailyPlanUseCaseProvider,
      Provider<ActivityRepository> activityRepositoryProvider,
      Provider<DietRepository> dietRepositoryProvider) {
    return new CalendarViewModel_Factory(getDailyPlanUseCaseProvider, activityRepositoryProvider, dietRepositoryProvider);
  }

  public static CalendarViewModel newInstance(GetDailyPlanUseCase getDailyPlanUseCase,
      ActivityRepository activityRepository, DietRepository dietRepository) {
    return new CalendarViewModel(getDailyPlanUseCase, activityRepository, dietRepository);
  }
}
