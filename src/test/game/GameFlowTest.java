package test.game;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import boomerang.model.Animal;
import boomerang.model.Card;
import boomerang.model.Draft;
import boomerang.player.Player;
import boomerang.scoring.ScoreCategory;
import test.testsupport.GameHarness;
import test.testsupport.ScriptedController;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/** Requirements 10e (once per game), 11 (rounds) and 12 (game end), checked on complete games. */
public class GameFlowTest {
    private static final int ROUNDS = 4;

    @Test
    @DisplayName("Req 11: the game plays and scores exactly four rounds")
    public void req11_fourRoundsAreStartedAndScored() {
        GameHarness.forEachGame(ScriptedController.SKIP_ACTIVITY, game -> {
            assertEquals(ROUNDS, game.listener.roundsStarted, game.context());
            assertEquals(ROUNDS, game.listener.drafts.size(), game.context());
            assertEquals(ROUNDS, game.listener.revealedThrows.size(), game.context());
        });
    }

    @Test
    @DisplayName("Req 11: each new round deals freshly shuffled cards")
    public void req11_newRoundsDealNewHands() {
        GameHarness.forEachGame(ScriptedController.SKIP_ACTIVITY, game -> {
            Set<List<Card>> handsOfPlayerZero = new HashSet<>(game.recorder.throwHands(0));
            assertTrue(handsOfPlayerZero.size() > 1,
                    game.context() + ": player 0 was dealt the same hand in every round");
        });
    }

    @Test
    @DisplayName("Req 11: every round, all players again hold 7 cards at the start")
    public void req11_everyRoundStartsWithFullHands() {
        GameHarness.forEachGame(ScriptedController.SKIP_ACTIVITY, game -> {
            for (int p = 0; p < game.playerCount; p++) {
                assertEquals(ROUNDS, game.recorder.throwHands(p).size(), game.context());
                for (List<Card> hand : game.recorder.throwHands(p)) {
                    assertEquals(7, hand.size(), game.context());
                }
            }
        });
    }

    @Test
    @DisplayName("Req 12: the game ends once, after the fourth round, with a result")
    public void req12_gameEndsAfterTheFourthRound() {
        GameHarness.forEachGame(ScriptedController.SKIP_ACTIVITY, game -> {
            assertEquals(1, game.listener.gameOvers, game.context());
            assertEquals(ROUNDS, game.listener.drafts.size(), game.context());
            assertEquals(game.players, game.result.players(), game.context());
            assertTrue(!game.result.winners().isEmpty(), game.context());
        });
    }

    @Test
    @DisplayName("Req 12: each player's total is the sum of all categories")
    public void req12_totalIsTheSumOfAllCategories() {
        GameHarness.forEachGame(ScriptedController.FIRST_AVAILABLE_ACTIVITY, game -> {
            for (Player player : game.players) {
                int sum = 0;
                for (ScoreCategory category : ScoreCategory.values()) {
                    sum += player.scoreSheet().total(category);
                }
                assertEquals(sum, player.scoreSheet().total(), game.context() + " " + player.name());
                assertTrue(player.scoreSheet().total(ScoreCategory.TOURIST_SITES) > 0,
                        game.context() + ": tourist sites must be part of the total");
            }
        });
    }

