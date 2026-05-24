package br.com.eventpass.portaria.di;

import br.com.eventpass.portaria.data.api.EventPassApi;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.Provider;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import retrofit2.Retrofit;

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
public final class AppModule_ProvideEventPassApiFactory implements Factory<EventPassApi> {
  private final Provider<Retrofit> retrofitProvider;

  private AppModule_ProvideEventPassApiFactory(Provider<Retrofit> retrofitProvider) {
    this.retrofitProvider = retrofitProvider;
  }

  @Override
  public EventPassApi get() {
    return provideEventPassApi(retrofitProvider.get());
  }

  public static AppModule_ProvideEventPassApiFactory create(Provider<Retrofit> retrofitProvider) {
    return new AppModule_ProvideEventPassApiFactory(retrofitProvider);
  }

  public static EventPassApi provideEventPassApi(Retrofit retrofit) {
    return Preconditions.checkNotNullFromProvides(AppModule.INSTANCE.provideEventPassApi(retrofit));
  }
}
