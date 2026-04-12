package com.appfit.ai;

import androidx.datastore.core.DataStore;
import androidx.datastore.preferences.core.Preferences;
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
public final class ApiKeyProvider_Factory implements Factory<ApiKeyProvider> {
  private final Provider<DataStore<Preferences>> dataStoreProvider;

  public ApiKeyProvider_Factory(Provider<DataStore<Preferences>> dataStoreProvider) {
    this.dataStoreProvider = dataStoreProvider;
  }

  @Override
  public ApiKeyProvider get() {
    return newInstance(dataStoreProvider.get());
  }

  public static ApiKeyProvider_Factory create(Provider<DataStore<Preferences>> dataStoreProvider) {
    return new ApiKeyProvider_Factory(dataStoreProvider);
  }

  public static ApiKeyProvider newInstance(DataStore<Preferences> dataStore) {
    return new ApiKeyProvider(dataStore);
  }
}
