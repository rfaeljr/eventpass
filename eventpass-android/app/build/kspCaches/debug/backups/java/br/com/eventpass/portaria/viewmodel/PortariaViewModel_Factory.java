package br.com.eventpass.portaria.viewmodel;

import br.com.eventpass.portaria.data.repository.PortariaRepository;
import br.com.eventpass.portaria.util.PreferenciasLocais;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Provider;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;

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
    "cast",
    "deprecation",
    "nullness:initialization.field.uninitialized"
})
public final class PortariaViewModel_Factory implements Factory<PortariaViewModel> {
  private final Provider<PortariaRepository> repositoryProvider;

  private final Provider<PreferenciasLocais> prefsProvider;

  private PortariaViewModel_Factory(Provider<PortariaRepository> repositoryProvider,
      Provider<PreferenciasLocais> prefsProvider) {
    this.repositoryProvider = repositoryProvider;
    this.prefsProvider = prefsProvider;
  }

  @Override
  public PortariaViewModel get() {
    return newInstance(repositoryProvider.get(), prefsProvider.get());
  }

  public static PortariaViewModel_Factory create(Provider<PortariaRepository> repositoryProvider,
      Provider<PreferenciasLocais> prefsProvider) {
    return new PortariaViewModel_Factory(repositoryProvider, prefsProvider);
  }

  public static PortariaViewModel newInstance(PortariaRepository repository,
      PreferenciasLocais prefs) {
    return new PortariaViewModel(repository, prefs);
  }
}
