package com.appfit.domain.usecase;

import com.appfit.ai.AnthropicService;
import com.appfit.ai.ClaudeToolExecutor;
import com.appfit.data.model.ChatMessage;
import com.appfit.data.model.ChatRole;
import com.appfit.data.model.DailyPlan;
import com.appfit.data.repository.ChatRepository;
import com.appfit.notification.NotificationScheduler;
import javax.inject.Inject;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u00008\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\b\u0007\u0018\u00002\u00020\u0001B\'\b\u0007\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\u0006\u0010\u0006\u001a\u00020\u0007\u0012\u0006\u0010\b\u001a\u00020\t\u00a2\u0006\u0002\u0010\nJ\u001e\u0010\u000b\u001a\u00020\f2\u0006\u0010\r\u001a\u00020\u000e2\u0006\u0010\u000f\u001a\u00020\u0010H\u0086B\u00a2\u0006\u0002\u0010\u0011R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\b\u001a\u00020\tX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0007X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0012"}, d2 = {"Lcom/appfit/domain/usecase/SendChatMessageUseCase;", "", "chatRepository", "Lcom/appfit/data/repository/ChatRepository;", "anthropicService", "Lcom/appfit/ai/AnthropicService;", "toolExecutor", "Lcom/appfit/ai/ClaudeToolExecutor;", "notificationScheduler", "Lcom/appfit/notification/NotificationScheduler;", "(Lcom/appfit/data/repository/ChatRepository;Lcom/appfit/ai/AnthropicService;Lcom/appfit/ai/ClaudeToolExecutor;Lcom/appfit/notification/NotificationScheduler;)V", "invoke", "", "userText", "", "currentPlan", "Lcom/appfit/data/model/DailyPlan;", "(Ljava/lang/String;Lcom/appfit/data/model/DailyPlan;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "app_debug"})
public final class SendChatMessageUseCase {
    @org.jetbrains.annotations.NotNull()
    private final com.appfit.data.repository.ChatRepository chatRepository = null;
    @org.jetbrains.annotations.NotNull()
    private final com.appfit.ai.AnthropicService anthropicService = null;
    @org.jetbrains.annotations.NotNull()
    private final com.appfit.ai.ClaudeToolExecutor toolExecutor = null;
    @org.jetbrains.annotations.NotNull()
    private final com.appfit.notification.NotificationScheduler notificationScheduler = null;
    
    @javax.inject.Inject()
    public SendChatMessageUseCase(@org.jetbrains.annotations.NotNull()
    com.appfit.data.repository.ChatRepository chatRepository, @org.jetbrains.annotations.NotNull()
    com.appfit.ai.AnthropicService anthropicService, @org.jetbrains.annotations.NotNull()
    com.appfit.ai.ClaudeToolExecutor toolExecutor, @org.jetbrains.annotations.NotNull()
    com.appfit.notification.NotificationScheduler notificationScheduler) {
        super();
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object invoke(@org.jetbrains.annotations.NotNull()
    java.lang.String userText, @org.jetbrains.annotations.NotNull()
    com.appfit.data.model.DailyPlan currentPlan, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.lang.Boolean> $completion) {
        return null;
    }
}