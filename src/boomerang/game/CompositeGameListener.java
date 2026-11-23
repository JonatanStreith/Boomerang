package boomerang.game;

import boomerang.player.Player;
import java.util.List;

/** Forwards every event to several listeners, e.g. one view per connected player. */
public final class CompositeGameListener implements GameListener {
    private final List<GameListener> listeners;

    public CompositeGameListener(List<GameListener> listeners) { this.listeners = List.copyOf(listeners); }

    @Override public void roundStarted(int round, List<Player> players) {
        listeners.forEach(l -> l.roundStarted(round, players));
    }
    @Override public void draftStepCompleted(int step, List<Player> players) {
        listeners.forEach(l -> l.draftStepCompleted(step, players));
    }
    @Override public void catchCardsShown(List<Player> players) {
        listeners.forEach(l -> l.catchCardsShown(players));
    }
    @Override public void roundScored(int round, List<Player> players) {
        listeners.forEach(l -> l.roundScored(round, players));
    }
    @Override public void gameOver(GameResult result) {
        listeners.forEach(l -> l.gameOver(result));
    }
}
