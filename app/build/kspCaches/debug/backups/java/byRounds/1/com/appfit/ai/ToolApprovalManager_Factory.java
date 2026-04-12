package com.appfit.ai;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;

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
public final class ToolApprovalManager_Factory implements Factory<ToolApprovalManager> {
  @Override
  public ToolApprovalManager get() {
    return newInstance();
  }

  public static ToolApprovalManager_Factory create() {
    return InstanceHolder.INSTANCE;
  }

  public static ToolApprovalManager newInstance() {
    return new ToolApprovalManager();
  }

  private static final class InstanceHolder {
    private static final ToolApprovalManager_Factory INSTANCE = new ToolApprovalManager_Factory();
  }
}
