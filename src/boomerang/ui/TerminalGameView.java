package boomerang.ui;

import boomerang.game.GameListener;
import boomerang.game.GameResult;
import boomerang.model.Card;
import boomerang.player.Player;
import boomerang.scoring.ScoreCategory;
import java.util.List;

/**
 * Shows game events on one Terminal. Every human gets their own view, so everybody sees
 * all face-up cards (rules 7-8) while the Throw cards stay hidden until the round is scored.
 */
public final class TerminalGameView implements GameListener {
    private final Terminal terminal;

    public TerminalGameView(Terminal terminal) { this.terminal = terminal; }

    @Override
    public void roundStarted(int round, List<Player> players) {
        terminal.show("\n===== Round " + (round + 1) + " =====");
    }

    @Override
    public void draftStepCompleted(int step, List<Player> players) {
        StringBuilder text = new StringBuilder("\n--- Face-up cards after pass " + (step + 1) + " ---");
        for (Player player : players) {
            text.append('\n').append(player.name()).append(':');
            for (Card card : player.draftedCards()) {
                text.append("\n    ").append(card);
            }
        }
        terminal.show(text.toString());
    }

    @Override
    public void catchCardsShown(List<Player> players) {
        StringBuilder text = new StringBuilder("\n--- Catch cards (the last card, passed back) ---");
        for (Player player : players) {
            text.append('\n').append(player.name()).append(": ")
                    .append(player.catchCard().map(Card::toString).orElse("?"));
        }
        terminal.show(text.toString());
    }

    @Override
    public void roundScored(int round, List<Player> players) {
        StringBuilder text = new StringBuilder("\n--- Scores after round " + (round + 1) + " ---");
        for (Player player : players) {
            text.append('\n').append(player.name())
                    .append("\n   Throw card: ").append(player.throwCard().map(Card::toString).orElse("?"))
                    .append("\n   this round:");
            for (ScoreCategory category : ScoreCategory.values()) {
                text.append("  ").append(category).append(' ').append(player.scoreSheet().pointsFor(category, round));
            }
            text.append("\n   total so far: ").append(player.scoreSheet().total());
        }
        terminal.show(text.toString());
    }

    @Override
    public void gameOver(GameResult result) {
        StringBuilder text = new StringBuilder("\n===== Final scores =====");
        for (Player player : result.players()) {
            text.append('\n').append(player.name()).append(": ").append(player.scoreSheet().total()).append("  (");
            ScoreCategory[] categories = ScoreCategory.values();
            for (int i = 0; i < categories.length; i++) {
                text.append(i > 0 ? ", " : "").append(categories[i]).append(' ')
                        .append(player.scoreSheet().total(categories[i]));
            }
            text.append(')');
        }
        StringBuilder names = new StringBuilder();
        for (Player winner : result.winners()) {
            names.append(names.length() > 0 ? " and " : "").append(winner.name());
        }
        text.append("\n\nThe winner").append(result.winners().size() > 1 ? "s are " : " is ").append(names).append('!');
        terminal.show(text.toString());
    }
}
