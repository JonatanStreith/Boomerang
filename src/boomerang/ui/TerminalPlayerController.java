package boomerang.ui;

import boomerang.model.Activity;
import boomerang.model.Card;
import boomerang.player.PlayerController;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/** Lets a human choose cards and activities through any Terminal (local console or network). */
public final class TerminalPlayerController implements PlayerController {
    private final Terminal terminal;
    private Card throwCard;   // remembered only so the player is reminded of it while drafting

    public TerminalPlayerController(Terminal terminal) { this.terminal = terminal; }

    @Override
    public Card chooseThrowCard(List<Card> hand) {
        terminal.show("\nYour hand:\n" + format(hand));
        throwCard = askForCard(hand, "Choose your THROW card (hidden from the others until the round ends)");
        return throwCard;
    }

    @Override
    public Card chooseDraftCard(List<Card> hand, List<Card> alreadyDrafted) {
        terminal.show("\nYour hidden Throw card: " + throwCard
                + "\nYour face-up cards so far:\n" + (alreadyDrafted.isEmpty() ? "  (none)" : format(alreadyDrafted))
                + "\nYour new hand:\n" + format(hand));
        return askForCard(hand, "Choose a card to keep (everyone will see it)");
    }

    @Override
    public Optional<Activity> chooseActivity(Map<Activity, Integer> options) {
        StringBuilder text = new StringBuilder(
                "\nActivities you can still score this round (one at most, each once per game):");
        options.forEach((activity, points) -> text.append("\n  ").append(activity).append(" = ")
                .append(points).append(" points"));
        terminal.show(text.toString());
        while (true) {
            String answer = terminal.ask("Type an activity name, or press Enter to skip: ").trim();
            if (answer.isEmpty()) {
                return Optional.empty();
            }
            for (Activity activity : options.keySet()) {
                if (activity.toString().equalsIgnoreCase(answer) || activity.name().equalsIgnoreCase(answer)) {
                    return Optional.of(activity);
                }
            }
            terminal.show("Not an available activity, try again.");
        }
    }

    private Card askForCard(List<Card> hand, String prompt) {
        while (true) {
            String answer = terminal.ask(prompt + " (type its letter): ").trim();
            for (Card card : hand) {
                if (card.site().equalsIgnoreCase(answer)) {
                    return card;
                }
            }
            terminal.show("That letter is not in your hand, try again.");
        }
    }

    private static String format(List<Card> cards) {
        StringBuilder text = new StringBuilder();
        for (Card card : cards) {
            text.append(text.length() > 0 ? "\n" : "").append("  ").append(card);
        }
        return text.toString();
    }
}
