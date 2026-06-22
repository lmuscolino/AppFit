package com.appfit.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.appfit.data.local.converters.DateTimeConverters
import com.appfit.data.local.dao.*
import com.appfit.data.model.*

@Database(
    entities = [
        Activity::class,
        Meal::class,
        DietPlan::class,
        ShoppingItem::class,
        ChatMessage::class,
        FavoriteRecipe::class,
        Reminder::class,
        PendingEmailItem::class
    ],
    version = 4,
    exportSchema = true
)
@TypeConverters(DateTimeConverters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun activityDao(): ActivityDao
    abstract fun mealDao(): MealDao
    abstract fun dietPlanDao(): DietPlanDao
    abstract fun shoppingItemDao(): ShoppingItemDao
    abstract fun chatMessageDao(): ChatMessageDao
    abstract fun favoriteRecipeDao(): FavoriteRecipeDao
    abstract fun reminderDao(): ReminderDao
    abstract fun pendingEmailItemDao(): PendingEmailItemDao
}
