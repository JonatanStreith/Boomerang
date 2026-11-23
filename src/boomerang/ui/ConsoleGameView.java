package boomerang.ui;

import boomerang.game.GameListener;
import boomerang.game.GameResult;
import boomerang.model.Card;
import boomerang.player.Player;
import boomerang.scoring.ScoreCategory;
import java.io.PrintStream;
import java.util.List;

/** Prints game events to a console. The only place that formats output for the game. */
public final class ConsoleGameView implements GameListener {
    private final PrintStream out;

    public ConsoleGameView(PrintStream out) { this.out = out; }

    @Override
    public void roundStarted(int round, List<Player> players) {
        out.println("\n===== Round " + (round + 1) + " =====");
    }

    @Override
    public void cardDrafted(Player player, Card card) {
        out.println(player.name() + " shows: " + card);
    }

    @Override
    public void roundScored(int round, List<Player> players) {
        out.println("\n--- Scores after round " + (round + 1) + " ---");
        for (Player player : players) {
            out.println(player.name() + "  Throw: " + player.throwCard().map(Card::toString).orElse("?")
                    + "  Catch: " + player.catchCard().map(Card::toString).orElse("?"));
            StringBuilder line = new StringBuilder("   this round: ");
            for (ScoreCategory category : ScoreCategory.values()) {
                line.append(category).append(' ').append(player.scoreSheet().pointsFor(category, round)).append("  ");
            }
            out.println(line.toString().stripTrailing());
            out.println("   total so far: " + player.scoreSheet().total());
        }
    }

    @Override
    public void gameOver(GameResult result) {
        out.println("\n===== Final scores =====");
        for (Player player : result.players()) {
            StringBuilder line = new StringBuilder(player.name() + ": " + player.scoreSheet().total() + "  (");
            for (ScoreCategory category : ScoreCategory.values()) {
                line.append(category).append(' ').append(player.scoreSheet().total(category)).append(", ");
            }
            line.setLength(line.length() - 2);
            out.println(line + ")");
        }
        StringBuilder names = new StringBuilder();
        for (Player winner : result.winners()) {
            names.append(names.length() > 0 ? " and " : "").append(winner.name());
        }
        out.println("\nThe winner" + (result.winners().size() > 1 ? "s are " : " is ") + names + "!");
    }
}
