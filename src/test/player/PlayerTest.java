package test.player;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import boomerang.model.Activity;
import boomerang.model.AustraliaCards;
import boomerang.model.Card;
import boomerang.model.Draft;
import boomerang.model.Hand;
import boomerang.player.Player;
import boomerang.player.PlayerController;
import test.testsupport.Recorder;
import test.testsupport.ScriptedController;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/** Requirements 5, 7, 9 and 11 at the level of a single player. */
public class PlayerTest {
    private final List<Card> deck = AustraliaCards.create();
    private Player player;

    @BeforeEach
    public void setUp() {
        player = new Player(0, "P0", new ScriptedController(0, new Recorder(), ScriptedController.SKIP_ACTIVITY));
        player.startRound(deck.subList(0, 7));
    }

    @Test
    @DisplayName("Req 5: choosing the Throw card takes it out of the hand and keeps it separate")
    public void req5_throwCardLeavesTheHand() {
        Card chosen = player.chooseThrowCard();

        assertEquals(deck.get(0), chosen);
        assertEquals(6, player.handCards().size());
        assertFalse(player.handCards().contains(chosen));
        assertEquals(Optional.of(chosen), player.throwCard());
        assertTrue(player.draftedCards().isEmpty());
    }

    @Test
    @DisplayName("Req 5: only one Throw card can be chosen per round")
    public void req5_throwCardCanOnlyBeChosenOnce() {
        player.chooseThrowCard();
        assertThrows(IllegalStateException.class, () -> player.chooseThrowCard());
    }

    @Test
    @DisplayName("Req 5: a Throw card that is not in the hand is rejected")
    public void req5_throwCardMustComeFromTheHand() {
        PlayerController cheater = new PlayerController() {
            public Card chooseThrowCard(List<Card> hand) { return deck.get(20); }
            public Card chooseDraftCard(List<Card> hand, List<Card> drafted) { return hand.get(0); }
            public Optional<Activity> chooseActivity(Map<Activity, Integer> options) { return Optional.empty(); }
        };
        Player cheating = new Player(1, "cheat", cheater);
        cheating.startRound(deck.subList(0, 7));
        assertThrows(IllegalStateException.class, () -> cheating.chooseThrowCard());
        assertEquals(7, cheating.handCards().size());
    }

    @Test
    @DisplayName("Req 7: drafting moves a card from the hand to the face-up row")
    public void req7_draftedCardBecomesFaceUp() {
        player.chooseThrowCard();
        Card drafted = player.draftCard();

        assertEquals(List.of(drafted), player.draftedCards());
        assertEquals(5, player.handCards().size());
        assertFalse(player.draftedCards().contains(player.throwCard().orElseThrow()));
    }

    @Test
    @DisplayName("Req 7: a drafted card that is not in the hand is rejected")
    public void req7_draftedCardMustComeFromTheHand() {
        PlayerController cheater = new PlayerController() {
            public Card chooseThrowCard(List<Card> hand) { return hand.get(0); }
            public Card chooseDraftCard(List<Card> hand, List<Card> drafted) { return deck.get(25); }
            public Optional<Activity> chooseActivity(Map<Activity, Integer> options) { return Optional.empty(); }
        };
        Player cheating = new Player(1, "cheat", cheater);
        cheating.startRound(deck.subList(0, 7));
        cheating.chooseThrowCard();
        assertThrows(IllegalStateException.class, () -> cheating.draftCard());
        assertTrue(cheating.draftedCards().isEmpty());
    }

    @Test
    @DisplayName("Req 9: Throw, five drafted cards and the Catch card make a draft of seven cards")
    public void req9_roundCompletesWithACatchCard() {
        Card throwCard = player.chooseThrowCard();
        for (int i = 0; i < 5; i++) {
            Hand hand = player.passHand();
            player.receiveHand(hand);
            player.draftCard();
        }
        Card last = player.passLastCard();
        player.receiveCatchCard(last);

        Draft draft = player.draft();
        assertEquals(throwCard, draft.throwCard());
        assertEquals(last, draft.catchCard());
        assertEquals(5, draft.drafted().size());
        assertEquals(7, draft.allCards().size());
        assertEquals(7, new java.util.HashSet<>(draft.allCards()).size());
    }

    @Test
    @DisplayName("Req 9: the draft is not available before the Catch card has arrived")
    public void req9_draftNeedsThrowAndCatch() {
        assertThrows(IllegalStateException.class, () -> player.draft());
        player.chooseThrowCard();
        assertThrows(IllegalStateException.class, () -> player.draft());
    }

    @Test
    @DisplayName("Req 9: the last card can only be passed when exactly one card is left")
    public void req9_lastCardNeedsExactlyOneCard() {
        assertThrows(IllegalStateException.class, () -> player.passLastCard());
    }

    @Test
    @DisplayName("Req 6: a hand can only be received by a player who has passed theirs on")
    public void req6_cannotReceiveWhileHoldingCards() {
        assertThrows(IllegalStateException.class, () -> player.receiveHand(new Hand(deck.subList(7, 12))));
    }

    @Test
    @DisplayName("Req 11: starting a new round clears the previous round")
    public void req11_newRoundClearsOldState() {
        player.chooseThrowCard();
        player.draftCard();
        player.startRound(deck.subList(7, 14));

        assertEquals(7, player.handCards().size());
        assertEquals(deck.subList(7, 14), player.handCards());
        assertTrue(player.throwCard().isEmpty());
        assertTrue(player.catchCard().isEmpty());
        assertTrue(player.draftedCards().isEmpty());
    }

    @Test
    @DisplayName("Req 10e: an activity that is not among the options is rejected")
    public void req10e_unavailableActivityIsRejected() {
        PlayerController cheater = new PlayerController() {
            public Card chooseThrowCard(List<Card> hand) { return hand.get(0); }
            public Card chooseDraftCard(List<Card> hand, List<Card> drafted) { return hand.get(0); }
            public Optional<Activity> chooseActivity(Map<Activity, Integer> options) {
                return Optional.of(Activity.SWIMMING);
            }
        };
        Player cheating = new Player(1, "cheat", cheater);
        assertThrows(IllegalStateException.class,
                () -> cheating.chooseActivity(Map.of(Activity.BUSHWALKING, 2)));
    }
}
