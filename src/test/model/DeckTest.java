package test.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;

import boomerang.model.AustraliaCards;
import boomerang.model.Card;
import boomerang.model.Deck;
import boomerang.model.GameRules;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/** Requirements 3 (shuffle) and 4 (deal seven cards to each player). */
public class DeckTest {
    private final List<Card> cards = AustraliaCards.create();

    @Test
    @DisplayName("Req 3: shuffling keeps the same 28 cards but changes their order")
    public void req3_shuffleReordersButKeepsAllCards() {
        Deck deck = new Deck(cards, new Random(42));
        deck.shuffle();
        List<Card> shuffled = deck.deal(28);

        assertEquals(new HashSet<>(cards), new HashSet<>(shuffled));
        assertEquals(28, shuffled.size());
        assertNotEquals(cards, shuffled);
    }

    @Test
    @DisplayName("Req 3: different random seeds give different orders")
    public void req3_differentSeedsGiveDifferentOrders() {
        Deck first = new Deck(cards, new Random(1));
        Deck second = new Deck(cards, new Random(2));
        first.shuffle();
        second.shuffle();
        assertNotEquals(first.deal(28), second.deal(28));
    }

    @Test
    @DisplayName("Req 3: the same seed gives the same order (so tests are repeatable)")
    public void req3_sameSeedGivesSameOrder() {
        Deck first = new Deck(cards, new Random(7));
        Deck second = new Deck(cards, new Random(7));
        first.shuffle();
        second.shuffle();
        assertEquals(first.deal(28), second.deal(28));
    }

    @Test
    @DisplayName("Req 3: an unshuffled deck is left in its original order")
    public void req3_deckIsNotShuffledUntilAsked() {
        assertEquals(cards, new Deck(cards, new Random(5)).deal(28));
    }

    @Test
    @DisplayName("Req 4: dealing 7 cards returns 7 cards and removes them from the deck")
    public void req4_dealSevenRemovesSevenCards() {
        Deck deck = new Deck(cards, new Random(3));
        deck.shuffle();
        List<Card> hand = deck.deal(GameRules.HAND_SIZE);

        assertEquals(7, hand.size());
        assertEquals(7, new HashSet<>(hand).size());
        assertEquals(21, deck.size());
    }

    @Test
    @DisplayName("Req 4: four players can each be dealt 7 different cards from the 28")
    public void req4_fourPlayersGetDisjointHands() {
        Deck deck = new Deck(cards, new Random(9));
        deck.shuffle();
        List<Card> everyone = new ArrayList<>();
        for (int player = 0; player < 4; player++) {
            everyone.addAll(deck.deal(7));
        }
        assertEquals(28, new HashSet<>(everyone).size());
        assertEquals(0, deck.size());
    }

    @Test
    @DisplayName("Req 4: dealing more cards than the deck holds is an error")
    public void req4_dealingTooManyCardsFails() {
        Deck deck = new Deck(cards, new Random(1));
        deck.deal(21);
        assertThrows(IllegalStateException.class, () -> deck.deal(8));
        assertTrue(deck.size() == 7, "a failed deal must not remove cards");
    }
}
