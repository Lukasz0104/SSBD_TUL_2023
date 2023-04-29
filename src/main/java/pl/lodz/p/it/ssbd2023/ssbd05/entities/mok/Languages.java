package pl.lodz.p.it.ssbd2023.ssbd05.entities.mok;

public enum Languages {
    PL("PL"),
    EN("EN");

    private final String text;

    Languages(final String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return text;
    }
}
