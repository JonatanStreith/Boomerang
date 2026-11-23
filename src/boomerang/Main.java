package boomerang;

import boomerang.game.BoomerangAustralia;
import boomerang.game.NextPlayerPassDirection;
import boomerang.model.AustraliaCards;
import boomerang.model.Card;
import boomerang.player.Player;
import boomerang.player.RandomBotController;
import boomerang.scoring.ScoreCalculator;
import boomerang.ui.ConsoleGameView;
import boomerang.ui.ConsolePlayerController;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

/**
 * Entry point and wiring. This is the only class that touches System.in and System.out.
 * Usage: java boomerang.Main [humans 0-1] [bots]   (2 to 4 players in total)
 */
public final class Main {
    private Main() { }

    public static void main(String[] args) {
        int humans;
        int bots;
        try {
            if (args.length != 2) throw new NumberFormatException();
            humans = Integer.parseInt(args[0]);
            bots = Integer.parseInt(args[1]);
        } catch (NumberFormatException e) {
            System.err.println("Usage: java boomerang.Main <humans 0-1> <bots>   (2 to 4 players in total)");
            System.err.println("Example: java boomerang.Main 1 2");
            System.exit(1);
            return;
        }
        if (humans < 0 || humans > 1 || bots < 0) {
            System.err.println("Humans must be 0 or 1 (network play is not implemented) and bots 0 or more.");
            System.exit(1);
            return;
        }

        Random random = new Random();
        Scanner in = new Scanner(System.in);
        List<Player> players = new ArrayList<>();
        for (int i = 0; i < humans; i++) {
            players.add(new Player(players.size(), "You", new ConsolePlayerController(in, System.out)));
        }
        for (int i = 0; i < bots; i++) {
            players.add(new Player(players.size(), "Bot " + (i + 1), new RandomBotController(random)));
        }

        List<Card> cards = AustraliaCards.create();
        try {
            new BoomerangAustralia(players, cards, ScoreCalculator.forCards(cards),
                    new NextPlayerPassDirection(), new ConsoleGameView(System.out), random).play();
        } catch (IllegalArgumentException | IllegalStateException e) {
            System.err.println("Error: " + e.getMessage());
            System.exit(1);
        }
    }
}
