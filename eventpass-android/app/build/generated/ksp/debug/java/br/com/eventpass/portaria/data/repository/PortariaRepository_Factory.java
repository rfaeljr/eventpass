package br.com.eventpass.portaria.data.repository;

import br.com.eventpass.portaria.data.api.EventPassApi;
import br.com.eventpass.portaria.util.PreferenciasLocais;
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
public final class PortariaRepository_Factory implements Factory<PortariaRepository> {
  private final Provider<EventPassApi> apiProvider;

  private final Provider<PreferenciasLocais> prefsProvider;

  private PortariaRepository_Factory(Provider<EventPassApi> apiProvider,
      Provider<PreferenciasLocais> prefsProvider) {
    this.apiProvider = apiProvider;
    this.prefsProvider = prefsProvider;
  }

  @Override
  public PortariaRepository get() {
    return newInstance(apiProvider.get(), prefsProvider.get());
  }

  public static PortariaRepository_Factory create(Provider<EventPassApi> apiProvider,
      Provider<PreferenciasLocais> prefsProvider) {
    return new PortariaRepository_Factory(apiProvider, prefsProvider);
  }

  public static PortariaRepository newInstance(EventPassApi api, PreferenciasLocais prefs) {
    return new PortariaRepository(api, prefs);
  }
}
