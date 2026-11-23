package boomerang.game;

import boomerang.player.Player;
import java.util.List;

/**
 * Receives game events so a view (console, network client, GUI) can show them. The game
 * never prints anything itself. All methods are optional, so a listener implements only
 * what it needs. Only public information is ever passed: a player's Throw card stays
 * hidden until {@link #roundScored}.
 */
public interface GameListener {
    default void roundStarted(int round, List<Player> players) { }

    /**
     * Called once per pass, after EVERY player has chosen their card, so nobody sees a pick
     * before making their own (rules 7-8). Each player's face-up row ({@code draftedCards()})
     * includes everything chosen so far, but never the Throw card.
     */
    default void draftStepCompleted(int step, List<Player> players) { }

    /** The last cards have been passed back and are now each player's Catch card (rule 9). */
    default void catchCardsShown(List<Player> players) { }

    /** All scoring for the round is done; the Throw cards may now be shown (rule 5). */
    default void roundScored(int round, List<Player> players) { }

    default void gameOver(GameResult result) { }
}
