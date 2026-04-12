package com.appfit.ui.diet;

import androidx.lifecycle.ViewModel;
import com.appfit.data.model.DailyPlan;
import com.appfit.domain.usecase.GetDailyPlanUseCase;
import com.appfit.ui.common.UiState;
import dagger.hilt.android.lifecycle.HiltViewModel;
import kotlinx.coroutines.flow.*;
import java.time.LocalDate;
import javax.inject.Inject;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\"\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\b\u0007\u0018\u00002\u00020\u0001B\u000f\b\u0007\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u001d\u0010\u0005\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\b0\u00070\u0006\u00a2\u0006\b\n\u0000\u001a\u0004\b\t\u0010\n\u00a8\u0006\u000b"}, d2 = {"Lcom/appfit/ui/diet/DietViewModel;", "Landroidx/lifecycle/ViewModel;", "getDailyPlanUseCase", "Lcom/appfit/domain/usecase/GetDailyPlanUseCase;", "(Lcom/appfit/domain/usecase/GetDailyPlanUseCase;)V", "todayPlan", "Lkotlinx/coroutines/flow/StateFlow;", "Lcom/appfit/ui/common/UiState;", "Lcom/appfit/data/model/DailyPlan;", "getTodayPlan", "()Lkotlinx/coroutines/flow/StateFlow;", "app_debug"})
@dagger.hilt.android.lifecycle.HiltViewModel()
public final class DietViewModel extends androidx.lifecycle.ViewModel {
    @org.jetbrains.annotations.NotNull()
    private final com.appfit.domain.usecase.GetDailyPlanUseCase getDailyPlanUseCase = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.StateFlow<com.appfit.ui.common.UiState<com.appfit.data.model.DailyPlan>> todayPlan = null;
    
    @javax.inject.Inject()
    public DietViewModel(@org.jetbrains.annotations.NotNull()
    com.appfit.domain.usecase.GetDailyPlanUseCase getDailyPlanUseCase) {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.StateFlow<com.appfit.ui.common.UiState<com.appfit.data.model.DailyPlan>> getTodayPlan() {
        return null;
    }
}