package com.appfit.data.local.dao;

import androidx.room.*;
import com.appfit.data.model.Meal;
import kotlinx.coroutines.flow.Flow;
import java.time.LocalDate;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u00008\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\t\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\b\n\u0002\u0010\u000b\n\u0002\b\u0003\bg\u0018\u00002\u00020\u0001J\u0016\u0010\u0002\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u0005H\u00a7@\u00a2\u0006\u0002\u0010\u0006J$\u0010\u0007\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\n0\t0\b2\u0006\u0010\u000b\u001a\u00020\n2\u0006\u0010\f\u001a\u00020\nH\'J\u0018\u0010\r\u001a\u0004\u0018\u00010\u000e2\u0006\u0010\u0004\u001a\u00020\u0005H\u00a7@\u00a2\u0006\u0002\u0010\u0006J\u001c\u0010\u000f\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u000e0\t0\b2\u0006\u0010\u0010\u001a\u00020\nH\'J$\u0010\u0011\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u000e0\t0\b2\u0006\u0010\u000b\u001a\u00020\n2\u0006\u0010\f\u001a\u00020\nH\'J\u0016\u0010\u0012\u001a\u00020\u00052\u0006\u0010\u0013\u001a\u00020\u000eH\u00a7@\u00a2\u0006\u0002\u0010\u0014J\u001e\u0010\u0015\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u00052\u0006\u0010\u0016\u001a\u00020\u0017H\u00a7@\u00a2\u0006\u0002\u0010\u0018J\u0016\u0010\u0019\u001a\u00020\u00032\u0006\u0010\u0013\u001a\u00020\u000eH\u00a7@\u00a2\u0006\u0002\u0010\u0014\u00a8\u0006\u001a"}, d2 = {"Lcom/appfit/data/local/dao/MealDao;", "", "deleteMeal", "", "id", "", "(JLkotlin/coroutines/Continuation;)Ljava/lang/Object;", "getDatesWithMeals", "Lkotlinx/coroutines/flow/Flow;", "", "Ljava/time/LocalDate;", "start", "end", "getMealById", "Lcom/appfit/data/model/Meal;", "getMealsForDate", "date", "getMealsForRange", "insertMeal", "meal", "(Lcom/appfit/data/model/Meal;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "setConsumed", "consumed", "", "(JZLkotlin/coroutines/Continuation;)Ljava/lang/Object;", "updateMeal", "app_debug"})
@androidx.room.Dao()
public abstract interface MealDao {
    
    @androidx.room.Query(value = "SELECT * FROM meals WHERE scheduledDate = :date ORDER BY type ASC")
    @org.jetbrains.annotations.NotNull()
    public abstract kotlinx.coroutines.flow.Flow<java.util.List<com.appfit.data.model.Meal>> getMealsForDate(@org.jetbrains.annotations.NotNull()
    java.time.LocalDate date);
    
    @androidx.room.Query(value = "SELECT * FROM meals WHERE scheduledDate BETWEEN :start AND :end ORDER BY scheduledDate ASC, type ASC")
    @org.jetbrains.annotations.NotNull()
    public abstract kotlinx.coroutines.flow.Flow<java.util.List<com.appfit.data.model.Meal>> getMealsForRange(@org.jetbrains.annotations.NotNull()
    java.time.LocalDate start, @org.jetbrains.annotations.NotNull()
    java.time.LocalDate end);
    
    @androidx.room.Query(value = "SELECT * FROM meals WHERE id = :id")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object getMealById(long id, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super com.appfit.data.model.Meal> $completion);
    
    @androidx.room.Insert(onConflict = 1)
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object insertMeal(@org.jetbrains.annotations.NotNull()
    com.appfit.data.model.Meal meal, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.lang.Long> $completion);
    
    @androidx.room.Update()
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object updateMeal(@org.jetbrains.annotations.NotNull()
    com.appfit.data.model.Meal meal, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
    
    @androidx.room.Query(value = "UPDATE meals SET isConsumed = :consumed WHERE id = :id")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object setConsumed(long id, boolean consumed, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
    
    @androidx.room.Query(value = "DELETE FROM meals WHERE id = :id")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object deleteMeal(long id, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
    
    @androidx.room.Query(value = "SELECT DISTINCT scheduledDate FROM meals WHERE scheduledDate BETWEEN :start AND :end")
    @org.jetbrains.annotations.NotNull()
    public abstract kotlinx.coroutines.flow.Flow<java.util.List<java.time.LocalDate>> getDatesWithMeals(@org.jetbrains.annotations.NotNull()
    java.time.LocalDate start, @org.jetbrains.annotations.NotNull()
    java.time.LocalDate end);
}