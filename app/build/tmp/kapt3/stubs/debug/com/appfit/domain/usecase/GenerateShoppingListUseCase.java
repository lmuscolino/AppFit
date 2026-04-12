package com.appfit.domain.usecase;

import com.appfit.data.model.ShoppingCategory;
import com.appfit.data.model.ShoppingItem;
import com.appfit.data.repository.DietRepository;
import com.appfit.data.repository.ShoppingRepository;
import java.time.LocalDate;
import javax.inject.Inject;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000B\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0004\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\u0011\n\u0002\b\u0002\b\u0007\u0018\u00002\u00020\u0001B\u0017\b\u0007\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u00a2\u0006\u0002\u0010\u0006J\u0010\u0010\u0007\u001a\u00020\b2\u0006\u0010\t\u001a\u00020\nH\u0002J\u0010\u0010\u000b\u001a\u00020\n2\u0006\u0010\f\u001a\u00020\nH\u0002J\u0010\u0010\r\u001a\u00020\n2\u0006\u0010\f\u001a\u00020\nH\u0002J\u0016\u0010\u000e\u001a\u00020\u000f2\u0006\u0010\u0010\u001a\u00020\u0011H\u0086B\u00a2\u0006\u0002\u0010\u0012J\u0010\u0010\u0013\u001a\u00020\n2\u0006\u0010\f\u001a\u00020\nH\u0002J%\u0010\u0014\u001a\u00020\u0015*\u00020\n2\u0012\u0010\u0016\u001a\n\u0012\u0006\b\u0001\u0012\u00020\n0\u0017\"\u00020\nH\u0002\u00a2\u0006\u0002\u0010\u0018R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0019"}, d2 = {"Lcom/appfit/domain/usecase/GenerateShoppingListUseCase;", "", "dietRepository", "Lcom/appfit/data/repository/DietRepository;", "shoppingRepository", "Lcom/appfit/data/repository/ShoppingRepository;", "(Lcom/appfit/data/repository/DietRepository;Lcom/appfit/data/repository/ShoppingRepository;)V", "classifyIngredient", "Lcom/appfit/data/model/ShoppingCategory;", "normalized", "", "extractQuantity", "raw", "extractUnit", "invoke", "", "weekStart", "Ljava/time/LocalDate;", "(Ljava/time/LocalDate;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "normalizeIngredient", "containsAny", "", "keywords", "", "(Ljava/lang/String;[Ljava/lang/String;)Z", "app_debug"})
public final class GenerateShoppingListUseCase {
    @org.jetbrains.annotations.NotNull()
    private final com.appfit.data.repository.DietRepository dietRepository = null;
    @org.jetbrains.annotations.NotNull()
    private final com.appfit.data.repository.ShoppingRepository shoppingRepository = null;
    
    @javax.inject.Inject()
    public GenerateShoppingListUseCase(@org.jetbrains.annotations.NotNull()
    com.appfit.data.repository.DietRepository dietRepository, @org.jetbrains.annotations.NotNull()
    com.appfit.data.repository.ShoppingRepository shoppingRepository) {
        super();
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object invoke(@org.jetbrains.annotations.NotNull()
    java.time.LocalDate weekStart, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
    
    private final java.lang.String normalizeIngredient(java.lang.String raw) {
        return null;
    }
    
    private final java.lang.String extractQuantity(java.lang.String raw) {
        return null;
    }
    
    private final java.lang.String extractUnit(java.lang.String raw) {
        return null;
    }
    
    private final com.appfit.data.model.ShoppingCategory classifyIngredient(java.lang.String normalized) {
        return null;
    }
    
    private final boolean containsAny(java.lang.String $this$containsAny, java.lang.String... keywords) {
        return false;
    }
}