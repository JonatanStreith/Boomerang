package boomerang.model;

/** Activities on a card (rule 2c-iii). Scored optionally, once per game (rule 10e). */
public enum Activity {
    SWIMMING("Swimming"),
    BUSHWALKING("Bushwalking"),
    INDIGENOUS_CULTURE("Indigenous Culture"),
    SIGHTSEEING("Sightseeing");

    private final String displayName;

    Activity(String displayName) { this.displayName = displayName; }

    @Override public String toString() { return displayName; }
}
