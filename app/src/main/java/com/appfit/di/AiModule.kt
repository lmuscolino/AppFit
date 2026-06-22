package com.appfit.di

import com.appfit.ai.AiDebugLogger
import com.appfit.ai.AiService
import com.appfit.ai.AiServiceFactory
import com.appfit.ai.ApiKeyProvider
import com.appfit.ai.AnthropicService
import com.appfit.ai.GeminiService
import com.appfit.ai.ToolApprovalManager
import com.appfit.ai.UserProfileProvider
import com.appfit.data.repository.ActivityRepository
import com.appfit.data.repository.DietRepository
import com.appfit.data.repository.FavoriteRecipeRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AiModule {

    @Provides
    @Singleton
    fun provideAnthropicService(
        apiKeyProvider: ApiKeyProvider,
        debugLogger: AiDebugLogger,
        toolApprovalManager: ToolApprovalManager,
        activityRepository: ActivityRepository,
        dietRepository: DietRepository,
        userProfileProvider: UserProfileProvider,
        favoriteRecipeRepository: FavoriteRecipeRepository
    ): AnthropicService = AnthropicService(
        apiKeyProvider,
        debugLogger,
        toolApprovalManager,
        activityRepository,
        dietRepository,
        userProfileProvider,
        favoriteRecipeRepository
    )

    @Provides
    @Singleton
    fun provideGeminiService(
        apiKeyProvider: ApiKeyProvider,
        debugLogger: AiDebugLogger,
        toolApprovalManager: ToolApprovalManager,
        activityRepository: ActivityRepository,
        dietRepository: DietRepository,
        userProfileProvider: UserProfileProvider,
        favoriteRecipeRepository: FavoriteRecipeRepository
    ): GeminiService = GeminiService(
        apiKeyProvider,
        debugLogger,
        toolApprovalManager,
        activityRepository,
        dietRepository,
        userProfileProvider,
        favoriteRecipeRepository
    )

    @Provides
    @Singleton
    fun provideAiService(
        apiKeyProvider: ApiKeyProvider,
        anthropicService: AnthropicService,
        geminiService: GeminiService
    ): AiService = AiServiceFactory(apiKeyProvider, anthropicService, geminiService)
}
