package com.appfit.di

import android.content.Context
import androidx.room.Room
import com.appfit.data.local.AppDatabase
import com.appfit.data.local.dao.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase =
        Room.databaseBuilder(context, AppDatabase::class.java, "appfit.db")
            .fallbackToDestructiveMigration()
            .build()

    @Provides
    fun provideActivityDao(db: AppDatabase): ActivityDao = db.activityDao()

    @Provides
    fun provideMealDao(db: AppDatabase): MealDao = db.mealDao()

    @Provides
    fun provideDietPlanDao(db: AppDatabase): DietPlanDao = db.dietPlanDao()

    @Provides
    fun provideShoppingItemDao(db: AppDatabase): ShoppingItemDao = db.shoppingItemDao()

    @Provides
    fun provideChatMessageDao(db: AppDatabase): ChatMessageDao = db.chatMessageDao()
}
