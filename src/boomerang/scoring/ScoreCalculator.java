package boomerang.scoring;

import boomerang.model.Activity;
import boomerang.model.Card;
import boomerang.model.Draft;
import boomerang.model.GameRules;
import boomerang.model.Region;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Applies the scoring rules to score sheets. It knows nothing about players, networking
 * or the console, so it can be tested with plain cards and sheets.
 */
public final class ScoreCalculator {
    private final List<ScoringRule> rules;
    private final ActivityScoring activityScoring;
    private final Map<Region, Set<String>> regionSites;

    public ScoreCalculator(List<ScoringRule> rules, ActivityScoring activityScoring,
                           Map<Region, Set<String>> regionSites) {
        this.rules = List.copyOf(rules);
        this.activityScoring = activityScoring;
        this.regionSites = regionSites;
    }

    /** The standard Boomerang Australia rules; the regions and their sites are read from the cards. */
    public static ScoreCalculator forCards(List<Card> cards) {
        Map<Region, Set<String>> sites = new EnumMap<>(Region.class);
        for (Card card : cards) {
            sites.computeIfAbsent(card.region(), r -> new HashSet<>()).add(card.site());
        }
        List<ScoringRule> rules = List.of(new ThrowAndCatchRule(), new TouristSitesRule(),
                new CollectionsRule(), new AnimalsRule());
        return new ScoreCalculator(rules, new ActivityScoring(), sites);
    }

    /**
     * Scores the automatic categories for one player and round, then marks the draft's sites
     * as visited. All rules see the visited sites from earlier rounds only.
     */
    public void scoreRound(ScoreSheet sheet, Draft draft, int round) {
        for (ScoringRule rule : rules) {
            sheet.record(rule.category(), round, rule.score(draft, sheet));
        }
        for (Card card : draft.allCards()) {
            sheet.markVisited(card);
        }
    }

    /**
     * Rule 10b-i/ii: awards the region bonus. A region already claimed in an earlier round is
     * closed; players completing it in this same round all receive the bonus.
     * Call after every player's round has been scored with {@link #scoreRound}.
     */
    public void awardRegionBonuses(List<ScoreSheet> sheets, int round) {
        Set<Region> claimedEarlier = EnumSet.noneOf(Region.class);
        for (ScoreSheet sheet : sheets) {
            claimedEarlier.addAll(sheet.bonusRegions());
        }
        List<Region> open = new ArrayList<>(regionSites.keySet());
        open.removeAll(claimedEarlier);

        Map<ScoreSheet, List<Region>> winners = new java.util.IdentityHashMap<>();
        for (ScoreSheet sheet : sheets) {
            for (Region region : open) {
                if (sheet.visitedSites().containsAll(regionSites.get(region))) {
                    winners.computeIfAbsent(sheet, s -> new ArrayList<>()).add(region);
                }
            }
        }
        for (Map.Entry<ScoreSheet, List<Region>> entry : winners.entrySet()) {
            for (Region region : entry.getValue()) {
                entry.getKey().addBonusRegion(region);
                entry.getKey().record(ScoreCategory.REGION_BONUS, round, GameRules.REGION_BONUS_POINTS);
            }
        }
    }

    /** The activities the player may still score this round, with the points each would give. */
    public Map<Activity, Integer> activityOptions(ScoreSheet sheet, Draft draft) {
        return activityScoring.options(draft, sheet);
    }

    /** Scores one chosen activity and marks it used (rule 10e). Returns the points. */
    public int scoreActivity(ScoreSheet sheet, Draft draft, int round, Activity activity) {
        if (sheet.hasScored(activity)) {
            throw new IllegalStateException(activity + " has already been scored in this game");
        }
        int points = GameRules.activityPoints(activityScoring.count(activity, draft));
        sheet.record(ScoreCategory.ACTIVITIES, round, points);
        sheet.markScored(activity);
        return points;
    }
}
