package boomerang.scoring;

import boomerang.model.Card;
import boomerang.model.CollectionType;
import boomerang.model.Draft;
import boomerang.model.GameRules;

/** Rule 10c: sum the item values; a total of 1-7 is doubled, a higher total scores as it is. */
public final class CollectionsRule implements ScoringRule {
    @Override public ScoreCategory category() { return ScoreCategory.COLLECTIONS; }

    @Override
    public int score(Draft draft, ScoreSheet sheet) {
        int total = 0;
        for (Card card : draft.allCards()) {
            total += card.collection().map(CollectionType::value).orElse(0);
        }
        return total <= GameRules.COLLECTION_DOUBLING_LIMIT ? total * 2 : total;
    }
}
