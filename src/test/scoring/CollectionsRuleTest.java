package test.scoring;

import static boomerang.model.CollectionType.LEAVES;
import static boomerang.model.CollectionType.SHELLS;
import static boomerang.model.CollectionType.SOUVENIRS;
import static boomerang.model.CollectionType.WILDFLOWERS;
import static org.junit.jupiter.api.Assertions.assertEquals;

import boomerang.model.CollectionType;
import boomerang.scoring.CollectionsRule;
import boomerang.scoring.ScoreSheet;
import test.testsupport.Fixtures;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/** Requirement 10c: add the item values; a total of 1-7 is doubled, a higher total is not. */
public class CollectionsRuleTest {
    private final CollectionsRule rule = new CollectionsRule();

    private int score(CollectionType... items) {
        return rule.score(Fixtures.draftWithCollections(items), new ScoreSheet());
    }

    @Test
    @DisplayName("Req 10c: item values are Leaves 1, Wildflowers 2, Shells 3, Souvenirs 5")
    public void req10c_itemValues() {
        assertEquals(1, LEAVES.value());
        assertEquals(2, WILDFLOWERS.value());
        assertEquals(3, SHELLS.value());
        assertEquals(5, SOUVENIRS.value());
    }

    @Test
    @DisplayName("Req 10c: rulebook example, 3 Leaves + 1 Shells = 6, doubled to 12")
    public void req10c_leavesAndShellsExample() {
        assertEquals(12, score(LEAVES, LEAVES, LEAVES, SHELLS));
    }

    @Test
    @DisplayName("Req 10c: rulebook example, 2 Wildflowers + 1 Souvenirs = 9, not doubled")
    public void req10c_wildflowersAndSouvenirExample() {
        assertEquals(9, score(WILDFLOWERS, WILDFLOWERS, SOUVENIRS));
    }

    @Test
    @DisplayName("Req 10c: a total of exactly 7 is still doubled")
    public void req10c_totalOfSevenIsDoubled() {
        assertEquals(14, score(SOUVENIRS, WILDFLOWERS));
    }

    @Test
    @DisplayName("Req 10c: a total of 8 is not doubled")
    public void req10c_totalOfEightIsNotDoubled() {
        assertEquals(8, score(SOUVENIRS, SHELLS));
    }

    @Test
    @DisplayName("Req 10c: the smallest total of 1 is doubled to 2")
    public void req10c_totalOfOneIsDoubled() {
        assertEquals(2, score(LEAVES));
    }

    @Test
    @DisplayName("Req 10c: no collection items score 0")
    public void req10c_noItemsScoreZero() {
        assertEquals(0, score());
    }

    @Test
    @DisplayName("Req 10c: the value is summed over all item types, not doubled per type")
    public void req10c_mixedItemsAreSummedBeforeDoubling() {
        assertEquals(2 * (1 + 2 + 3), score(LEAVES, WILDFLOWERS, SHELLS));
    }
}
