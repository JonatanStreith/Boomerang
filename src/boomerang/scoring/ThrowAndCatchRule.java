package boomerang.scoring;

import boomerang.model.Draft;

/** Rule 10a: the absolute difference between the Throw and the Catch number. */
public final class ThrowAndCatchRule implements ScoringRule {
    @Override public ScoreCategory category() { return ScoreCategory.THROW_AND_CATCH; }

    @Override
    public int score(Draft draft, ScoreSheet sheet) {
        return Math.abs(draft.throwCard().number() - draft.catchCard().number());
    }
}
