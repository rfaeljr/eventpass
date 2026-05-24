package br.com.eventpass.domain.enums;

public enum PlanoSlug {
    STARTER("STARTER"),
    POR_EVENTO("POR_EVENTO"),
    MENSAL("MENSAL"),
    ANUAL("ANUAL");

    private final String slug;

    // O construtor do enum é sempre privado internamente
    PlanoSlug(String slug) {
        this.slug = slug;
    }

    // Método para recuperar o valor em String
    public String getSlug() {
        return this.slug;
    }
}