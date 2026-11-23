package boomerang.player;

import boomerang.model.Activity;
import boomerang.model.Card;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * How a player makes decisions. The game depends only on this interface, so a console
 * human, a bot, or (later) a network player can be plugged in without changing game code.
 */
public interface PlayerController {
    /** Chooses the hidden Throw card from the dealt hand (rule 5). Must return a card in {@code hand}. */
    Card chooseThrowCard(List<Card> hand);

    /** Chooses one card to keep face-up (rules 7-8). Must return a card in {@code hand}. */
    Card chooseDraftCard(List<Card> hand, List<Card> alreadyDrafted);

    /** Optionally chooses one activity to score (rule 10e), given the points each would give. */
    Optional<Activity> chooseActivity(Map<Activity, Integer> options);
}
