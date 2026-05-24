package br.com.eventpass.portaria.di;

import android.content.Context;
import android.content.SharedPreferences;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.Provider;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;

@ScopeMetadata("javax.inject.Singleton")
@QualifierMetadata("dagger.hilt.android.qualifiers.ApplicationContext")
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
public final class AppModule_ProvideSharedPreferencesFactory implements Factory<SharedPreferences> {
  private final Provider<Context> ctxProvider;

  private AppModule_ProvideSharedPreferencesFactory(Provider<Context> ctxProvider) {
    this.ctxProvider = ctxProvider;
  }

  @Override
  public SharedPreferences get() {
    return provideSharedPreferences(ctxProvider.get());
  }

  public static AppModule_ProvideSharedPreferencesFactory create(Provider<Context> ctxProvider) {
    return new AppModule_ProvideSharedPreferencesFactory(ctxProvider);
  }

  public static SharedPreferences provideSharedPreferences(Context ctx) {
    return Preconditions.checkNotNullFromProvides(AppModule.INSTANCE.provideSharedPreferences(ctx));
  }
}
