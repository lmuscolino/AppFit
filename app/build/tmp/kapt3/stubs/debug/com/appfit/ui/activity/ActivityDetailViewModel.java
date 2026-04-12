package com.appfit.ui.activity;

import androidx.lifecycle.SavedStateHandle;
import androidx.lifecycle.ViewModel;
import com.appfit.data.model.Activity;
import com.appfit.data.repository.ActivityRepository;
import com.appfit.ui.common.UiState;
import dagger.hilt.android.lifecycle.HiltViewModel;
import kotlinx.coroutines.flow.StateFlow;
import javax.inject.Inject;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000<\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\t\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0002\b\u0002\b\u0007\u0018\u00002\u00020\u0001B\u0017\b\u0007\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u00a2\u0006\u0002\u0010\u0006J\b\u0010\u0011\u001a\u00020\u0012H\u0002J\u0006\u0010\u0013\u001a\u00020\u0012R\u001a\u0010\u0007\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\n0\t0\bX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u000b\u001a\u00020\fX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u001d\u0010\r\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\n0\t0\u000e\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000f\u0010\u0010\u00a8\u0006\u0014"}, d2 = {"Lcom/appfit/ui/activity/ActivityDetailViewModel;", "Landroidx/lifecycle/ViewModel;", "repository", "Lcom/appfit/data/repository/ActivityRepository;", "savedStateHandle", "Landroidx/lifecycle/SavedStateHandle;", "(Lcom/appfit/data/repository/ActivityRepository;Landroidx/lifecycle/SavedStateHandle;)V", "_uiState", "Lkotlinx/coroutines/flow/MutableStateFlow;", "Lcom/appfit/ui/common/UiState;", "Lcom/appfit/data/model/Activity;", "activityId", "", "uiState", "Lkotlinx/coroutines/flow/StateFlow;", "getUiState", "()Lkotlinx/coroutines/flow/StateFlow;", "loadActivity", "", "toggleCompleted", "app_debug"})
@dagger.hilt.android.lifecycle.HiltViewModel()
public final class ActivityDetailViewModel extends androidx.lifecycle.ViewModel {
    @org.jetbrains.annotations.NotNull()
    private final com.appfit.data.repository.ActivityRepository repository = null;
    private final long activityId = 0L;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.MutableStateFlow<com.appfit.ui.common.UiState<com.appfit.data.model.Activity>> _uiState = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.StateFlow<com.appfit.ui.common.UiState<com.appfit.data.model.Activity>> uiState = null;
    
    @javax.inject.Inject()
    public ActivityDetailViewModel(@org.jetbrains.annotations.NotNull()
    com.appfit.data.repository.ActivityRepository repository, @org.jetbrains.annotations.NotNull()
    androidx.lifecycle.SavedStateHandle savedStateHandle) {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.StateFlow<com.appfit.ui.common.UiState<com.appfit.data.model.Activity>> getUiState() {
        return null;
    }
    
    private final void loadActivity() {
    }
    
    public final void toggleCompleted() {
    }
}