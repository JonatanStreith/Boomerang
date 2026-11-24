package test.scoring;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import boomerang.model.AustraliaCards;
import boomerang.model.Card;
import boomerang.model.Draft;
import boomerang.model.Region;
import boomerang.scoring.ScoreCalculator;
import boomerang.scoring.ScoreCategory;
import boomerang.scoring.ScoreSheet;
import test.testsupport.Fixtures;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/** Requirement 10b: one point per new site, and a 3-point bonus for completing a region. */
public class TouristSitesAndRegionBonusTest {
    private final List<Card> deck = AustraliaCards.create();
    private final ScoreCalculator calculator = ScoreCalculator.forCards(deck);

    /** Seven real sites; the last one is the Catch card. */
    private Draft sites(String... letters) {
        return Fixtures.draftOfSites(deck, letters);
    }

    /** Scores one round for several players at once, as the game does, then awards region bonuses. */
    private void scoreRound(int round, List<ScoreSheet> sheets, List<Draft> drafts) {
        for (int i = 0; i < sheets.size(); i++) {
            calculator.scoreRound(sheets.get(i), drafts.get(i), round);
        }
        calculator.awardRegionBonuses(sheets, round);
    }

    private void scoreAlone(ScoreSheet sheet, int round, Draft draft) {
        scoreRound(round, List.of(sheet), List.of(draft));
    }

    @Test
    @DisplayName("Req 10b: each new site scores one point")
    public void req10b_eachNewSiteScoresOnePoint() {
        ScoreSheet sheet = new ScoreSheet();
        scoreAlone(sheet, 0, sites("A", "B", "C", "H", "I", "N", "S"));
        assertEquals(7, sheet.pointsFor(ScoreCategory.TOURIST_SITES, 0));
    }

    @Test
    @DisplayName("Req 10b: a site visited in an earlier round does not score again")
    public void req10b_visitedSitesDoNotScoreAgain() {
        ScoreSheet sheet = new ScoreSheet();
        scoreAlone(sheet, 0, sites("A", "B", "C", "E", "F", "M", "N"));
        scoreAlone(sheet, 1, sites("A", "B", "C", "H", "I", "R", "S"));
        assertEquals(4, sheet.pointsFor(ScoreCategory.TOURIST_SITES, 1));
        assertEquals(11, sheet.total(ScoreCategory.TOURIST_SITES));
    }

    @Test
    @DisplayName("Req 10b: drafting exactly the same sites again scores nothing")
    public void req10b_sameSitesAgainScoreNothing() {
        ScoreSheet sheet = new ScoreSheet();
        scoreAlone(sheet, 0, sites("A", "B", "E", "F", "M", "N", "R"));
        scoreAlone(sheet, 1, sites("A", "B", "E", "F", "M", "N", "R"));
        assertEquals(0, sheet.pointsFor(ScoreCategory.TOURIST_SITES, 1));
    }

    @Test
    @DisplayName("Req 10b: the first player to complete a region gets 3 bonus points")
    public void req10b_firstToCompleteARegionGetsBonus() {
        ScoreSheet first = new ScoreSheet();
        ScoreSheet other = new ScoreSheet();
        scoreRound(0, List.of(first, other), List.of(
                sites("A", "B", "C", "D", "E", "F", "G"),
                sites("E", "F", "G", "M", "N", "O", "Q")));

        assertEquals(3, first.total(ScoreCategory.REGION_BONUS));
        assertEquals(EnumSet.of(Region.WESTERN_AUSTRALIA), first.bonusRegions());
        assertEquals(0, other.total(ScoreCategory.REGION_BONUS));
    }

    @Test
    @DisplayName("Req 10b-ii: players completing the same region in the same round all get the bonus")
    public void req10b_simultaneousCompletionGivesEveryoneTheBonus() {
        ScoreSheet one = new ScoreSheet();
        ScoreSheet two = new ScoreSheet();
        scoreRound(0, List.of(one, two), List.of(
                sites("A", "B", "C", "D", "E", "F", "G"),
                sites("A", "B", "C", "D", "M", "N", "O")));

        assertEquals(3, one.total(ScoreCategory.REGION_BONUS));
        assertEquals(3, two.total(ScoreCategory.REGION_BONUS));
    }

