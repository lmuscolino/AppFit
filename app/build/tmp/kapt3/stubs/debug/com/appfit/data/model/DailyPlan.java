package com.appfit.data.model;

import java.time.LocalDate;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000>\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\t\n\u0002\u0010\b\n\u0002\b\u0012\n\u0002\u0010\u000b\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0000\b\u0087\b\u0018\u00002\u00020\u0001B9\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u000e\b\u0002\u0010\u0004\u001a\b\u0012\u0004\u0012\u00020\u00060\u0005\u0012\u000e\b\u0002\u0010\u0007\u001a\b\u0012\u0004\u0012\u00020\b0\u0005\u0012\n\b\u0002\u0010\t\u001a\u0004\u0018\u00010\n\u00a2\u0006\u0002\u0010\u000bJ\t\u0010!\u001a\u00020\u0003H\u00c6\u0003J\u000f\u0010\"\u001a\b\u0012\u0004\u0012\u00020\u00060\u0005H\u00c6\u0003J\u000f\u0010#\u001a\b\u0012\u0004\u0012\u00020\b0\u0005H\u00c6\u0003J\u000b\u0010$\u001a\u0004\u0018\u00010\nH\u00c6\u0003J?\u0010%\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\u000e\b\u0002\u0010\u0004\u001a\b\u0012\u0004\u0012\u00020\u00060\u00052\u000e\b\u0002\u0010\u0007\u001a\b\u0012\u0004\u0012\u00020\b0\u00052\n\b\u0002\u0010\t\u001a\u0004\u0018\u00010\nH\u00c6\u0001J\u0013\u0010&\u001a\u00020\'2\b\u0010(\u001a\u0004\u0018\u00010\u0001H\u00d6\u0003J\t\u0010)\u001a\u00020\u0014H\u00d6\u0001J\t\u0010*\u001a\u00020+H\u00d6\u0001R\u0013\u0010\t\u001a\u0004\u0018\u00010\n\u00a2\u0006\b\n\u0000\u001a\u0004\b\f\u0010\rR\u0017\u0010\u0004\u001a\b\u0012\u0004\u0012\u00020\u00060\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000e\u0010\u000fR\u0011\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0010\u0010\u0011R\u0017\u0010\u0007\u001a\b\u0012\u0004\u0012\u00020\b0\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0012\u0010\u000fR\u0011\u0010\u0013\u001a\u00020\u00148F\u00a2\u0006\u0006\u001a\u0004\b\u0015\u0010\u0016R\u0011\u0010\u0017\u001a\u00020\u00148F\u00a2\u0006\u0006\u001a\u0004\b\u0018\u0010\u0016R\u0011\u0010\u0019\u001a\u00020\u00148F\u00a2\u0006\u0006\u001a\u0004\b\u001a\u0010\u0016R\u0011\u0010\u001b\u001a\u00020\u00148F\u00a2\u0006\u0006\u001a\u0004\b\u001c\u0010\u0016R\u0011\u0010\u001d\u001a\u00020\u00148F\u00a2\u0006\u0006\u001a\u0004\b\u001e\u0010\u0016R\u0011\u0010\u001f\u001a\u00020\u00148F\u00a2\u0006\u0006\u001a\u0004\b \u0010\u0016\u00a8\u0006,"}, d2 = {"Lcom/appfit/data/model/DailyPlan;", "", "date", "Ljava/time/LocalDate;", "activities", "", "Lcom/appfit/data/model/Activity;", "meals", "Lcom/appfit/data/model/Meal;", "activeDietPlan", "Lcom/appfit/data/model/DietPlan;", "(Ljava/time/LocalDate;Ljava/util/List;Ljava/util/List;Lcom/appfit/data/model/DietPlan;)V", "getActiveDietPlan", "()Lcom/appfit/data/model/DietPlan;", "getActivities", "()Ljava/util/List;", "getDate", "()Ljava/time/LocalDate;", "getMeals", "totalCaloriesBurned", "", "getTotalCaloriesBurned", "()I", "totalCaloriesConsumed", "getTotalCaloriesConsumed", "totalCaloriesPlanned", "getTotalCaloriesPlanned", "totalCarbsG", "getTotalCarbsG", "totalFatG", "getTotalFatG", "totalProteinG", "getTotalProteinG", "component1", "component2", "component3", "component4", "copy", "equals", "", "other", "hashCode", "toString", "", "app_debug"})
public final class DailyPlan {
    @org.jetbrains.annotations.NotNull()
    private final java.time.LocalDate date = null;
    @org.jetbrains.annotations.NotNull()
    private final java.util.List<com.appfit.data.model.Activity> activities = null;
    @org.jetbrains.annotations.NotNull()
    private final java.util.List<com.appfit.data.model.Meal> meals = null;
    @org.jetbrains.annotations.Nullable()
    private final com.appfit.data.model.DietPlan activeDietPlan = null;
    
    public DailyPlan(@org.jetbrains.annotations.NotNull()
    java.time.LocalDate date, @org.jetbrains.annotations.NotNull()
    java.util.List<com.appfit.data.model.Activity> activities, @org.jetbrains.annotations.NotNull()
    java.util.List<com.appfit.data.model.Meal> meals, @org.jetbrains.annotations.Nullable()
    com.appfit.data.model.DietPlan activeDietPlan) {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.time.LocalDate getDate() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.util.List<com.appfit.data.model.Activity> getActivities() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.util.List<com.appfit.data.model.Meal> getMeals() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final com.appfit.data.model.DietPlan getActiveDietPlan() {
        return null;
    }
    
    public final int getTotalCaloriesConsumed() {
        return 0;
    }
    
    public final int getTotalCaloriesPlanned() {
        return 0;
    }
    
    public final int getTotalCaloriesBurned() {
        return 0;
    }
    
    public final int getTotalProteinG() {
        return 0;
    }
    
    public final int getTotalCarbsG() {
        return 0;
    }
    
    public final int getTotalFatG() {
        return 0;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.time.LocalDate component1() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.util.List<com.appfit.data.model.Activity> component2() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.util.List<com.appfit.data.model.Meal> component3() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final com.appfit.data.model.DietPlan component4() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.appfit.data.model.DailyPlan copy(@org.jetbrains.annotations.NotNull()
    java.time.LocalDate date, @org.jetbrains.annotations.NotNull()
    java.util.List<com.appfit.data.model.Activity> activities, @org.jetbrains.annotations.NotNull()
    java.util.List<com.appfit.data.model.Meal> meals, @org.jetbrains.annotations.Nullable()
    com.appfit.data.model.DietPlan activeDietPlan) {
        return null;
    }
    
    @java.lang.Override()
    public boolean equals(@org.jetbrains.annotations.Nullable()
    java.lang.Object other) {
        return false;
    }
    
    @java.lang.Override()
    public int hashCode() {
        return 0;
    }
    
    @java.lang.Override()
    @org.jetbrains.annotations.NotNull()
    public java.lang.String toString() {
        return null;
    }
}