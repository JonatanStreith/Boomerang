package boomerang.scoring;

import boomerang.model.Animal;
import boomerang.model.Card;
import boomerang.model.Draft;
import java.util.EnumMap;
import java.util.Map;

/** Rule 10d: every matching pair of an animal scores that animal's points (3 of a kind = 1 pair, 4 = 2 pairs). */
public final class AnimalsRule implements ScoringRule {
    @Override public ScoreCategory category() { return ScoreCategory.ANIMALS; }

    @Override
    public int score(Draft draft, ScoreSheet sheet) {
        Map<Animal, Integer> counts = new EnumMap<>(Animal.class);
        for (Card card : draft.allCards()) {
            card.animal().ifPresent(animal -> counts.merge(animal, 1, Integer::sum));
        }
        int total = 0;
        for (Map.Entry<Animal, Integer> entry : counts.entrySet()) {
            total += (entry.getValue() / 2) * entry.getKey().pairPoints();
        }
        return total;
    }
}
