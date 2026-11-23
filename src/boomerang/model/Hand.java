package boomerang.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/** The cards a player currently holds and may choose from or pass on. */
public final class Hand {
    private final List<Card> cards = new ArrayList<>();

    public Hand() { }

    public Hand(Collection<Card> initial) { cards.addAll(initial); }

    public boolean remove(Card card) { return cards.remove(card); }
    public boolean contains(Card card) { return cards.contains(card); }
    public int size() { return cards.size(); }
    public boolean isEmpty() { return cards.isEmpty(); }

    /** Read-only view, so callers (such as player controllers) cannot change the hand. */
    public List<Card> cards() { return Collections.unmodifiableList(cards); }
}
