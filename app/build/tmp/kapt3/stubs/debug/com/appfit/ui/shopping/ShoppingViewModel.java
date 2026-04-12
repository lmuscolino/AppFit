package com.appfit.ui.shopping;

import androidx.lifecycle.ViewModel;
import com.appfit.data.model.ShoppingCategory;
import com.appfit.data.model.ShoppingItem;
import com.appfit.data.repository.ShoppingRepository;
import com.appfit.domain.usecase.GenerateShoppingListUseCase;
import dagger.hilt.android.lifecycle.HiltViewModel;
import kotlinx.coroutines.flow.*;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import javax.inject.Inject;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000P\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0002\u0010$\n\u0002\u0018\u0002\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\t\n\u0002\b\u0004\b\u0007\u0018\u00002\u00020\u0001B\u0017\b\u0007\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u00a2\u0006\u0002\u0010\u0006J\u000e\u0010\u0017\u001a\u00020\u00182\u0006\u0010\u0019\u001a\u00020\u001aJ\u0006\u0010\u001b\u001a\u00020\u0018J\u000e\u0010\u001c\u001a\u00020\u00182\u0006\u0010\u001d\u001a\u00020\u000fR\u0014\u0010\u0007\u001a\b\u0012\u0004\u0012\u00020\t0\bX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082\u0004\u00a2\u0006\u0002\n\u0000R)\u0010\n\u001a\u001a\u0012\u0016\u0012\u0014\u0012\u0004\u0012\u00020\r\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u000f0\u000e0\f0\u000b\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0010\u0010\u0011R\u0017\u0010\u0012\u001a\b\u0012\u0004\u0012\u00020\t0\u000b\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0012\u0010\u0011R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0014\u0010\u0013\u001a\u00020\u00148BX\u0082\u0004\u00a2\u0006\u0006\u001a\u0004\b\u0015\u0010\u0016\u00a8\u0006\u001e"}, d2 = {"Lcom/appfit/ui/shopping/ShoppingViewModel;", "Landroidx/lifecycle/ViewModel;", "shoppingRepository", "Lcom/appfit/data/repository/ShoppingRepository;", "generateShoppingListUseCase", "Lcom/appfit/domain/usecase/GenerateShoppingListUseCase;", "(Lcom/appfit/data/repository/ShoppingRepository;Lcom/appfit/domain/usecase/GenerateShoppingListUseCase;)V", "_isGenerating", "Lkotlinx/coroutines/flow/MutableStateFlow;", "", "groupedItems", "Lkotlinx/coroutines/flow/StateFlow;", "", "Lcom/appfit/data/model/ShoppingCategory;", "", "Lcom/appfit/data/model/ShoppingItem;", "getGroupedItems", "()Lkotlinx/coroutines/flow/StateFlow;", "isGenerating", "weekStart", "Ljava/time/LocalDate;", "getWeekStart", "()Ljava/time/LocalDate;", "deleteItem", "", "id", "", "regenerateFromMealPlan", "toggleItemChecked", "item", "app_debug"})
@dagger.hilt.android.lifecycle.HiltViewModel()
public final class ShoppingViewModel extends androidx.lifecycle.ViewModel {
    @org.jetbrains.annotations.NotNull()
    private final com.appfit.data.repository.ShoppingRepository shoppingRepository = null;
    @org.jetbrains.annotations.NotNull()
    private final com.appfit.domain.usecase.GenerateShoppingListUseCase generateShoppingListUseCase = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.StateFlow<java.util.Map<com.appfit.data.model.ShoppingCategory, java.util.List<com.appfit.data.model.ShoppingItem>>> groupedItems = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.MutableStateFlow<java.lang.Boolean> _isGenerating = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.StateFlow<java.lang.Boolean> isGenerating = null;
    
    @javax.inject.Inject()
    public ShoppingViewModel(@org.jetbrains.annotations.NotNull()
    com.appfit.data.repository.ShoppingRepository shoppingRepository, @org.jetbrains.annotations.NotNull()
    com.appfit.domain.usecase.GenerateShoppingListUseCase generateShoppingListUseCase) {
        super();
    }
    
    private final java.time.LocalDate getWeekStart() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.StateFlow<java.util.Map<com.appfit.data.model.ShoppingCategory, java.util.List<com.appfit.data.model.ShoppingItem>>> getGroupedItems() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.StateFlow<java.lang.Boolean> isGenerating() {
        return null;
    }
    
    public final void regenerateFromMealPlan() {
    }
    
    public final void toggleItemChecked(@org.jetbrains.annotations.NotNull()
    com.appfit.data.model.ShoppingItem item) {
    }
    
    public final void deleteItem(long id) {
    }
}