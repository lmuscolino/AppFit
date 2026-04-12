package com.appfit.data.repository;

import com.appfit.data.local.dao.ShoppingItemDao;
import com.appfit.data.model.ShoppingItem;
import kotlinx.coroutines.flow.Flow;
import java.time.LocalDate;
import javax.inject.Inject;
import javax.inject.Singleton;

@javax.inject.Singleton()
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000>\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\t\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0010\u000b\n\u0002\b\u0002\b\u0007\u0018\u00002\u00020\u0001B\u000f\b\u0007\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004J\u0016\u0010\u0005\u001a\u00020\u00062\u0006\u0010\u0007\u001a\u00020\bH\u0086@\u00a2\u0006\u0002\u0010\tJ\u001a\u0010\n\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\r0\f0\u000b2\u0006\u0010\u000e\u001a\u00020\u000fJ$\u0010\u0010\u001a\u00020\u00062\u0006\u0010\u000e\u001a\u00020\u000f2\f\u0010\u0011\u001a\b\u0012\u0004\u0012\u00020\r0\fH\u0086@\u00a2\u0006\u0002\u0010\u0012J\u001e\u0010\u0013\u001a\u00020\u00062\u0006\u0010\u0007\u001a\u00020\b2\u0006\u0010\u0014\u001a\u00020\u0015H\u0086@\u00a2\u0006\u0002\u0010\u0016R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0017"}, d2 = {"Lcom/appfit/data/repository/ShoppingRepository;", "", "dao", "Lcom/appfit/data/local/dao/ShoppingItemDao;", "(Lcom/appfit/data/local/dao/ShoppingItemDao;)V", "deleteItem", "", "id", "", "(JLkotlin/coroutines/Continuation;)Ljava/lang/Object;", "getShoppingListForWeek", "Lkotlinx/coroutines/flow/Flow;", "", "Lcom/appfit/data/model/ShoppingItem;", "weekStart", "Ljava/time/LocalDate;", "replaceShoppingListForWeek", "items", "(Ljava/time/LocalDate;Ljava/util/List;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "toggleChecked", "currentState", "", "(JZLkotlin/coroutines/Continuation;)Ljava/lang/Object;", "app_debug"})
public final class ShoppingRepository {
    @org.jetbrains.annotations.NotNull()
    private final com.appfit.data.local.dao.ShoppingItemDao dao = null;
    
    @javax.inject.Inject()
    public ShoppingRepository(@org.jetbrains.annotations.NotNull()
    com.appfit.data.local.dao.ShoppingItemDao dao) {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.Flow<java.util.List<com.appfit.data.model.ShoppingItem>> getShoppingListForWeek(@org.jetbrains.annotations.NotNull()
    java.time.LocalDate weekStart) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object replaceShoppingListForWeek(@org.jetbrains.annotations.NotNull()
    java.time.LocalDate weekStart, @org.jetbrains.annotations.NotNull()
    java.util.List<com.appfit.data.model.ShoppingItem> items, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object toggleChecked(long id, boolean currentState, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object deleteItem(long id, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
}