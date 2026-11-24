package test.testsupport;

import boomerang.model.Card;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** Remembers what every ScriptedController was shown, so tests can check how cards moved. */
public final class Recorder {
    private final Map<Integer, List<List<Card>>> throwHands = new HashMap<>();
    private final Map<Integer, List<List<Card>>> draftHands = new HashMap<>();
    private final Map<Integer, Integer> activityCalls = new HashMap<>();

    /** The hand player {@code player} saw when choosing the Throw card, one entry per round. */
    public List<List<Card>> throwHands(int player) {
        return throwHands.computeIfAbsent(player, p -> new ArrayList<>());
    }

    /** The hands player {@code player} saw when drafting, in order: five entries per round. */
    public List<List<Card>> draftHands(int player) {
        return draftHands.computeIfAbsent(player, p -> new ArrayList<>());
    }

    public int activityCalls(int player) { return activityCalls.getOrDefault(player, 0); }

    void countActivityCall(int player) { activityCalls.merge(player, 1, Integer::sum); }
}
