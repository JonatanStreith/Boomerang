package boomerang;

import boomerang.game.BoomerangAustralia;
import boomerang.game.CompositeGameListener;
import boomerang.game.GameListener;
import boomerang.game.NextPlayerPassDirection;
import boomerang.model.AustraliaCards;
import boomerang.model.Card;
import boomerang.model.GameRules;
import boomerang.network.GameClient;
import boomerang.network.GameServer;
import boomerang.network.Protocol;
import boomerang.network.RemoteTerminal;
import boomerang.player.Player;
import boomerang.player.PlayerController;
import boomerang.player.RandomBotController;
import boomerang.scoring.ScoreCalculator;
import boomerang.ui.ConsoleTerminal;
import boomerang.ui.TerminalGameView;
import boomerang.ui.TerminalPlayerController;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

/**
 * Entry point and wiring. This and the network classes are the only code that touches
 * System.in and System.out or opens sockets; the game itself does neither.
 */
public final class Main {
    private static final String USAGE = """
            Usage:
              java boomerang.Main local <humans 0-1> <bots>          one console, 2 to 4 players in total
              java boomerang.Main host <humans> <bots> [port]        host a network game (humans connect via 'join')
              java boomerang.Main join <host> [port]                 join a hosted game
            Examples:
              java boomerang.Main local 1 2
              java boomerang.Main host 2 1          (then each human runs: java boomerang.Main join <server-ip>)""";

    private Main() { }

    public static void main(String[] args) {
        try {
            if (args.length == 0) throw new IllegalArgumentException("Missing mode");
            switch (args[0]) {
                case "local" -> runLocal(args);
                case "host" -> runHost(args);
                case "join" -> runJoin(args);
                default -> throw new IllegalArgumentException("Unknown mode: " + args[0]);
            }
        } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
            System.err.println(USAGE);
            System.exit(1);
        } catch (IllegalArgumentException | IllegalStateException | IOException e) {
            System.err.println("Error: " + e.getMessage());
            if (e instanceof IllegalArgumentException && e.getMessage() != null && e.getMessage().startsWith("Missing")) {
                System.err.println(USAGE);
            }
            System.exit(1);
        }
    }

    private static void runLocal(String[] args) {
        int humans = Integer.parseInt(args[1]);
        int bots = Integer.parseInt(args[2]);
        if (humans < 0 || humans > 1) {
            throw new IllegalArgumentException("'local' supports 0 or 1 humans; use 'host' for several humans");
        }
        checkPlayerCount(humans + bots);

        Random random = new Random();
        ConsoleTerminal console = new ConsoleTerminal(new Scanner(System.in), System.out);
        List<Player> players = new ArrayList<>();
        if (humans == 1) {
            players.add(new Player(0, "You", new TerminalPlayerController(console)));
        }
        addBots(players, bots, random);
        play(players, new TerminalGameView(console), random);
    }

    private static void runHost(String[] args) throws IOException {
        int humans = Integer.parseInt(args[1]);
        int bots = Integer.parseInt(args[2]);
        int port = args.length > 3 ? Integer.parseInt(args[3]) : Protocol.DEFAULT_PORT;
        if (humans < 1) {
            throw new IllegalArgumentException("'host' needs at least 1 human; use 'local 0 <bots>' for bots only");
        }
        checkPlayerCount(humans + bots);

        List<RemoteTerminal> terminals = GameServer.acceptClients(humans, port, System.out);
        try {
            Random random = new Random();
            List<Player> players = new ArrayList<>();
            List<GameListener> views = new ArrayList<>();
            views.add(new TerminalGameView(ConsoleTerminal.outputOnly(System.out)));   // server log of public events
            for (int i = 0; i < humans; i++) {
                PlayerController controller = new TerminalPlayerController(terminals.get(i));
                players.add(new Player(i, "Player " + (i + 1), controller));
                views.add(new TerminalGameView(terminals.get(i)));
            }
            addBots(players, bots, random);
            play(players, new CompositeGameListener(views), random);
            terminals.forEach(t -> t.close("Game over. Thanks for playing!"));
        } catch (RuntimeException e) {
            terminals.forEach(t -> t.close("The game was aborted: " + e.getMessage()));
            throw e;
        }
    }

    private static void runJoin(String[] args) throws IOException {
        String host = args[1];
        int port = args.length > 2 ? Integer.parseInt(args[2]) : Protocol.DEFAULT_PORT;
        GameClient.run(host, port, new Scanner(System.in), System.out);
    }

    private static void addBots(List<Player> players, int bots, Random random) {
        for (int i = 0; i < bots; i++) {
            players.add(new Player(players.size(), "Bot " + (i + 1), new RandomBotController(random)));
        }
    }

    private static void play(List<Player> players, GameListener listener, Random random) {
        List<Card> cards = AustraliaCards.create();
        new BoomerangAustralia(players, cards, ScoreCalculator.forCards(cards),
                new NextPlayerPassDirection(), listener, random).play();
    }

    private static void checkPlayerCount(int total) {
        if (total < GameRules.MIN_PLAYERS || total > GameRules.MAX_PLAYERS) {
            throw new IllegalArgumentException("A game needs " + GameRules.MIN_PLAYERS + " to "
                    + GameRules.MAX_PLAYERS + " players but got " + total);
        }
    }
}
