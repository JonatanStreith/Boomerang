package boomerang.game;

import boomerang.player.Player;
import java.util.List;

/** The final outcome. {@code winners} has more than one player only if even the tie-break is level. */
public record GameResult(List<Player> players, List<Player> winners) { }
