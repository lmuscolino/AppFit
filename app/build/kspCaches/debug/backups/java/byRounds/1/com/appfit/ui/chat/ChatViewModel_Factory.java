package com.appfit.ui.chat;

import com.appfit.ai.AiDebugLogger;
import com.appfit.ai.ToolApprovalManager;
import com.appfit.data.repository.ChatRepository;
import com.appfit.domain.usecase.GetDailyPlanUseCase;
import com.appfit.domain.usecase.SendChatMessageUseCase;
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
public final class ChatViewModel_Factory implements Factory<ChatViewModel> {
  private final Provider<ChatRepository> chatRepositoryProvider;

  private final Provider<SendChatMessageUseCase> sendChatMessageUseCaseProvider;

  private final Provider<GetDailyPlanUseCase> getDailyPlanUseCaseProvider;

  private final Provider<AiDebugLogger> aiDebugLoggerProvider;

  private final Provider<ToolApprovalManager> toolApprovalManagerProvider;

  public ChatViewModel_Factory(Provider<ChatRepository> chatRepositoryProvider,
      Provider<SendChatMessageUseCase> sendChatMessageUseCaseProvider,
      Provider<GetDailyPlanUseCase> getDailyPlanUseCaseProvider,
      Provider<AiDebugLogger> aiDebugLoggerProvider,
      Provider<ToolApprovalManager> toolApprovalManagerProvider) {
    this.chatRepositoryProvider = chatRepositoryProvider;
    this.sendChatMessageUseCaseProvider = sendChatMessageUseCaseProvider;
    this.getDailyPlanUseCaseProvider = getDailyPlanUseCaseProvider;
    this.aiDebugLoggerProvider = aiDebugLoggerProvider;
    this.toolApprovalManagerProvider = toolApprovalManagerProvider;
  }

  @Override
  public ChatViewModel get() {
    return newInstance(chatRepositoryProvider.get(), sendChatMessageUseCaseProvider.get(), getDailyPlanUseCaseProvider.get(), aiDebugLoggerProvider.get(), toolApprovalManagerProvider.get());
  }

  public static ChatViewModel_Factory create(Provider<ChatRepository> chatRepositoryProvider,
      Provider<SendChatMessageUseCase> sendChatMessageUseCaseProvider,
      Provider<GetDailyPlanUseCase> getDailyPlanUseCaseProvider,
      Provider<AiDebugLogger> aiDebugLoggerProvider,
      Provider<ToolApprovalManager> toolApprovalManagerProvider) {
    return new ChatViewModel_Factory(chatRepositoryProvider, sendChatMessageUseCaseProvider, getDailyPlanUseCaseProvider, aiDebugLoggerProvider, toolApprovalManagerProvider);
  }

  public static ChatViewModel newInstance(ChatRepository chatRepository,
      SendChatMessageUseCase sendChatMessageUseCase, GetDailyPlanUseCase getDailyPlanUseCase,
      AiDebugLogger aiDebugLogger, ToolApprovalManager toolApprovalManager) {
    return new ChatViewModel(chatRepository, sendChatMessageUseCase, getDailyPlanUseCase, aiDebugLogger, toolApprovalManager);
  }
}
