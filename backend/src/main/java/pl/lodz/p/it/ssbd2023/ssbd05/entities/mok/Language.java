package pl.lodz.p.it.ssbd2023.ssbd05.entities.mok;

public enum Language {
    PL("PL"),
    EN("EN");

    private final String text;

    Language(final String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return text;
    }
}
