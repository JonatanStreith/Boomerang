package boomerang.game;

/** Always passes to the next player, as in the basic rules. */
public final class NextPlayerPassDirection implements PassDirection {
    @Override public int step(int round) { return 1; }
}
