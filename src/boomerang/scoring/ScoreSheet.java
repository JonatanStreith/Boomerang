package boomerang.scoring;

import boomerang.model.Activity;
import boomerang.model.Card;
import boomerang.model.GameRules;
import boomerang.model.Region;
import java.util.Collections;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

/**
 * One player's score sheet: points per category and round, plus the things the sheet
 * "crosses out" (visited sites, scored activities, claimed region bonuses).
 * It only stores and sums; the rules that decide the points live in ScoreCalculator.
 */
public final class ScoreSheet {
    private final Map<ScoreCategory, int[]> points = new EnumMap<>(ScoreCategory.class);
    private final Set<String> visitedSites = new TreeSet<>();
    private final Set<Activity> scoredActivities = EnumSet.noneOf(Activity.class);
    private final Set<Region> bonusRegions = EnumSet.noneOf(Region.class);

    public ScoreSheet() {
        for (ScoreCategory category : ScoreCategory.values()) {
            points.put(category, new int[GameRules.ROUNDS]);
        }
    }

    /** Adds points to a category for a round (0-based). Several additions in a round accumulate. */
    public void record(ScoreCategory category, int round, int pointsToAdd) {
        points.get(category)[checkRound(round)] += pointsToAdd;
    }

    public int pointsFor(ScoreCategory category, int round) {
        return points.get(category)[checkRound(round)];
    }

    public int total(ScoreCategory category) {
        int sum = 0;
        for (int p : points.get(category)) sum += p;
        return sum;
    }

    /** The grand total over every category and round (rule 12). */
    public int total() {
        int sum = 0;
        for (ScoreCategory category : ScoreCategory.values()) sum += total(category);
        return sum;
    }

    public Set<String> visitedSites() { return Collections.unmodifiableSet(visitedSites); }
    public void markVisited(Card card) { visitedSites.add(card.site()); }

    public boolean hasScored(Activity activity) { return scoredActivities.contains(activity); }
    public void markScored(Activity activity) { scoredActivities.add(activity); }

    public Set<Region> bonusRegions() { return Collections.unmodifiableSet(bonusRegions); }
    public void addBonusRegion(Region region) { bonusRegions.add(region); }

    private static int checkRound(int round) {
        if (round < 0 || round >= GameRules.ROUNDS) {
            throw new IllegalArgumentException("Round must be 0.." + (GameRules.ROUNDS - 1) + " but was " + round);
        }
        return round;
    }
}
