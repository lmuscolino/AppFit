package com.appfit.data.repository;

import com.appfit.data.local.dao.DietPlanDao;
import com.appfit.data.local.dao.MealDao;
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
public final class DietRepository_Factory implements Factory<DietRepository> {
  private final Provider<MealDao> mealDaoProvider;

  private final Provider<DietPlanDao> dietPlanDaoProvider;

  public DietRepository_Factory(Provider<MealDao> mealDaoProvider,
      Provider<DietPlanDao> dietPlanDaoProvider) {
    this.mealDaoProvider = mealDaoProvider;
    this.dietPlanDaoProvider = dietPlanDaoProvider;
  }

  @Override
  public DietRepository get() {
    return newInstance(mealDaoProvider.get(), dietPlanDaoProvider.get());
  }

  public static DietRepository_Factory create(Provider<MealDao> mealDaoProvider,
      Provider<DietPlanDao> dietPlanDaoProvider) {
    return new DietRepository_Factory(mealDaoProvider, dietPlanDaoProvider);
  }

  public static DietRepository newInstance(MealDao mealDao, DietPlanDao dietPlanDao) {
    return new DietRepository(mealDao, dietPlanDao);
  }
}
