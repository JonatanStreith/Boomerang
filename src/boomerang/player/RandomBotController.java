package boomerang.player;

import boomerang.model.Activity;
import boomerang.model.Card;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;

/** A simple computer player: random cards, and the best-scoring activity if it scores anything. */
public final class RandomBotController implements PlayerController {
    private final Random random;

    public RandomBotController(Random random) { this.random = random; }

    @Override
    public Card chooseThrowCard(List<Card> hand) { return hand.get(random.nextInt(hand.size())); }

    @Override
    public Card chooseDraftCard(List<Card> hand, List<Card> alreadyDrafted) {
        return hand.get(random.nextInt(hand.size()));
    }

    @Override
    public Optional<Activity> chooseActivity(Map<Activity, Integer> options) {
        return options.entrySet().stream()
                .filter(entry -> entry.getValue() > 0)
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey);
    }
}
