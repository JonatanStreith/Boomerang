package test.scoring;

import static org.junit.jupiter.api.Assertions.assertEquals;

import boomerang.scoring.ScoreCategory;
import boomerang.scoring.ScoreSheet;
import boomerang.scoring.ThrowAndCatchRule;
import test.testsupport.Fixtures;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/** Requirement 10a: Throw and Catch score = absolute difference of the two numbers. */
public class ThrowAndCatchRuleTest {
    private final ThrowAndCatchRule rule = new ThrowAndCatchRule();

    private int score(int throwNumber, int catchNumber) {
        return rule.score(Fixtures.draftWithNumbers(throwNumber, catchNumber), new ScoreSheet());
    }

    @Test
    @DisplayName("Req 10a: rulebook example, Throw 4 and Catch 1 give 3")
    public void req10a_throwFourCatchOneGivesThree() {
        assertEquals(3, score(4, 1));
    }

    @Test
    @DisplayName("Req 10a: rulebook example, Throw 1 and Catch 4 also give 3")
    public void req10a_throwOneCatchFourGivesThree() {
        assertEquals(3, score(1, 4));
    }

    @Test
    @DisplayName("Req 10a: equal numbers give 0")
    public void req10a_equalNumbersGiveZero() {
        assertEquals(0, score(5, 5));
    }

    @Test
    @DisplayName("Req 10a: the largest possible difference is 6")
    public void req10a_extremesGiveSix() {
        assertEquals(6, score(7, 1));
        assertEquals(6, score(1, 7));
    }

    @Test
    @DisplayName("Req 10a: only the Throw and Catch cards count, not the cards in between")
    public void req10a_middleCardsDoNotMatter() {
        assertEquals(2, score(3, 5));
        assertEquals(ThrowAndCatchRule.class, rule.getClass());
        assertEquals(ScoreCategory.THROW_AND_CATCH, rule.category());
    }
}
