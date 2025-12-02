package screens.scenes;

public enum ScenesEnum {
    DEFAULT("Painel (padrão)"),
    BATCH_SIMULATION("Simulação Batch"),
    TRAFFIC_LIGHT("Semáforo");

    private final String label;

    ScenesEnum(String label) {
        this.label = label;
    }

    @Override
    public String toString() {
        return label;
    }
}
