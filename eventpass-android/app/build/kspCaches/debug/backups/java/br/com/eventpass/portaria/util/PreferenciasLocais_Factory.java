package br.com.eventpass.portaria.util;

import android.content.SharedPreferences;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Provider;
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
    "cast",
    "deprecation",
    "nullness:initialization.field.uninitialized"
})
public final class PreferenciasLocais_Factory implements Factory<PreferenciasLocais> {
  private final Provider<SharedPreferences> prefsProvider;

  private PreferenciasLocais_Factory(Provider<SharedPreferences> prefsProvider) {
    this.prefsProvider = prefsProvider;
  }

  @Override
  public PreferenciasLocais get() {
    return newInstance(prefsProvider.get());
  }

  public static PreferenciasLocais_Factory create(Provider<SharedPreferences> prefsProvider) {
    return new PreferenciasLocais_Factory(prefsProvider);
  }

  public static PreferenciasLocais newInstance(SharedPreferences prefs) {
    return new PreferenciasLocais(prefs);
  }
}
