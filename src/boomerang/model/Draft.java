package boomerang.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * The seven cards a player ends a round with: the hidden Throw card, the cards drafted
 * face-up, and the final Catch card. Throw and Catch are explicit, so no code needs
 * "card number 0 and card number 6" conventions.
 */
public final class Draft {
    private final Card throwCard;
    private final List<Card> drafted;
    private final Card catchCard;

    public Draft(Card throwCard, List<Card> drafted, Card catchCard) {
        this.throwCard = Objects.requireNonNull(throwCard, "throwCard");
        this.catchCard = Objects.requireNonNull(catchCard, "catchCard");
        this.drafted = List.copyOf(drafted);
        if (allCards().size() != GameRules.HAND_SIZE) {
            throw new IllegalArgumentException("A draft must contain exactly " + GameRules.HAND_SIZE
                    + " cards but has " + allCards().size());
        }
    }

    public Card throwCard() { return throwCard; }
    public Card catchCard() { return catchCard; }
    /** The cards chosen face-up between Throw and Catch. */
    public List<Card> drafted() { return drafted; }

    /** All seven cards: Throw, drafted cards, Catch. */
    public List<Card> allCards() {
        List<Card> all = new ArrayList<>();
        all.add(throwCard);
        all.addAll(drafted);
        all.add(catchCard);
        return all;
    }
}
