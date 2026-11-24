package test.game;

import static org.junit.jupiter.api.Assertions.assertEquals;

import boomerang.game.WinnerSelector;
import boomerang.player.Player;
import boomerang.player.RandomBotController;
import boomerang.scoring.ScoreCategory;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/** Requirement 12: the highest total wins; a tie goes to the most Throw & Catch points. */
public class WinnerSelectorTest {
    private final WinnerSelector selector = new WinnerSelector();

    private static Player player(int id) {
        return new Player(id, "P" + id, new RandomBotController(new Random(id)));
    }

    private static List<String> names(List<Player> players) {
        List<String> names = new ArrayList<>();
        for (Player p : players) names.add(p.name());
        return names;
    }

    @Test
    @DisplayName("Req 12: the player with the highest total wins")
    public void req12_highestTotalWins() {
        Player a = player(0);
        Player b = player(1);
        Player c = player(2);
        a.scoreSheet().record(ScoreCategory.ANIMALS, 0, 20);
        b.scoreSheet().record(ScoreCategory.ANIMALS, 0, 35);
        c.scoreSheet().record(ScoreCategory.ANIMALS, 0, 30);
        assertEquals(List.of("P1"), names(selector.select(List.of(a, b, c))));
    }

    @Test
    @DisplayName("Req 12a: on equal totals the player with more Throw & Catch points wins")
    public void req12a_tieIsBrokenByThrowAndCatch() {
        Player a = player(0);
        Player b = player(1);
        a.scoreSheet().record(ScoreCategory.THROW_AND_CATCH, 0, 3);
        a.scoreSheet().record(ScoreCategory.ANIMALS, 0, 7);
        b.scoreSheet().record(ScoreCategory.THROW_AND_CATCH, 0, 6);
        b.scoreSheet().record(ScoreCategory.ANIMALS, 0, 4);
        assertEquals(10, a.scoreSheet().total());
        assertEquals(10, b.scoreSheet().total());
        assertEquals(List.of("P1"), names(selector.select(List.of(a, b))));
    }

    @Test
    @DisplayName("Req 12a: the tie-break only applies between players tied on the total")
    public void req12a_tieBreakDoesNotOverrideAHigherTotal() {
        Player a = player(0);
        Player b = player(1);
        a.scoreSheet().record(ScoreCategory.THROW_AND_CATCH, 0, 2);
        a.scoreSheet().record(ScoreCategory.ANIMALS, 0, 20);
        b.scoreSheet().record(ScoreCategory.THROW_AND_CATCH, 0, 12);
        b.scoreSheet().record(ScoreCategory.ANIMALS, 0, 5);
        assertEquals(List.of("P0"), names(selector.select(List.of(a, b))));
    }

    @Test
    @DisplayName("Req 12a: players level on both total and Throw & Catch share the win")
    public void req12a_completeTieHasSeveralWinners() {
        Player a = player(0);
        Player b = player(1);
        Player c = player(2);
        for (Player p : List.of(a, b)) {
            p.scoreSheet().record(ScoreCategory.THROW_AND_CATCH, 0, 5);
            p.scoreSheet().record(ScoreCategory.ANIMALS, 0, 5);
        }
        c.scoreSheet().record(ScoreCategory.ANIMALS, 0, 1);
        assertEquals(List.of("P0", "P1"), names(selector.select(List.of(a, b, c))));
    }

    @Test
    @DisplayName("Req 12: the winner is found from the total including tourist sites and region bonuses")
    public void req12_totalIncludesSitesAndBonuses() {
        Player a = player(0);
        Player b = player(1);
        a.scoreSheet().record(ScoreCategory.ANIMALS, 0, 10);
        b.scoreSheet().record(ScoreCategory.TOURIST_SITES, 0, 8);
        b.scoreSheet().record(ScoreCategory.REGION_BONUS, 0, 3);
        assertEquals(List.of("P1"), names(selector.select(List.of(a, b))));
    }
}
