package com.appfit.data.local.dao;

import androidx.room.*;
import com.appfit.data.model.DietPlan;
import kotlinx.coroutines.flow.Flow;
import java.time.LocalDate;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u00000\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\t\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010 \n\u0002\b\u0005\bg\u0018\u00002\u00020\u0001J\u0016\u0010\u0002\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u0005H\u00a7@\u00a2\u0006\u0002\u0010\u0006J\u000e\u0010\u0007\u001a\u00020\u0003H\u00a7@\u00a2\u0006\u0002\u0010\bJ\u0016\u0010\t\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u0005H\u00a7@\u00a2\u0006\u0002\u0010\u0006J\u0018\u0010\n\u001a\n\u0012\u0006\u0012\u0004\u0018\u00010\f0\u000b2\u0006\u0010\r\u001a\u00020\u000eH\'J\u0014\u0010\u000f\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\f0\u00100\u000bH\'J\u0016\u0010\u0011\u001a\u00020\u00052\u0006\u0010\u0012\u001a\u00020\fH\u00a7@\u00a2\u0006\u0002\u0010\u0013J\u0016\u0010\u0014\u001a\u00020\u00032\u0006\u0010\u0012\u001a\u00020\fH\u00a7@\u00a2\u0006\u0002\u0010\u0013\u00a8\u0006\u0015"}, d2 = {"Lcom/appfit/data/local/dao/DietPlanDao;", "", "activatePlan", "", "id", "", "(JLkotlin/coroutines/Continuation;)Ljava/lang/Object;", "deactivateAllPlans", "(Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "deletePlan", "getActivePlanForDate", "Lkotlinx/coroutines/flow/Flow;", "Lcom/appfit/data/model/DietPlan;", "date", "Ljava/time/LocalDate;", "getAllPlans", "", "insertPlan", "plan", "(Lcom/appfit/data/model/DietPlan;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "updatePlan", "app_debug"})
@androidx.room.Dao()
public abstract interface DietPlanDao {
    
    @androidx.room.Query(value = "SELECT * FROM diet_plans WHERE isActive = 1 AND startDate <= :date AND endDate >= :date LIMIT 1")
    @org.jetbrains.annotations.NotNull()
    public abstract kotlinx.coroutines.flow.Flow<com.appfit.data.model.DietPlan> getActivePlanForDate(@org.jetbrains.annotations.NotNull()
    java.time.LocalDate date);
    
    @androidx.room.Query(value = "SELECT * FROM diet_plans ORDER BY startDate DESC")
    @org.jetbrains.annotations.NotNull()
    public abstract kotlinx.coroutines.flow.Flow<java.util.List<com.appfit.data.model.DietPlan>> getAllPlans();
    
    @androidx.room.Insert(onConflict = 1)
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object insertPlan(@org.jetbrains.annotations.NotNull()
    com.appfit.data.model.DietPlan plan, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.lang.Long> $completion);
    
    @androidx.room.Update()
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object updatePlan(@org.jetbrains.annotations.NotNull()
    com.appfit.data.model.DietPlan plan, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
    
    @androidx.room.Query(value = "UPDATE diet_plans SET isActive = 0")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object deactivateAllPlans(@org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
    
    @androidx.room.Query(value = "UPDATE diet_plans SET isActive = 1 WHERE id = :id")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object activatePlan(long id, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
    
    @androidx.room.Query(value = "DELETE FROM diet_plans WHERE id = :id")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object deletePlan(long id, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
}