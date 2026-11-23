package boomerang.scoring;

import boomerang.model.Activity;
import boomerang.model.Card;
import boomerang.model.Draft;
import boomerang.model.GameRules;
import java.util.EnumMap;
import java.util.Map;

/**
 * Rule 10e. Activities are different from the automatic rules because the player chooses
 * whether and which one to score, at most one per round and each only once per game.
 */
public final class ActivityScoring {
    /** What each activity that is still available would score for this draft. */
    public Map<Activity, Integer> options(Draft draft, ScoreSheet sheet) {
        Map<Activity, Integer> options = new EnumMap<>(Activity.class);
        for (Activity activity : Activity.values()) {
            if (!sheet.hasScored(activity)) {
                options.put(activity, GameRules.activityPoints(count(activity, draft)));
            }
        }
        return options;
    }

    public int count(Activity activity, Draft draft) {
        int count = 0;
        for (Card card : draft.allCards()) {
            if (card.activity().filter(activity::equals).isPresent()) {
                count++;
            }
        }
        return count;
    }
}
