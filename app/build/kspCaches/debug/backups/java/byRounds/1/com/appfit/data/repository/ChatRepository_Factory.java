package com.appfit.data.repository;

import com.appfit.data.local.dao.ChatMessageDao;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata("javax.inject.Singleton")
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
public final class ChatRepository_Factory implements Factory<ChatRepository> {
  private final Provider<ChatMessageDao> daoProvider;

  public ChatRepository_Factory(Provider<ChatMessageDao> daoProvider) {
    this.daoProvider = daoProvider;
  }

  @Override
  public ChatRepository get() {
    return newInstance(daoProvider.get());
  }

  public static ChatRepository_Factory create(Provider<ChatMessageDao> daoProvider) {
    return new ChatRepository_Factory(daoProvider);
  }

  public static ChatRepository newInstance(ChatMessageDao dao) {
    return new ChatRepository(dao);
  }
}
