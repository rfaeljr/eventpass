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
public final class PareamentoViewModel_Factory implements Factory<PareamentoViewModel> {
  private final Provider<PortariaRepository> repositoryProvider;

  private final Provider<PreferenciasLocais> prefsProvider;

  private PareamentoViewModel_Factory(Provider<PortariaRepository> repositoryProvider,
      Provider<PreferenciasLocais> prefsProvider) {
    this.repositoryProvider = repositoryProvider;
    this.prefsProvider = prefsProvider;
  }

  @Override
  public PareamentoViewModel get() {
    return newInstance(repositoryProvider.get(), prefsProvider.get());
  }

  public static PareamentoViewModel_Factory create(Provider<PortariaRepository> repositoryProvider,
      Provider<PreferenciasLocais> prefsProvider) {
    return new PareamentoViewModel_Factory(repositoryProvider, prefsProvider);
  }

  public static PareamentoViewModel newInstance(PortariaRepository repository,
      PreferenciasLocais prefs) {
    return new PareamentoViewModel(repository, prefs);
  }
}
