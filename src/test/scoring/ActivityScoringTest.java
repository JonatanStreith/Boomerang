package test.scoring;

import static boomerang.model.Activity.BUSHWALKING;
import static boomerang.model.Activity.INDIGENOUS_CULTURE;
import static boomerang.model.Activity.SIGHTSEEING;
import static boomerang.model.Activity.SWIMMING;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import boomerang.model.Activity;
import boomerang.model.AustraliaCards;
import boomerang.model.Draft;
import boomerang.model.GameRules;
import boomerang.scoring.ScoreCalculator;
import boomerang.scoring.ScoreCategory;
import boomerang.scoring.ScoreSheet;
import test.testsupport.Fixtures;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/** Requirement 10e: activities are optional, one per round, each only once per game. */
public class ActivityScoringTest {
    private final ScoreCalculator calculator = ScoreCalculator.forCards(AustraliaCards.create());

    private static Draft bushwalking(int count) {
        Activity[] activities = new Activity[count];
        for (int i = 0; i < count; i++) activities[i] = BUSHWALKING;
        return Fixtures.draftWithActivities(activities);
    }

    @Test
    @DisplayName("Req 10e: 1 to 6 matching cards score 0, 2, 4, 7, 10 and 15")
    public void req10e_pointTable() {
        int[] expected = {0, 2, 4, 7, 10, 15};
        for (int count = 1; count <= 6; count++) {
            assertEquals(expected[count - 1], GameRules.activityPoints(count), count + " matching cards");
        }
        assertEquals(0, GameRules.activityPoints(0));
    }

    @Test
    @DisplayName("Req 10e: rulebook example, three Bushwalking cards score 4")
    public void req10e_threeBushwalkingScoreFour() {
        ScoreSheet sheet = new ScoreSheet();
        int points = calculator.scoreActivity(sheet, bushwalking(3), 0, BUSHWALKING);
        assertEquals(4, points);
        assertEquals(4, sheet.pointsFor(ScoreCategory.ACTIVITIES, 0));
    }

    @Test
    @DisplayName("Req 10e: the table is applied through the calculator for every count")
    public void req10e_calculatorUsesTable() {
        int[] expected = {0, 2, 4, 7, 10, 15};
        for (int count = 1; count <= 6; count++) {
            int points = calculator.scoreActivity(new ScoreSheet(), bushwalking(count), 0, BUSHWALKING);
            assertEquals(expected[count - 1], points, count + " matching cards");
        }
    }

    @Test
    @DisplayName("Req 10e: only the chosen activity is scored, other activities are ignored")
    public void req10e_onlyTheChosenActivityScores() {
        Draft draft = Fixtures.draftWithActivities(SWIMMING, SWIMMING, BUSHWALKING, BUSHWALKING, BUSHWALKING);
        ScoreSheet sheet = new ScoreSheet();
        assertEquals(2, calculator.scoreActivity(sheet, draft, 0, SWIMMING));
        assertTrue(sheet.hasScored(SWIMMING));
        assertFalse(sheet.hasScored(BUSHWALKING));
    }

    @Test
    @DisplayName("Req 10e: every activity is offered with the points it would score")
    public void req10e_optionsShowPointsForEachActivity() {
        Draft draft = Fixtures.draftWithActivities(SWIMMING, SWIMMING, BUSHWALKING, SIGHTSEEING, SIGHTSEEING, SIGHTSEEING);
        Map<Activity, Integer> options = calculator.activityOptions(new ScoreSheet(), draft);

        assertEquals(4, options.size());
        assertEquals(2, (int) options.get(SWIMMING));
        assertEquals(0, (int) options.get(BUSHWALKING));
        assertEquals(4, (int) options.get(SIGHTSEEING));
        assertEquals(0, (int) options.get(INDIGENOUS_CULTURE));
    }

    @Test
    @DisplayName("Req 10e: scoring is optional, so choosing nothing changes nothing")
    public void req10e_notChoosingAnActivityScoresNothing() {
        ScoreSheet sheet = new ScoreSheet();
        calculator.activityOptions(sheet, bushwalking(3));
        assertEquals(0, sheet.total(ScoreCategory.ACTIVITIES));
        assertEquals(4, calculator.activityOptions(sheet, bushwalking(3)).size());
    }

    @Test
    @DisplayName("Req 10e: an activity that was scored is no longer offered")
    public void req10e_scoredActivityIsRemovedFromOptions() {
        ScoreSheet sheet = new ScoreSheet();
        calculator.scoreActivity(sheet, bushwalking(3), 0, BUSHWALKING);
        Map<Activity, Integer> options = calculator.activityOptions(sheet, bushwalking(3));

        assertEquals(3, options.size());
        assertFalse(options.containsKey(BUSHWALKING));
    }

    @Test
    @DisplayName("Req 10e: each activity can only be scored once per game")
    public void req10e_activityCannotBeScoredTwice() {
        ScoreSheet sheet = new ScoreSheet();
        calculator.scoreActivity(sheet, bushwalking(3), 0, BUSHWALKING);
        assertThrows(IllegalStateException.class,
                () -> calculator.scoreActivity(sheet, bushwalking(4), 1, BUSHWALKING));
        assertEquals(4, sheet.total(ScoreCategory.ACTIVITIES));
    }
}
