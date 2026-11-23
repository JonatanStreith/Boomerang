package boomerang.game;

/**
 * Decides which way hands are passed. +1 passes to the next player (rule 6) and the final
 * card goes the opposite way (rule 9). Another implementation can alternate the direction
 * per round (rule 13) without changing the game.
 */
public interface PassDirection {
    int step(int round);
}
