package boomerang.player;

import boomerang.model.Activity;
import boomerang.model.Card;
import boomerang.model.Draft;
import boomerang.model.Hand;
import boomerang.scoring.ScoreSheet;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * A player's state during a round (hand, Throw card, drafted cards, Catch card) and the
 * score sheet. Decisions are delegated to a PlayerController; the game checks every
 * decision, so a faulty controller cannot break the rules.
 */
public final class Player {
    private final int id;
    private final String name;
    private final PlayerController controller;
    private final ScoreSheet scoreSheet = new ScoreSheet();

    private Hand hand = new Hand();
    private Card throwCard;
    private final List<Card> drafted = new ArrayList<>();
    private Card catchCard;

    public Player(int id, String name, PlayerController controller) {
        this.id = id;
        this.name = Objects.requireNonNull(name, "name");
        this.controller = Objects.requireNonNull(controller, "controller");
    }

    public int id() { return id; }
    public String name() { return name; }
    public ScoreSheet scoreSheet() { return scoreSheet; }
    public Optional<Card> throwCard() { return Optional.ofNullable(throwCard); }
    public Optional<Card> catchCard() { return Optional.ofNullable(catchCard); }
    public List<Card> draftedCards() { return Collections.unmodifiableList(drafted); }
    public List<Card> handCards() { return hand.cards(); }

    /** Starts a new round with freshly dealt cards (rule 4), clearing the previous round. */
    public void startRound(List<Card> dealt) {
        hand = new Hand(dealt);
        throwCard = null;
        drafted.clear();
        catchCard = null;
    }

    /** Rule 5: the player picks the Throw card; it leaves the hand and stays hidden until the round ends. */
    public Card chooseThrowCard() {
        if (throwCard != null) {
            throw new IllegalStateException(name + " has already chosen a Throw card");
        }
        Card chosen = controller.chooseThrowCard(hand.cards());
        takeFromHand(chosen);
        throwCard = chosen;
        return chosen;
    }

    /** Rules 7-8: the player picks one card from the hand to keep face-up. */
    public Card draftCard() {
        Card chosen = controller.chooseDraftCard(hand.cards(), List.copyOf(drafted));
        takeFromHand(chosen);
        drafted.add(chosen);
        return chosen;
    }

    /** Hands over the whole hand to be passed on (rule 6). */
    public Hand passHand() {
        Hand passed = hand;
        hand = new Hand();
        return passed;
    }

    public void receiveHand(Hand received) {
        if (!hand.isEmpty()) {
            throw new IllegalStateException(name + " still holds cards and cannot receive a hand");
        }
        hand = received;
    }

    /** Rule 9: hands over the one remaining card, to be passed back as someone else's Catch card. */
    public Card passLastCard() {
        if (hand.size() != 1) {
            throw new IllegalStateException(name + " should hold exactly one card but holds " + hand.size());
        }
        Card last = hand.cards().get(0);
        hand = new Hand();
        return last;
    }

    public void receiveCatchCard(Card card) {
        if (catchCard != null) {
            throw new IllegalStateException(name + " already has a Catch card");
        }
        catchCard = Objects.requireNonNull(card, "card");
    }

    /** The finished seven-card draft (Throw, drafted cards, Catch). */
    public Draft draft() {
        if (throwCard == null || catchCard == null) {
            throw new IllegalStateException(name + "'s round is not complete: Throw or Catch card is missing");
        }
        return new Draft(throwCard, drafted, catchCard);
    }

    /** Rule 10e: asks the controller which activity to score, if any, and checks the answer. */
    public Optional<Activity> chooseActivity(Map<Activity, Integer> options) {
        Optional<Activity> choice = controller.chooseActivity(Collections.unmodifiableMap(options));
        if (choice.isPresent() && !options.containsKey(choice.get())) {
            throw new IllegalStateException(name + " chose an activity that is not available: " + choice.get());
        }
        return choice;
    }

    private void takeFromHand(Card card) {
        if (card == null || !hand.remove(card)) {
            throw new IllegalStateException(name + " chose a card that is not in the hand: " + card);
        }
    }

    @Override public String toString() { return name; }
}
