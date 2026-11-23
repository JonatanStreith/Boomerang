package boomerang.scoring;

import boomerang.model.Draft;

/**
 * One automatic scoring rule. A new edition adds new rules by implementing this interface
 * instead of editing existing code (open/closed principle).
 */
public interface ScoringRule {
    ScoreCategory category();

    /** Points this rule gives for the draft. {@code sheet} holds what was visited in earlier rounds. */
    int score(Draft draft, ScoreSheet sheet);
}
