package test.scoring;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import boomerang.scoring.ScoreCategory;
import boomerang.scoring.ScoreSheet;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/** Requirement 12: the grand total adds up every category of every round. */
public class ScoreSheetTest {
    @Test
    @DisplayName("Req 12: a new sheet has no points")
    public void req12_newSheetIsEmpty() {
        ScoreSheet sheet = new ScoreSheet();
        assertEquals(0, sheet.total());
        for (ScoreCategory category : ScoreCategory.values()) {
            assertEquals(0, sheet.total(category), category.toString());
        }
    }

    @Test
    @DisplayName("Req 12: a category total adds its four rounds")
    public void req12_categoryTotalAddsAllRounds() {
        ScoreSheet sheet = new ScoreSheet();
        sheet.record(ScoreCategory.ANIMALS, 0, 4);
        sheet.record(ScoreCategory.ANIMALS, 1, 10);
        sheet.record(ScoreCategory.ANIMALS, 3, 3);
        assertEquals(17, sheet.total(ScoreCategory.ANIMALS));
        assertEquals(10, sheet.pointsFor(ScoreCategory.ANIMALS, 1));
        assertEquals(0, sheet.pointsFor(ScoreCategory.ANIMALS, 2));
    }

    @Test
    @DisplayName("Req 12: the grand total includes every category, tourist sites and region bonuses too")
    public void req12_grandTotalIncludesEveryCategory() {
        ScoreSheet sheet = new ScoreSheet();
        int expected = 0;
        int points = 1;
        for (ScoreCategory category : ScoreCategory.values()) {
            sheet.record(category, 0, points);
            sheet.record(category, 3, points * 10);
            expected += points * 11;
            points++;
        }
        assertEquals(expected, sheet.total());
        assertEquals(6 * 11, sheet.total(ScoreCategory.REGION_BONUS));
        assertEquals(2 * 11, sheet.total(ScoreCategory.TOURIST_SITES));
    }

    @Test
    @DisplayName("Req 12: points added to the same category and round accumulate")
    public void req12_pointsInTheSameRoundAccumulate() {
        ScoreSheet sheet = new ScoreSheet();
        sheet.record(ScoreCategory.REGION_BONUS, 2, 3);
        sheet.record(ScoreCategory.REGION_BONUS, 2, 3);
        assertEquals(6, sheet.pointsFor(ScoreCategory.REGION_BONUS, 2));
    }

    @Test
    @DisplayName("Req 11: a sheet has exactly four rounds")
    public void req11_onlyFourRoundsExist() {
        ScoreSheet sheet = new ScoreSheet();
        assertThrows(IllegalArgumentException.class, () -> sheet.record(ScoreCategory.ANIMALS, 4, 1));
        assertThrows(IllegalArgumentException.class, () -> sheet.pointsFor(ScoreCategory.ANIMALS, -1));
    }
}
