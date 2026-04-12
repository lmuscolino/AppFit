package com.appfit.ui.debug;

import com.appfit.ai.AiDebugLogger;
import com.appfit.data.local.AppDatabase;
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
public final class DbViewerViewModel_Factory implements Factory<DbViewerViewModel> {
  private final Provider<AppDatabase> dbProvider;

  private final Provider<AiDebugLogger> aiDebugLoggerProvider;

  public DbViewerViewModel_Factory(Provider<AppDatabase> dbProvider,
      Provider<AiDebugLogger> aiDebugLoggerProvider) {
    this.dbProvider = dbProvider;
    this.aiDebugLoggerProvider = aiDebugLoggerProvider;
  }

  @Override
  public DbViewerViewModel get() {
    return newInstance(dbProvider.get(), aiDebugLoggerProvider.get());
  }

  public static DbViewerViewModel_Factory create(Provider<AppDatabase> dbProvider,
      Provider<AiDebugLogger> aiDebugLoggerProvider) {
    return new DbViewerViewModel_Factory(dbProvider, aiDebugLoggerProvider);
  }

  public static DbViewerViewModel newInstance(AppDatabase db, AiDebugLogger aiDebugLogger) {
    return new DbViewerViewModel(db, aiDebugLogger);
  }
}
