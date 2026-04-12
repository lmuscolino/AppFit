package com.appfit.ai;

import com.anthropic.client.AnthropicClient;
import com.anthropic.client.okhttp.AnthropicOkHttpClient;
import com.anthropic.models.messages.ContentBlock;
import com.anthropic.models.messages.ContentBlockParam;
import com.anthropic.models.messages.MessageCreateParams;
import com.anthropic.models.messages.MessageParam;
import com.anthropic.models.messages.TextBlockParam;
import com.anthropic.models.messages.Tool;
import com.anthropic.models.messages.ToolResultBlockParam;
import com.anthropic.models.messages.ToolUseBlockParam;
import com.appfit.ai.prompt.SystemPromptBuilder;
import com.appfit.data.model.ChatMessage;
import com.appfit.data.model.ChatRole;
import com.appfit.data.model.DailyPlan;
import com.google.gson.Gson;
import kotlinx.coroutines.Dispatchers;
import javax.inject.Inject;
import javax.inject.Singleton;

@javax.inject.Singleton()
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000L\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\b\u0007\u0018\u00002\u00020\u0001B\u000f\b\u0007\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004J\u000e\u0010\u000b\u001a\b\u0012\u0004\u0012\u00020\r0\fH\u0002J\u0010\u0010\u000e\u001a\u00020\b2\u0006\u0010\u000f\u001a\u00020\u0006H\u0002J4\u0010\u0010\u001a\u00020\u00112\u0006\u0010\u0012\u001a\u00020\u00062\f\u0010\u0013\u001a\b\u0012\u0004\u0012\u00020\u00140\f2\u0006\u0010\u0015\u001a\u00020\u00162\u0006\u0010\u0017\u001a\u00020\u0018H\u0086@\u00a2\u0006\u0002\u0010\u0019R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0010\u0010\u0005\u001a\u0004\u0018\u00010\u0006X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u0010\u0010\u0007\u001a\u0004\u0018\u00010\bX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\t\u001a\u00020\nX\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u001a"}, d2 = {"Lcom/appfit/ai/AnthropicService;", "", "apiKeyProvider", "Lcom/appfit/ai/ApiKeyProvider;", "(Lcom/appfit/ai/ApiKeyProvider;)V", "cachedApiKey", "", "cachedClient", "Lcom/anthropic/client/AnthropicClient;", "gson", "Lcom/google/gson/Gson;", "buildTools", "", "Lcom/anthropic/models/messages/Tool;", "getClient", "apiKey", "sendMessage", "Lcom/appfit/ai/AiResponse;", "userMessage", "conversationHistory", "Lcom/appfit/data/model/ChatMessage;", "currentPlan", "Lcom/appfit/data/model/DailyPlan;", "toolExecutor", "Lcom/appfit/ai/ClaudeToolExecutor;", "(Ljava/lang/String;Ljava/util/List;Lcom/appfit/data/model/DailyPlan;Lcom/appfit/ai/ClaudeToolExecutor;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "app_debug"})
public final class AnthropicService {
    @org.jetbrains.annotations.NotNull()
    private final com.appfit.ai.ApiKeyProvider apiKeyProvider = null;
    @org.jetbrains.annotations.NotNull()
    private final com.google.gson.Gson gson = null;
    @org.jetbrains.annotations.Nullable()
    private com.anthropic.client.AnthropicClient cachedClient;
    @org.jetbrains.annotations.Nullable()
    private java.lang.String cachedApiKey;
    
    @javax.inject.Inject()
    public AnthropicService(@org.jetbrains.annotations.NotNull()
    com.appfit.ai.ApiKeyProvider apiKeyProvider) {
        super();
    }
    
    private final com.anthropic.client.AnthropicClient getClient(java.lang.String apiKey) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object sendMessage(@org.jetbrains.annotations.NotNull()
    java.lang.String userMessage, @org.jetbrains.annotations.NotNull()
    java.util.List<com.appfit.data.model.ChatMessage> conversationHistory, @org.jetbrains.annotations.NotNull()
    com.appfit.data.model.DailyPlan currentPlan, @org.jetbrains.annotations.NotNull()
    com.appfit.ai.ClaudeToolExecutor toolExecutor, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super com.appfit.ai.AiResponse> $completion) {
        return null;
    }
    
    private final java.util.List<com.anthropic.models.messages.Tool> buildTools() {
        return null;
    }
}