package com.appfit.domain.usecase;

import com.appfit.ai.AiService;
import com.appfit.ai.ClaudeToolExecutor;
import com.appfit.data.repository.ChatRepository;
import com.appfit.notification.NotificationScheduler;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata
@QualifierMetadata
@DaggerGenerated
@Generated(
    value = "dagger.internal.codegen.ComponentProcessor",
    comments = "https://dagger.dev"
)
@SuppressWarnings({
    "unchecked",
    "rawtypes",
    "KotlinInternal",
    "KotlinInternalInJava",
    "cast"
})
public final class SendChatMessageUseCase_Factory implements Factory<SendChatMessageUseCase> {
  private final Provider<ChatRepository> chatRepositoryProvider;

  private final Provider<AiService> aiServiceProvider;

  private final Provider<ClaudeToolExecutor> toolExecutorProvider;

  private final Provider<NotificationScheduler> notificationSchedulerProvider;

  public SendChatMessageUseCase_Factory(Provider<ChatRepository> chatRepositoryProvider,
      Provider<AiService> aiServiceProvider, Provider<ClaudeToolExecutor> toolExecutorProvider,
      Provider<NotificationScheduler> notificationSchedulerProvider) {
    this.chatRepositoryProvider = chatRepositoryProvider;
    this.aiServiceProvider = aiServiceProvider;
    this.toolExecutorProvider = toolExecutorProvider;
    this.notificationSchedulerProvider = notificationSchedulerProvider;
  }

  @Override
  public SendChatMessageUseCase get() {
    return newInstance(chatRepositoryProvider.get(), aiServiceProvider.get(), toolExecutorProvider.get(), notificationSchedulerProvider.get());
  }

  public static SendChatMessageUseCase_Factory create(
      Provider<ChatRepository> chatRepositoryProvider, Provider<AiService> aiServiceProvider,
      Provider<ClaudeToolExecutor> toolExecutorProvider,
      Provider<NotificationScheduler> notificationSchedulerProvider) {
    return new SendChatMessageUseCase_Factory(chatRepositoryProvider, aiServiceProvider, toolExecutorProvider, notificationSchedulerProvider);
  }

  public static SendChatMessageUseCase newInstance(ChatRepository chatRepository,
      AiService aiService, ClaudeToolExecutor toolExecutor,
      NotificationScheduler notificationScheduler) {
    return new SendChatMessageUseCase(chatRepository, aiService, toolExecutor, notificationScheduler);
  }
}