    /**
     * Recalculates throw & catch, sites, collections and animals straight from the finished drafts,
     * with simple code written here, and compares with what the game's scoring produced.
     */
    @Test
    @DisplayName("Req 10 and 12: scores in a real game match an independent recalculation")
    public void req10_gameScoresMatchIndependentRecalculation() {
        GameHarness.forEachGame(ScriptedController.SKIP_ACTIVITY, game -> {
            for (int p = 0; p < game.playerCount; p++) {
                int throwCatch = 0;
                int sites = 0;
                int collections = 0;
                int animals = 0;
                Set<String> visited = new HashSet<>();
                for (int round = 0; round < ROUNDS; round++) {
                    Draft draft = game.listener.drafts.get(round).get(p);
                    throwCatch += Math.abs(draft.throwCard().number() - draft.catchCard().number());

                    int collectionTotal = 0;
                    Map<Animal, Integer> animalCounts = new EnumMap<>(Animal.class);
                    for (Card card : draft.allCards()) {
                        if (visited.add(card.site())) sites++;
                        if (card.collection().isPresent()) collectionTotal += card.collection().get().value();
                        card.animal().ifPresent(a -> animalCounts.merge(a, 1, Integer::sum));
                    }
                    collections += collectionTotal <= 7 ? collectionTotal * 2 : collectionTotal;
                    for (Map.Entry<Animal, Integer> e : animalCounts.entrySet()) {
                        animals += (e.getValue() / 2) * e.getKey().pairPoints();
                    }
                }
                Player player = game.players.get(p);
                String who = game.context() + " player " + p;
                assertEquals(throwCatch, player.scoreSheet().total(ScoreCategory.THROW_AND_CATCH), who + " throw & catch");
                assertEquals(sites, player.scoreSheet().total(ScoreCategory.TOURIST_SITES), who + " sites");
                assertEquals(collections, player.scoreSheet().total(ScoreCategory.COLLECTIONS), who + " collections");
                assertEquals(animals, player.scoreSheet().total(ScoreCategory.ANIMALS), who + " animals");
                assertEquals(0, player.scoreSheet().total(ScoreCategory.ACTIVITIES), who + " activities (skipped)");
                assertEquals(0, player.scoreSheet().total(ScoreCategory.REGION_BONUS) % 3, who + " bonus is a multiple of 3");
            }
        });
    }

    @Test
    @DisplayName("Req 12: the winners have the highest total, and ties are broken by Throw & Catch")
    public void req12_winnersFollowTheRules() {
        GameHarness.forEachGame(ScriptedController.FIRST_AVAILABLE_ACTIVITY, game -> {
            int best = 0;
            for (Player p : game.players) best = Math.max(best, p.scoreSheet().total());
            List<Player> tied = new ArrayList<>();
            for (Player p : game.players) {
                if (p.scoreSheet().total() == best) tied.add(p);
            }
            int bestThrowCatch = 0;
            for (Player p : tied) bestThrowCatch = Math.max(bestThrowCatch, p.scoreSheet().total(ScoreCategory.THROW_AND_CATCH));

            for (Player winner : game.result.winners()) {
                assertEquals(best, winner.scoreSheet().total(), game.context());
                assertEquals(bestThrowCatch, winner.scoreSheet().total(ScoreCategory.THROW_AND_CATCH), game.context());
            }
            int expectedWinners = 0;
            for (Player p : tied) {
                if (p.scoreSheet().total(ScoreCategory.THROW_AND_CATCH) == bestThrowCatch) expectedWinners++;
            }
            assertEquals(expectedWinners, game.result.winners().size(), game.context());
        });
    }

    @Test
    @DisplayName("Req 10e: a player is asked about activities once per round and scores at most one")
    public void req10e_atMostOneActivityPerRound() {
        GameHarness.forEachGame(ScriptedController.FIRST_AVAILABLE_ACTIVITY, game -> {
            for (int p = 0; p < game.playerCount; p++) {
                assertEquals(ROUNDS, game.recorder.activityCalls(p), game.context() + " player " + p);
            }
            for (int round = 0; round < ROUNDS; round++) {
                for (int p = 0; p < game.playerCount; p++) {
                    assertEquals(round + 1, (int) game.listener.scoredActivityCounts.get(round).get(p),
                            game.context() + " round " + round + " player " + p);
                }
            }
        });
    }

    @Test
    @DisplayName("Req 10e: over four rounds each of the four activities is scored exactly once")
    public void req10e_eachActivityOnceInTheWholeGame() {
        GameHarness.forEachGame(ScriptedController.FIRST_AVAILABLE_ACTIVITY, game -> {
            for (Player player : game.players) {
                int scored = 0;
                for (boomerang.model.Activity a : boomerang.model.Activity.values()) {
                    if (player.scoreSheet().hasScored(a)) scored++;
                }
                assertEquals(4, scored, game.context() + " " + player.name());
            }
        });
    }

    @Test
    @DisplayName("Req 10e: scoring activities is optional, skipping them every round scores none")
    public void req10e_skippingActivitiesScoresNothing() {
        GameHarness.forEachGame(ScriptedController.SKIP_ACTIVITY, game -> {
            for (Player player : game.players) {
                assertEquals(0, player.scoreSheet().total(ScoreCategory.ACTIVITIES), game.context());
                for (boomerang.model.Activity a : boomerang.model.Activity.values()) {
                    assertEquals(false, player.scoreSheet().hasScored(a), game.context());
                }
            }
            assertNotEquals(0, game.listener.drafts.size());
        });
    }
}
