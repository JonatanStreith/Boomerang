package boomerang.model;

/** Collectable items on a card (rule 2c-i) and their point values (rule 10c). */
public enum CollectionType {
    LEAVES("Leaves", 1),
    WILDFLOWERS("Wildflowers", 2),
    SHELLS("Shells", 3),
    SOUVENIRS("Souvenirs", 5);

    private final String displayName;
    private final int value;

    CollectionType(String displayName, int value) {
        this.displayName = displayName;
        this.value = value;
    }

    public int value() { return value; }

    @Override public String toString() { return displayName; }
}
