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
        ChatMessage::class
    ],
    version = 1,
    exportSchema = true
)
@TypeConverters(DateTimeConverters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun activityDao(): ActivityDao
    abstract fun mealDao(): MealDao
    abstract fun dietPlanDao(): DietPlanDao
    abstract fun shoppingItemDao(): ShoppingItemDao
    abstract fun chatMessageDao(): ChatMessageDao
}
