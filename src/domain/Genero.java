package domain;

public enum Genero {
	DESCONOCIDO("Desconocido"),
    REALISMO_MAGICO("Realismo mágico"),
    DISTOPIA("Distopía"),
    CLASICO("Clásico"),
    INFANTIL("Infantil"),
    NOVELA_PSICOLOGICA("Novela psicológica"),
    ROMANTICA("Romántica"),
    MISTERIO("Misterio"),
    FANTASIA("Fantasía"),
    HISTORICA("Histórica"),
    FILOSOFICA("Filosófica"),
    TERROR("Terror"),
    SATIRICA("Satírica"),
    THRILLER("Thriller");

    private final String nombre;

    Genero(String nombre) {
        this.nombre = nombre;
    }

    public String getNombre() {
        return nombre;
    }

    @Override
    public String toString() {
        return nombre;
    }
    public static Genero fromString(String nombre) {
        for (Genero g : Genero.values()) {
            if (g.getNombre().equalsIgnoreCase(nombre.trim())) {
                return g;
            }
        }
        return DESCONOCIDO; // por defecto si no coincide
    }

}
