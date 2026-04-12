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
public final class UserProfileProvider_Factory implements Factory<UserProfileProvider> {
  private final Provider<DataStore<Preferences>> dataStoreProvider;

  public UserProfileProvider_Factory(Provider<DataStore<Preferences>> dataStoreProvider) {
    this.dataStoreProvider = dataStoreProvider;
  }

  @Override
  public UserProfileProvider get() {
    return newInstance(dataStoreProvider.get());
  }

  public static UserProfileProvider_Factory create(
      Provider<DataStore<Preferences>> dataStoreProvider) {
    return new UserProfileProvider_Factory(dataStoreProvider);
  }

  public static UserProfileProvider newInstance(DataStore<Preferences> dataStore) {
    return new UserProfileProvider(dataStore);
  }
}
