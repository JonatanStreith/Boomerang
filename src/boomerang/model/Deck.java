package boomerang.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/** A shuffled pile of cards. The Random is injected so shuffling can be made deterministic in tests. */
public final class Deck {
    private final List<Card> cards;
    private final Random random;

    public Deck(List<Card> cards, Random random) {
        this.cards = new ArrayList<>(cards);
        this.random = random;
    }

    /** Rule 3. */
    public void shuffle() { Collections.shuffle(cards, random); }

    /** Removes and returns the top {@code count} cards (rule 4). */
    public List<Card> deal(int count) {
        if (count > cards.size()) {
            throw new IllegalStateException("Cannot deal " + count + " cards from a deck of " + cards.size());
        }
        List<Card> top = cards.subList(0, count);
        List<Card> dealt = new ArrayList<>(top);
        top.clear();
        return dealt;
    }

    public int size() { return cards.size(); }
}
