package com.appfit.data.local.dao;

import androidx.room.*;
import com.appfit.data.model.ShoppingItem;
import kotlinx.coroutines.flow.Flow;
import java.time.LocalDate;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u00008\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\t\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b\u0007\n\u0002\u0010\u000b\n\u0002\b\u0002\bg\u0018\u00002\u00020\u0001J\u0016\u0010\u0002\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u0005H\u00a7@\u00a2\u0006\u0002\u0010\u0006J\u0016\u0010\u0007\u001a\u00020\u00032\u0006\u0010\b\u001a\u00020\tH\u00a7@\u00a2\u0006\u0002\u0010\nJ\u001c\u0010\u000b\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u000e0\r0\f2\u0006\u0010\b\u001a\u00020\tH\'J\u001c\u0010\u000f\u001a\u00020\u00032\f\u0010\u0010\u001a\b\u0012\u0004\u0012\u00020\u000e0\rH\u00a7@\u00a2\u0006\u0002\u0010\u0011J$\u0010\u0012\u001a\u00020\u00032\u0006\u0010\b\u001a\u00020\t2\f\u0010\u0010\u001a\b\u0012\u0004\u0012\u00020\u000e0\rH\u0097@\u00a2\u0006\u0002\u0010\u0013J\u001e\u0010\u0014\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u00052\u0006\u0010\u0015\u001a\u00020\u0016H\u00a7@\u00a2\u0006\u0002\u0010\u0017\u00a8\u0006\u0018"}, d2 = {"Lcom/appfit/data/local/dao/ShoppingItemDao;", "", "deleteItem", "", "id", "", "(JLkotlin/coroutines/Continuation;)Ljava/lang/Object;", "deleteItemsForWeek", "weekStart", "Ljava/time/LocalDate;", "(Ljava/time/LocalDate;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "getShoppingListForWeek", "Lkotlinx/coroutines/flow/Flow;", "", "Lcom/appfit/data/model/ShoppingItem;", "insertItems", "items", "(Ljava/util/List;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "replaceShoppingListForWeek", "(Ljava/time/LocalDate;Ljava/util/List;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "setChecked", "checked", "", "(JZLkotlin/coroutines/Continuation;)Ljava/lang/Object;", "app_debug"})
@androidx.room.Dao()
public abstract interface ShoppingItemDao {
    
    @androidx.room.Query(value = "SELECT * FROM shopping_items WHERE weekStartDate = :weekStart ORDER BY category ASC, name ASC")
    @org.jetbrains.annotations.NotNull()
    public abstract kotlinx.coroutines.flow.Flow<java.util.List<com.appfit.data.model.ShoppingItem>> getShoppingListForWeek(@org.jetbrains.annotations.NotNull()
    java.time.LocalDate weekStart);
    
    @androidx.room.Insert(onConflict = 1)
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object insertItems(@org.jetbrains.annotations.NotNull()
    java.util.List<com.appfit.data.model.ShoppingItem> items, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
    
    @androidx.room.Query(value = "UPDATE shopping_items SET isChecked = :checked WHERE id = :id")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object setChecked(long id, boolean checked, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
    
    @androidx.room.Query(value = "DELETE FROM shopping_items WHERE weekStartDate = :weekStart")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object deleteItemsForWeek(@org.jetbrains.annotations.NotNull()
    java.time.LocalDate weekStart, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
    
    @androidx.room.Query(value = "DELETE FROM shopping_items WHERE id = :id")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object deleteItem(long id, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
    
    @androidx.room.Transaction()
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object replaceShoppingListForWeek(@org.jetbrains.annotations.NotNull()
    java.time.LocalDate weekStart, @org.jetbrains.annotations.NotNull()
    java.util.List<com.appfit.data.model.ShoppingItem> items, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 3, xi = 48)
    public static final class DefaultImpls {
        
        @androidx.room.Transaction()
        @org.jetbrains.annotations.Nullable()
        public static java.lang.Object replaceShoppingListForWeek(@org.jetbrains.annotations.NotNull()
        com.appfit.data.local.dao.ShoppingItemDao $this, @org.jetbrains.annotations.NotNull()
        java.time.LocalDate weekStart, @org.jetbrains.annotations.NotNull()
        java.util.List<com.appfit.data.model.ShoppingItem> items, @org.jetbrains.annotations.NotNull()
        kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
            return null;
        }
    }
}