package boomerang.game;

import boomerang.model.Card;
import boomerang.player.Player;
import java.util.List;

/**
 * Receives game events so a view (console, GUI, network) can show them. The game never
 * prints anything itself. All methods are optional, so a listener implements only what it needs.
 */
public interface GameListener {
    default void roundStarted(int round, List<Player> players) { }

    /** A card was drafted face-up (rule 7). The hidden Throw card is never announced here. */
    default void cardDrafted(Player player, Card card) { }

    /** All scoring for the round is done; Throw and Catch cards may now be shown (rule 5). */
    default void roundScored(int round, List<Player> players) { }

    default void gameOver(GameResult result) { }
}
