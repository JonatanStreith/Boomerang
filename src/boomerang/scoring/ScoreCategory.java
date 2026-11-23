package boomerang.scoring;

/** The scoring categories of the score sheet (rule 10). */
public enum ScoreCategory {
    THROW_AND_CATCH("Throw & Catch"),
    TOURIST_SITES("Tourist sites"),
    COLLECTIONS("Collections"),
    ANIMALS("Animals"),
    ACTIVITIES("Activities"),
    REGION_BONUS("Region bonuses");

    private final String displayName;

    ScoreCategory(String displayName) { this.displayName = displayName; }

    @Override public String toString() { return displayName; }
}
