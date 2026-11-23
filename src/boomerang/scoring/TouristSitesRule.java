package boomerang.scoring;

import boomerang.model.Card;
import boomerang.model.Draft;

/** Rule 10b: one point for each site not visited in an earlier round. */
public final class TouristSitesRule implements ScoringRule {
    @Override public ScoreCategory category() { return ScoreCategory.TOURIST_SITES; }

    @Override
    public int score(Draft draft, ScoreSheet sheet) {
        int newSites = 0;
        for (Card card : draft.allCards()) {
            if (!sheet.visitedSites().contains(card.site())) {
                newSites++;
            }
        }
        return newSites;
    }
}
