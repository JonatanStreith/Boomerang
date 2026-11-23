package boomerang.game;

import boomerang.model.Activity;
import boomerang.model.Card;
import boomerang.model.Deck;
import boomerang.model.Draft;
import boomerang.model.GameRules;
import boomerang.model.Hand;
import boomerang.player.Player;
import boomerang.scoring.ScoreCalculator;
import boomerang.scoring.ScoreSheet;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Random;

/**
 * The game flow: four rounds of deal, Throw, draft and score, then the winner.
 * This class contains no console, file or network code. Players are reached through
 * their controllers, output goes to a GameListener, and everything else is injected.
 */
public final class BoomerangAustralia {
    private final List<Player> players;
    private final List<Card> cards;
    private final ScoreCalculator calculator;
    private final PassDirection direction;
    private final GameListener listener;
    private final Random random;
    private final WinnerSelector winnerSelector = new WinnerSelector();

    public BoomerangAustralia(List<Player> players, List<Card> cards, ScoreCalculator calculator,
                              PassDirection direction, GameListener listener, Random random) {
        this.players = List.copyOf(Objects.requireNonNull(players, "players"));
        this.cards = List.copyOf(Objects.requireNonNull(cards, "cards"));
        this.calculator = Objects.requireNonNull(calculator, "calculator");
        this.direction = Objects.requireNonNull(direction, "direction");
        this.listener = Objects.requireNonNull(listener, "listener");
        this.random = Objects.requireNonNull(random, "random");

        if (players.size() < GameRules.MIN_PLAYERS || players.size() > GameRules.MAX_PLAYERS) {
            throw new IllegalArgumentException("A game needs " + GameRules.MIN_PLAYERS + " to "
                    + GameRules.MAX_PLAYERS + " players but got " + players.size());
        }
        if (cards.size() < players.size() * GameRules.HAND_SIZE) {
            throw new IllegalArgumentException("Not enough cards (" + cards.size() + ") for "
                    + players.size() + " players");
        }
    }

    /** Plays all rounds (rule 11-12) and returns the result. */
    public GameResult play() {
        for (int round = 0; round < GameRules.ROUNDS; round++) {
            playRound(round);
        }
        GameResult result = new GameResult(players, winnerSelector.select(players));
        listener.gameOver(result);
        return result;
    }

    /** One round (rules 3-10). {@code round} is 0-based. */
    public void playRound(int round) {
        dealCards();
        listener.roundStarted(round, players);

        for (Player player : players) {
            player.chooseThrowCard();                                  // rule 5
        }
        int step = direction.step(round);
        int drafts = GameRules.HAND_SIZE - 2;                           // everything except Throw and Catch
        for (int i = 0; i < drafts; i++) {
            passHands(step);                                            // rule 6
            for (Player player : players) {
                Card card = player.draftCard();                         // rule 7
                listener.cardDrafted(player, card);
            }
        }
        passLastCards(step);                                            // rule 9
        scoreRound(round);                                              // rule 10
        listener.roundScored(round, players);
    }

    private void dealCards() {
        Deck deck = new Deck(cards, random);
        deck.shuffle();                                                 // rule 3
        for (Player player : players) {
            player.startRound(deck.deal(GameRules.HAND_SIZE));          // rule 4
        }
    }

    /** Every player passes the whole hand "step" seats on; the last player passes to the first. */
    private void passHands(int step) {
        List<Hand> hands = new ArrayList<>();
        for (Player player : players) {
            hands.add(player.passHand());
        }
        for (int i = 0; i < players.size(); i++) {
            players.get(Math.floorMod(i + step, players.size())).receiveHand(hands.get(i));
        }
    }

    /** The final card goes back the opposite way and becomes the receiver's Catch card. */
    private void passLastCards(int step) {
        List<Card> last = new ArrayList<>();
        for (Player player : players) {
            last.add(player.passLastCard());
        }
        for (int i = 0; i < players.size(); i++) {
            players.get(Math.floorMod(i - step, players.size())).receiveCatchCard(last.get(i));
        }
    }

    private void scoreRound(int round) {
        List<ScoreSheet> sheets = new ArrayList<>();
        for (Player player : players) {
            calculator.scoreRound(player.scoreSheet(), player.draft(), round);
            sheets.add(player.scoreSheet());
        }
        calculator.awardRegionBonuses(sheets, round);

        for (Player player : players) {
            Draft draft = player.draft();
            Map<Activity, Integer> options = calculator.activityOptions(player.scoreSheet(), draft);
            Optional<Activity> choice = options.isEmpty() ? Optional.empty() : player.chooseActivity(options);
            choice.ifPresent(activity -> calculator.scoreActivity(player.scoreSheet(), draft, round, activity));
        }
    }
}
