package com.appfit.data.local;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import com.appfit.data.local.converters.DateTimeConverters;
import com.appfit.data.local.dao.*;
import com.appfit.data.model.*;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000*\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\b\'\u0018\u00002\u00020\u0001B\u0005\u00a2\u0006\u0002\u0010\u0002J\b\u0010\u0003\u001a\u00020\u0004H&J\b\u0010\u0005\u001a\u00020\u0006H&J\b\u0010\u0007\u001a\u00020\bH&J\b\u0010\t\u001a\u00020\nH&J\b\u0010\u000b\u001a\u00020\fH&\u00a8\u0006\r"}, d2 = {"Lcom/appfit/data/local/AppDatabase;", "Landroidx/room/RoomDatabase;", "()V", "activityDao", "Lcom/appfit/data/local/dao/ActivityDao;", "chatMessageDao", "Lcom/appfit/data/local/dao/ChatMessageDao;", "dietPlanDao", "Lcom/appfit/data/local/dao/DietPlanDao;", "mealDao", "Lcom/appfit/data/local/dao/MealDao;", "shoppingItemDao", "Lcom/appfit/data/local/dao/ShoppingItemDao;", "app_debug"})
@androidx.room.Database(entities = {com.appfit.data.model.Activity.class, com.appfit.data.model.Meal.class, com.appfit.data.model.DietPlan.class, com.appfit.data.model.ShoppingItem.class, com.appfit.data.model.ChatMessage.class}, version = 1, exportSchema = true)
@androidx.room.TypeConverters(value = {com.appfit.data.local.converters.DateTimeConverters.class})
public abstract class AppDatabase extends androidx.room.RoomDatabase {
    
    public AppDatabase() {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public abstract com.appfit.data.local.dao.ActivityDao activityDao();
    
    @org.jetbrains.annotations.NotNull()
    public abstract com.appfit.data.local.dao.MealDao mealDao();
    
    @org.jetbrains.annotations.NotNull()
    public abstract com.appfit.data.local.dao.DietPlanDao dietPlanDao();
    
    @org.jetbrains.annotations.NotNull()
    public abstract com.appfit.data.local.dao.ShoppingItemDao shoppingItemDao();
    
    @org.jetbrains.annotations.NotNull()
    public abstract com.appfit.data.local.dao.ChatMessageDao chatMessageDao();
}