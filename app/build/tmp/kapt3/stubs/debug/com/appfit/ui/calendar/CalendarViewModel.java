package com.appfit.ui.calendar;

import androidx.lifecycle.ViewModel;
import com.appfit.data.model.DailyPlan;
import com.appfit.data.repository.ActivityRepository;
import com.appfit.data.repository.DietRepository;
import com.appfit.domain.usecase.GetDailyPlanUseCase;
import com.appfit.ui.common.UiState;
import dagger.hilt.android.lifecycle.HiltViewModel;
import kotlinx.coroutines.ExperimentalCoroutinesApi;
import kotlinx.coroutines.flow.*;
import java.time.LocalDate;
import java.time.YearMonth;
import javax.inject.Inject;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000b\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0010\"\n\u0002\b\u0004\n\u0002\u0010\u0002\n\u0002\b\u0005\n\u0002\u0010\t\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0002\b\u0007\u0018\u00002\u00020\u0001B\u001f\b\u0007\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\u0006\u0010\u0006\u001a\u00020\u0007\u00a2\u0006\u0002\u0010\bJ\u000e\u0010\u001e\u001a\u00020\u001f2\u0006\u0010 \u001a\u00020\u000eJ\u000e\u0010!\u001a\u00020\u001f2\u0006\u0010\"\u001a\u00020\u000bJ\u0016\u0010#\u001a\u00020\u001f2\u0006\u0010$\u001a\u00020%2\u0006\u0010&\u001a\u00020\'J\u0016\u0010(\u001a\u00020\u001f2\u0006\u0010$\u001a\u00020%2\u0006\u0010&\u001a\u00020\'R\u001c\u0010\t\u001a\u0010\u0012\f\u0012\n \f*\u0004\u0018\u00010\u000b0\u000b0\nX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u001c\u0010\r\u001a\u0010\u0012\f\u0012\n \f*\u0004\u0018\u00010\u000e0\u000e0\nX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0017\u0010\u000f\u001a\b\u0012\u0004\u0012\u00020\u000b0\u0010\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0011\u0010\u0012R#\u0010\u0013\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00150\u00140\u0010\u00a2\u0006\u000e\n\u0000\u0012\u0004\b\u0016\u0010\u0017\u001a\u0004\b\u0018\u0010\u0012R\u001d\u0010\u0019\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u000e0\u001a0\u0010\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001b\u0010\u0012R\u000e\u0010\u0006\u001a\u00020\u0007X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0017\u0010\u001c\u001a\b\u0012\u0004\u0012\u00020\u000e0\u0010\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001d\u0010\u0012\u00a8\u0006)"}, d2 = {"Lcom/appfit/ui/calendar/CalendarViewModel;", "Landroidx/lifecycle/ViewModel;", "getDailyPlanUseCase", "Lcom/appfit/domain/usecase/GetDailyPlanUseCase;", "activityRepository", "Lcom/appfit/data/repository/ActivityRepository;", "dietRepository", "Lcom/appfit/data/repository/DietRepository;", "(Lcom/appfit/domain/usecase/GetDailyPlanUseCase;Lcom/appfit/data/repository/ActivityRepository;Lcom/appfit/data/repository/DietRepository;)V", "_currentMonth", "Lkotlinx/coroutines/flow/MutableStateFlow;", "Ljava/time/YearMonth;", "kotlin.jvm.PlatformType", "_selectedDate", "Ljava/time/LocalDate;", "currentMonth", "Lkotlinx/coroutines/flow/StateFlow;", "getCurrentMonth", "()Lkotlinx/coroutines/flow/StateFlow;", "dailyPlanState", "Lcom/appfit/ui/common/UiState;", "Lcom/appfit/data/model/DailyPlan;", "getDailyPlanState$annotations", "()V", "getDailyPlanState", "datesWithContent", "", "getDatesWithContent", "selectedDate", "getSelectedDate", "onDateSelected", "", "date", "onMonthChanged", "month", "toggleActivityCompleted", "id", "", "currentState", "", "toggleMealConsumed", "app_debug"})
@dagger.hilt.android.lifecycle.HiltViewModel()
public final class CalendarViewModel extends androidx.lifecycle.ViewModel {
    @org.jetbrains.annotations.NotNull()
    private final com.appfit.domain.usecase.GetDailyPlanUseCase getDailyPlanUseCase = null;
    @org.jetbrains.annotations.NotNull()
    private final com.appfit.data.repository.ActivityRepository activityRepository = null;
    @org.jetbrains.annotations.NotNull()
    private final com.appfit.data.repository.DietRepository dietRepository = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.MutableStateFlow<java.time.LocalDate> _selectedDate = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.StateFlow<java.time.LocalDate> selectedDate = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.MutableStateFlow<java.time.YearMonth> _currentMonth = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.StateFlow<java.time.YearMonth> currentMonth = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.StateFlow<com.appfit.ui.common.UiState<com.appfit.data.model.DailyPlan>> dailyPlanState = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.StateFlow<java.util.Set<java.time.LocalDate>> datesWithContent = null;
    
    @javax.inject.Inject()
    public CalendarViewModel(@org.jetbrains.annotations.NotNull()
    com.appfit.domain.usecase.GetDailyPlanUseCase getDailyPlanUseCase, @org.jetbrains.annotations.NotNull()
    com.appfit.data.repository.ActivityRepository activityRepository, @org.jetbrains.annotations.NotNull()
    com.appfit.data.repository.DietRepository dietRepository) {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.StateFlow<java.time.LocalDate> getSelectedDate() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.StateFlow<java.time.YearMonth> getCurrentMonth() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.StateFlow<com.appfit.ui.common.UiState<com.appfit.data.model.DailyPlan>> getDailyPlanState() {
        return null;
    }
    
    @kotlin.OptIn(markerClass = {kotlinx.coroutines.ExperimentalCoroutinesApi.class})
    @java.lang.Deprecated()
    public static void getDailyPlanState$annotations() {
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.StateFlow<java.util.Set<java.time.LocalDate>> getDatesWithContent() {
        return null;
    }
    
    public final void onDateSelected(@org.jetbrains.annotations.NotNull()
    java.time.LocalDate date) {
    }
    
    public final void onMonthChanged(@org.jetbrains.annotations.NotNull()
    java.time.YearMonth month) {
    }
    
    public final void toggleActivityCompleted(long id, boolean currentState) {
    }
    
    public final void toggleMealConsumed(long id, boolean currentState) {
    }
}