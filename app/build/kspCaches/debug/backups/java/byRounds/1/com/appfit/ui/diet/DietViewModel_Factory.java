package com.appfit.ui.diet;

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
public final class DietViewModel_Factory implements Factory<DietViewModel> {
  private final Provider<GetDailyPlanUseCase> getDailyPlanUseCaseProvider;

  public DietViewModel_Factory(Provider<GetDailyPlanUseCase> getDailyPlanUseCaseProvider) {
    this.getDailyPlanUseCaseProvider = getDailyPlanUseCaseProvider;
  }

  @Override
  public DietViewModel get() {
    return newInstance(getDailyPlanUseCaseProvider.get());
  }

  public static DietViewModel_Factory create(
      Provider<GetDailyPlanUseCase> getDailyPlanUseCaseProvider) {
    return new DietViewModel_Factory(getDailyPlanUseCaseProvider);
  }

  public static DietViewModel newInstance(GetDailyPlanUseCase getDailyPlanUseCase) {
    return new DietViewModel(getDailyPlanUseCase);
  }
}
