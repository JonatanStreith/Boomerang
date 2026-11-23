package boomerang.game;

import boomerang.player.Player;
import boomerang.scoring.ScoreCategory;
import java.util.ArrayList;
import java.util.List;

/** Rule 12: the highest total wins; a tie goes to the most Throw & Catch points (rule 12a). */
public final class WinnerSelector {
    public List<Player> select(List<Player> players) {
        List<Player> leaders = withMax(players, p -> p.scoreSheet().total());
        return withMax(leaders, p -> p.scoreSheet().total(ScoreCategory.THROW_AND_CATCH));
    }

    private static List<Player> withMax(List<Player> players, java.util.function.ToIntFunction<Player> value) {
        int best = Integer.MIN_VALUE;
        for (Player p : players) best = Math.max(best, value.applyAsInt(p));
        List<Player> result = new ArrayList<>();
        for (Player p : players) {
            if (value.applyAsInt(p) == best) result.add(p);
        }
        return result;
    }
}
