package com.appfit.data.repository;

import com.appfit.data.local.dao.DietPlanDao;
import com.appfit.data.local.dao.MealDao;
import com.appfit.data.model.DietPlan;
import com.appfit.data.model.Meal;
import kotlinx.coroutines.flow.Flow;
import java.time.LocalDate;
import javax.inject.Inject;
import javax.inject.Singleton;

@javax.inject.Singleton()
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000N\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\t\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010 \n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\n\n\u0002\u0010\u000b\n\u0002\b\u0003\b\u0007\u0018\u00002\u00020\u0001B\u0017\b\u0007\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u00a2\u0006\u0002\u0010\u0006J\u0016\u0010\u0007\u001a\u00020\b2\u0006\u0010\t\u001a\u00020\nH\u0086@\u00a2\u0006\u0002\u0010\u000bJ\u0016\u0010\f\u001a\u00020\b2\u0006\u0010\t\u001a\u00020\nH\u0086@\u00a2\u0006\u0002\u0010\u000bJ\u0016\u0010\r\u001a\u00020\b2\u0006\u0010\t\u001a\u00020\nH\u0086@\u00a2\u0006\u0002\u0010\u000bJ\u0016\u0010\u000e\u001a\n\u0012\u0006\u0012\u0004\u0018\u00010\u00100\u000f2\u0006\u0010\u0011\u001a\u00020\u0012J\u0012\u0010\u0013\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00100\u00140\u000fJ\"\u0010\u0015\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00120\u00140\u000f2\u0006\u0010\u0016\u001a\u00020\u00122\u0006\u0010\u0017\u001a\u00020\u0012J\u0018\u0010\u0018\u001a\u0004\u0018\u00010\u00192\u0006\u0010\t\u001a\u00020\nH\u0086@\u00a2\u0006\u0002\u0010\u000bJ\u001a\u0010\u001a\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00190\u00140\u000f2\u0006\u0010\u0011\u001a\u00020\u0012J\"\u0010\u001b\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00190\u00140\u000f2\u0006\u0010\u0016\u001a\u00020\u00122\u0006\u0010\u0017\u001a\u00020\u0012J\u0016\u0010\u001c\u001a\u00020\n2\u0006\u0010\u001d\u001a\u00020\u0019H\u0086@\u00a2\u0006\u0002\u0010\u001eJ\u0016\u0010\u001f\u001a\u00020\n2\u0006\u0010 \u001a\u00020\u0010H\u0086@\u00a2\u0006\u0002\u0010!J\u001e\u0010\"\u001a\u00020\b2\u0006\u0010\t\u001a\u00020\n2\u0006\u0010#\u001a\u00020$H\u0086@\u00a2\u0006\u0002\u0010%J\u0016\u0010&\u001a\u00020\b2\u0006\u0010\u001d\u001a\u00020\u0019H\u0086@\u00a2\u0006\u0002\u0010\u001eR\u000e\u0010\u0004\u001a\u00020\u0005X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\'"}, d2 = {"Lcom/appfit/data/repository/DietRepository;", "", "mealDao", "Lcom/appfit/data/local/dao/MealDao;", "dietPlanDao", "Lcom/appfit/data/local/dao/DietPlanDao;", "(Lcom/appfit/data/local/dao/MealDao;Lcom/appfit/data/local/dao/DietPlanDao;)V", "activatePlan", "", "id", "", "(JLkotlin/coroutines/Continuation;)Ljava/lang/Object;", "deleteMeal", "deletePlan", "getActivePlanForDate", "Lkotlinx/coroutines/flow/Flow;", "Lcom/appfit/data/model/DietPlan;", "date", "Ljava/time/LocalDate;", "getAllPlans", "", "getDatesWithMeals", "start", "end", "getMealById", "Lcom/appfit/data/model/Meal;", "getMealsForDate", "getMealsForRange", "insertMeal", "meal", "(Lcom/appfit/data/model/Meal;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "insertPlan", "plan", "(Lcom/appfit/data/model/DietPlan;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "setConsumed", "consumed", "", "(JZLkotlin/coroutines/Continuation;)Ljava/lang/Object;", "updateMeal", "app_debug"})
public final class DietRepository {
    @org.jetbrains.annotations.NotNull()
    private final com.appfit.data.local.dao.MealDao mealDao = null;
    @org.jetbrains.annotations.NotNull()
    private final com.appfit.data.local.dao.DietPlanDao dietPlanDao = null;
    
    @javax.inject.Inject()
    public DietRepository(@org.jetbrains.annotations.NotNull()
    com.appfit.data.local.dao.MealDao mealDao, @org.jetbrains.annotations.NotNull()
    com.appfit.data.local.dao.DietPlanDao dietPlanDao) {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.Flow<java.util.List<com.appfit.data.model.Meal>> getMealsForDate(@org.jetbrains.annotations.NotNull()
    java.time.LocalDate date) {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.Flow<java.util.List<com.appfit.data.model.Meal>> getMealsForRange(@org.jetbrains.annotations.NotNull()
    java.time.LocalDate start, @org.jetbrains.annotations.NotNull()
    java.time.LocalDate end) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object getMealById(long id, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super com.appfit.data.model.Meal> $completion) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object insertMeal(@org.jetbrains.annotations.NotNull()
    com.appfit.data.model.Meal meal, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.lang.Long> $completion) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object updateMeal(@org.jetbrains.annotations.NotNull()
    com.appfit.data.model.Meal meal, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object setConsumed(long id, boolean consumed, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object deleteMeal(long id, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.Flow<java.util.List<java.time.LocalDate>> getDatesWithMeals(@org.jetbrains.annotations.NotNull()
    java.time.LocalDate start, @org.jetbrains.annotations.NotNull()
    java.time.LocalDate end) {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.Flow<com.appfit.data.model.DietPlan> getActivePlanForDate(@org.jetbrains.annotations.NotNull()
    java.time.LocalDate date) {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.Flow<java.util.List<com.appfit.data.model.DietPlan>> getAllPlans() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object insertPlan(@org.jetbrains.annotations.NotNull()
    com.appfit.data.model.DietPlan plan, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.lang.Long> $completion) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object activatePlan(long id, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object deletePlan(long id, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
}