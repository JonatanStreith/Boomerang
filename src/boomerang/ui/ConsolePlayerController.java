package boomerang.ui;

import boomerang.model.Activity;
import boomerang.model.Card;
import boomerang.player.PlayerController;
import java.io.PrintStream;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Scanner;

/** Lets a human choose cards and activities on a console. The streams are injected, not hard-wired. */
public final class ConsolePlayerController implements PlayerController {
    private final Scanner in;
    private final PrintStream out;

    public ConsolePlayerController(Scanner in, PrintStream out) {
        this.in = in;
        this.out = out;
    }

    @Override
    public Card chooseThrowCard(List<Card> hand) {
        out.println("\nYour hand:");
        hand.forEach(card -> out.println("  " + card));
        return askForCard(hand, "Choose your THROW card (hidden from the others until the round ends)");
    }

    @Override
    public Card chooseDraftCard(List<Card> hand, List<Card> alreadyDrafted) {
        out.println("\nYour cards so far: " + alreadyDrafted.size());
        alreadyDrafted.forEach(card -> out.println("  " + card));
        out.println("Your new hand:");
        hand.forEach(card -> out.println("  " + card));
        return askForCard(hand, "Choose a card to keep");
    }

    @Override
    public Optional<Activity> chooseActivity(Map<Activity, Integer> options) {
        out.println("\nActivities you can still score this round (one at most, each once per game):");
        options.forEach((activity, points) -> out.println("  " + activity + " = " + points + " points"));
        while (true) {
            out.print("Type an activity name, or press Enter to skip: ");
            String answer = readLine().trim();
            if (answer.isEmpty()) {
                return Optional.empty();
            }
            for (Activity activity : options.keySet()) {
                if (activity.toString().equalsIgnoreCase(answer) || activity.name().equalsIgnoreCase(answer)) {
                    return Optional.of(activity);
                }
            }
            out.println("Not an available activity, try again.");
        }
    }

    private Card askForCard(List<Card> hand, String prompt) {
        while (true) {
            out.print(prompt + " (type its letter): ");
            String answer = readLine().trim();
            for (Card card : hand) {
                if (card.site().equalsIgnoreCase(answer)) {
                    return card;
                }
            }
            out.println("That letter is not in your hand, try again.");
        }
    }

    private String readLine() {
        if (!in.hasNextLine()) {
            throw new IllegalStateException("Input was closed before the game finished");
        }
        return in.nextLine();
    }
}
