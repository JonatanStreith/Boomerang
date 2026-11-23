package boomerang.model;

/** The numeric rules of Boomerang Australia in one place, so no magic numbers are scattered in the code. */
public final class GameRules {
    public static final int MIN_PLAYERS = 2;                 // rule 1
    public static final int MAX_PLAYERS = 4;                 // rule 1
    public static final int HAND_SIZE = 7;                   // rule 4
    public static final int ROUNDS = 4;                      // rule 12
    public static final int REGION_BONUS_POINTS = 3;         // rule 10b
    public static final int COLLECTION_DOUBLING_LIMIT = 7;   // rule 10c

    /** Points for 1..6 matching activity cards (rule 10e). */
    private static final int[] ACTIVITY_POINTS = {0, 2, 4, 7, 10, 15};

    private GameRules() { }

    public static int activityPoints(int matchingCards) {
        if (matchingCards <= 0) {
            return 0;
        }
        return ACTIVITY_POINTS[Math.min(matchingCards, ACTIVITY_POINTS.length) - 1];
    }
}
