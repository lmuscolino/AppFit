package com.appfit.domain.usecase;

import com.appfit.data.model.DailyPlan;
import com.appfit.data.repository.ActivityRepository;
import com.appfit.data.repository.DietRepository;
import kotlinx.coroutines.flow.Flow;
import java.time.LocalDate;
import javax.inject.Inject;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000(\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\b\u0007\u0018\u00002\u00020\u0001B\u0017\b\u0007\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u00a2\u0006\u0002\u0010\u0006J\u0017\u0010\u0007\u001a\b\u0012\u0004\u0012\u00020\t0\b2\u0006\u0010\n\u001a\u00020\u000bH\u0086\u0002R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\f"}, d2 = {"Lcom/appfit/domain/usecase/GetDailyPlanUseCase;", "", "activityRepository", "Lcom/appfit/data/repository/ActivityRepository;", "dietRepository", "Lcom/appfit/data/repository/DietRepository;", "(Lcom/appfit/data/repository/ActivityRepository;Lcom/appfit/data/repository/DietRepository;)V", "invoke", "Lkotlinx/coroutines/flow/Flow;", "Lcom/appfit/data/model/DailyPlan;", "date", "Ljava/time/LocalDate;", "app_debug"})
public final class GetDailyPlanUseCase {
    @org.jetbrains.annotations.NotNull()
    private final com.appfit.data.repository.ActivityRepository activityRepository = null;
    @org.jetbrains.annotations.NotNull()
    private final com.appfit.data.repository.DietRepository dietRepository = null;
    
    @javax.inject.Inject()
    public GetDailyPlanUseCase(@org.jetbrains.annotations.NotNull()
    com.appfit.data.repository.ActivityRepository activityRepository, @org.jetbrains.annotations.NotNull()
    com.appfit.data.repository.DietRepository dietRepository) {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.Flow<com.appfit.data.model.DailyPlan> invoke(@org.jetbrains.annotations.NotNull()
    java.time.LocalDate date) {
        return null;
    }
}