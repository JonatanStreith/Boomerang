package test.testsupport;

import boomerang.game.GameListener;
import boomerang.game.GameResult;
import boomerang.model.Card;
import boomerang.model.Draft;
import boomerang.model.Activity;
import boomerang.player.Player;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/** Records every public event of a game. All lists are copies, so later changes cannot alter them. */
public final class RecordingListener implements GameListener {
    public int roundsStarted;
    public int gameOvers;
    public GameResult result;
    /** One entry per draft step (five per round); each holds every player's face-up row. */
    public final List<List<List<Card>>> draftSteps = new ArrayList<>();
    /** One entry per round: each player's Catch card as announced. */
    public final List<List<Card>> catchCards = new ArrayList<>();
    /** One entry per round: each player's Throw card as revealed at scoring. */
    public final List<List<Card>> revealedThrows = new ArrayList<>();
    /** One entry per round: each player's finished draft. */
    public final List<List<Draft>> drafts = new ArrayList<>();
    /** One entry per round: how many activities each player has scored so far. */
    public final List<List<Integer>> scoredActivityCounts = new ArrayList<>();
    /** Counts any Throw card that was visible in a face-up row before scoring. Must stay 0. */
    public int throwCardLeaks;

    @Override
    public void roundStarted(int round, List<Player> players) { roundsStarted++; }

    @Override
    public void draftStepCompleted(int step, List<Player> players) {
        Set<Card> hidden = new HashSet<>();
        for (Player player : players) {
            player.throwCard().ifPresent(hidden::add);
        }
        List<List<Card>> rows = new ArrayList<>();
        for (Player player : players) {
            List<Card> row = List.copyOf(player.draftedCards());
            for (Card card : row) {
                if (hidden.contains(card)) throwCardLeaks++;
            }
            rows.add(row);
        }
        draftSteps.add(rows);
    }

    @Override
    public void catchCardsShown(List<Player> players) {
        List<Card> catches = new ArrayList<>();
        for (Player player : players) {
            catches.add(player.catchCard().orElseThrow());
        }
        catchCards.add(catches);
    }

    @Override
    public void roundScored(int round, List<Player> players) {
        List<Card> throwsShown = new ArrayList<>();
        List<Draft> finished = new ArrayList<>();
        List<Integer> activityCounts = new ArrayList<>();
        for (Player player : players) {
            throwsShown.add(player.throwCard().orElseThrow());
            finished.add(player.draft());
            int scored = 0;
            for (Activity activity : Activity.values()) {
                if (player.scoreSheet().hasScored(activity)) scored++;
            }
            activityCounts.add(scored);
        }
        revealedThrows.add(throwsShown);
        drafts.add(finished);
        scoredActivityCounts.add(activityCounts);
    }

    @Override
    public void gameOver(GameResult game) {
        gameOvers++;
        result = game;
    }
}