    @Test
    @DisplayName("Req 10b-i: a region claimed in an earlier round gives later completers no bonus")
    public void req10b_claimedRegionGivesNoBonusLater() {
        ScoreSheet early = new ScoreSheet();
        ScoreSheet late = new ScoreSheet();
        scoreRound(0, List.of(early, late), List.of(
                sites("A", "B", "C", "D", "E", "F", "G"),
                sites("E", "F", "G", "M", "N", "O", "Q")));
        scoreRound(1, List.of(early, late), List.of(
                sites("M", "N", "O", "Q", "R", "S", "T"),
                sites("A", "B", "C", "D", "H", "I", "J")));

        assertFalse(late.bonusRegions().contains(Region.WESTERN_AUSTRALIA));
        assertEquals(EnumSet.of(Region.NORTHERN_TERRITORY), late.bonusRegions());
        assertEquals(3, late.total(ScoreCategory.REGION_BONUS));
    }

    @Test
    @DisplayName("Req 10b: the Catch card counts towards completing a region")
    public void req10b_catchCardCountsTowardsRegion() {
        ScoreSheet sheet = new ScoreSheet();
        Draft draft = sites("B", "C", "D", "E", "F", "G", "A");
        assertEquals("A", draft.catchCard().site());

        scoreAlone(sheet, 0, draft);
        assertEquals(EnumSet.of(Region.WESTERN_AUSTRALIA), sheet.bonusRegions());
        assertEquals(3, sheet.pointsFor(ScoreCategory.REGION_BONUS, 0));
    }

    @Test
    @DisplayName("Req 10b: a region can be completed over several rounds")
    public void req10b_completionCanSpanSeveralRounds() {
        ScoreSheet sheet = new ScoreSheet();
        scoreAlone(sheet, 0, sites("A", "B", "E", "F", "G", "R", "S"));
        assertEquals(0, sheet.pointsFor(ScoreCategory.REGION_BONUS, 0));

        scoreAlone(sheet, 1, sites("C", "D", "I", "J", "T", "U", "V"));
        assertEquals(3, sheet.pointsFor(ScoreCategory.REGION_BONUS, 1));
        assertEquals(EnumSet.of(Region.WESTERN_AUSTRALIA), sheet.bonusRegions());
    }

    @Test
    @DisplayName("Req 10b: the same player gets the bonus for a region only once")
    public void req10b_bonusIsAwardedOncePerRegion() {
        ScoreSheet sheet = new ScoreSheet();
        scoreAlone(sheet, 0, sites("A", "B", "C", "D", "E", "F", "G"));
        scoreAlone(sheet, 1, sites("A", "B", "C", "D", "M", "N", "O"));
        assertEquals(3, sheet.total(ScoreCategory.REGION_BONUS));
    }

    @Test
    @DisplayName("Req 10b: Tasmania is completed by Y, Z and the sites '*' and '-'")
    public void req10b_tasmaniaUsesStarAndDash() {
        ScoreSheet sheet = new ScoreSheet();
        scoreAlone(sheet, 0, sites("Y", "Z", "*", "-", "A", "B", "C"));
        assertEquals(EnumSet.of(Region.TASMANIA), sheet.bonusRegions());
    }

    @Test
    @DisplayName("Req 10b: each of the seven regions can be completed and earns its bonus")
    public void req10b_everyRegionCanBeCompleted() {
        Map<Region, List<Card>> byRegion = new HashMap<>();
        for (Card card : deck) {
            byRegion.computeIfAbsent(card.region(), r -> new ArrayList<>()).add(card);
        }
        Region[] regions = Region.values();
        for (int i = 0; i < regions.length; i++) {
            Region target = regions[i];
            Region other = regions[(i + 1) % regions.length];
            List<Card> seven = new ArrayList<>(byRegion.get(target));
            seven.addAll(byRegion.get(other).subList(0, 3));

            ScoreSheet sheet = new ScoreSheet();
            scoreAlone(sheet, 0, Fixtures.draftFrom(seven));

            assertEquals(EnumSet.of(target), sheet.bonusRegions(), "completing " + target);
            assertEquals(3, sheet.total(ScoreCategory.REGION_BONUS), "bonus for " + target);
        }
        assertTrue(byRegion.size() == 7);
    }
}
