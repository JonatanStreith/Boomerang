package test.testsupport;

import boomerang.model.Activity;
import boomerang.model.Card;
import boomerang.player.PlayerController;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

/**
 * A fully predictable player: always takes the first card of its hand, records everything it
 * is shown, and answers the activity question with the given policy.
 */
public final class ScriptedController implements PlayerController {
    public static final Function<Map<Activity, Integer>, Optional<Activity>> SKIP_ACTIVITY = options -> Optional.empty();
    public static final Function<Map<Activity, Integer>, Optional<Activity>> FIRST_AVAILABLE_ACTIVITY =
            options -> options.keySet().stream().findFirst();

    private final int index;
    private final Recorder recorder;
    private final Function<Map<Activity, Integer>, Optional<Activity>> activityPolicy;

    public ScriptedController(int index, Recorder recorder,
                              Function<Map<Activity, Integer>, Optional<Activity>> activityPolicy) {
        this.index = index;
        this.recorder = recorder;
        this.activityPolicy = activityPolicy;
    }

    @Override
    public Card chooseThrowCard(List<Card> hand) {
        recorder.throwHands(index).add(List.copyOf(hand));
        return hand.get(0);
    }

    @Override
    public Card chooseDraftCard(List<Card> hand, List<Card> alreadyDrafted) {
        recorder.draftHands(index).add(List.copyOf(hand));
        return hand.get(0);
    }

    @Override
    public Optional<Activity> chooseActivity(Map<Activity, Integer> options) {
        recorder.countActivityCall(index);
        return activityPolicy.apply(options);
    }
}
