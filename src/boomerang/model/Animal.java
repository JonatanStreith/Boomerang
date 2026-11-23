package boomerang.model;

/** Animals on a card (rule 2c-ii) and the points one matching pair is worth (rule 10d). */
public enum Animal {
    KANGAROOS("Kangaroos", 3),
    EMUS("Emus", 4),
    WOMBATS("Wombats", 5),
    KOALAS("Koalas", 7),
    PLATYPUSES("Platypuses", 9);

    private final String displayName;
    private final int pairPoints;

    Animal(String displayName, int pairPoints) {
        this.displayName = displayName;
        this.pairPoints = pairPoints;
    }

    public int pairPoints() { return pairPoints; }

    @Override public String toString() { return displayName; }
}
