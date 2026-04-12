package com.appfit.di;

import com.appfit.ai.ApiKeyProvider;
import com.appfit.ai.AnthropicService;
import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.components.SingletonComponent;
import javax.inject.Singleton;

@dagger.Module()
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\u0018\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\b\u00c7\u0002\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002J\u0010\u0010\u0003\u001a\u00020\u00042\u0006\u0010\u0005\u001a\u00020\u0006H\u0007\u00a8\u0006\u0007"}, d2 = {"Lcom/appfit/di/AiModule;", "", "()V", "provideAnthropicService", "Lcom/appfit/ai/AnthropicService;", "apiKeyProvider", "Lcom/appfit/ai/ApiKeyProvider;", "app_debug"})
@dagger.hilt.InstallIn(value = {dagger.hilt.components.SingletonComponent.class})
public final class AiModule {
    @org.jetbrains.annotations.NotNull()
    public static final com.appfit.di.AiModule INSTANCE = null;
    
    private AiModule() {
        super();
    }
    
    @dagger.Provides()
    @javax.inject.Singleton()
    @org.jetbrains.annotations.NotNull()
    public final com.appfit.ai.AnthropicService provideAnthropicService(@org.jetbrains.annotations.NotNull()
    com.appfit.ai.ApiKeyProvider apiKeyProvider) {
        return null;
    }
}