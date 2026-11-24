package test.game;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import boomerang.model.Card;
import boomerang.model.Draft;
import test.testsupport.GameHarness;
import test.testsupport.ScriptedController;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Requirements 4 to 9: how the cards move through a round. The scripted players always take the
 * first card of their hand, which makes the expected movement of every card predictable.
 * Each check runs on games of 2, 3 and 4 players with several shuffles.
 */
public class RoundFlowTest {
    private static final int ROUNDS = 4;
    private static final int DRAFTS_PER_ROUND = 5;

    private static List<Card> withoutFirst(List<Card> cards) {
        return cards.subList(1, cards.size());
    }

    private static int previous(int player, int count) {
        return Math.floorMod(player - 1, count);
    }

    @Test
    @DisplayName("Req 4: every player is dealt 7 cards and no card is dealt twice in a round")
    public void req4_everyoneGetsSevenDifferentCards() {
        GameHarness.forEachGame(ScriptedController.SKIP_ACTIVITY, game -> {
            for (int round = 0; round < ROUNDS; round++) {
                Set<Card> allDealt = new HashSet<>();
                for (int p = 0; p < game.playerCount; p++) {
                    List<Card> hand = game.recorder.throwHands(p).get(round);
                    assertEquals(7, hand.size(), game.context() + " round " + round);
                    allDealt.addAll(hand);
                }
                assertEquals(7 * game.playerCount, allDealt.size(), game.context() + " round " + round);
            }
        });
    }

    @Test
    @DisplayName("Req 5: the Throw card comes from the dealt hand and is never shown before scoring")
    public void req5_throwCardIsHiddenUntilTheRoundIsScored() {
        GameHarness.forEachGame(ScriptedController.SKIP_ACTIVITY, game -> {
            assertEquals(0, game.listener.throwCardLeaks, game.context());
            for (int round = 0; round < ROUNDS; round++) {
                for (int p = 0; p < game.playerCount; p++) {
                    Card expectedThrow = game.recorder.throwHands(p).get(round).get(0);
                    assertEquals(expectedThrow, game.listener.revealedThrows.get(round).get(p), game.context());
                    Draft draft = game.listener.drafts.get(round).get(p);
                    assertEquals(expectedThrow, draft.throwCard(), game.context());
                    assertFalse(draft.drafted().contains(expectedThrow), game.context());
                }
            }
        });
    }

    @Test
    @DisplayName("Req 6: after the Throw card each hand is passed to the next player, the last to the first")
    public void req6_firstHandComesFromThePreviousPlayer() {
        GameHarness.forEachGame(ScriptedController.SKIP_ACTIVITY, game -> {
            for (int round = 0; round < ROUNDS; round++) {
                for (int p = 0; p < game.playerCount; p++) {
                    int from = previous(p, game.playerCount);
                    List<Card> received = game.recorder.draftHands(p).get(round * DRAFTS_PER_ROUND);
                    List<Card> sentByPrevious = withoutFirst(game.recorder.throwHands(from).get(round));
                    assertEquals(sentByPrevious, received, game.context() + " round " + round + " player " + p);
                }
            }
        });
    }

    @Test
    @DisplayName("Req 6 and 8: every later hand is what the previous player had left, passed on again")
    public void req6_laterHandsKeepMovingToTheNextPlayer() {
        GameHarness.forEachGame(ScriptedController.SKIP_ACTIVITY, game -> {
            for (int round = 0; round < ROUNDS; round++) {
                for (int draft = 1; draft < DRAFTS_PER_ROUND; draft++) {
                    for (int p = 0; p < game.playerCount; p++) {
                        int from = previous(p, game.playerCount);
                        List<Card> received = game.recorder.draftHands(p).get(round * DRAFTS_PER_ROUND + draft);
                        List<Card> sent = withoutFirst(
                                game.recorder.draftHands(from).get(round * DRAFTS_PER_ROUND + draft - 1));
                        assertEquals(sent, received, game.context() + " round " + round + " draft " + draft);
                    }
                }
            }
        });
    }

    @Test
    @DisplayName("Req 7: each pass, every player shows one card from the new hand, visible to all players")
    public void req7_everyPlayersPickIsShownToEveryone() {
        GameHarness.forEachGame(ScriptedController.SKIP_ACTIVITY, game -> {
            assertEquals(ROUNDS * DRAFTS_PER_ROUND, game.listener.draftSteps.size(), game.context());
            for (int round = 0; round < ROUNDS; round++) {
                for (int draft = 0; draft < DRAFTS_PER_ROUND; draft++) {
                    List<List<Card>> rows = game.listener.draftSteps.get(round * DRAFTS_PER_ROUND + draft);
                    assertEquals(game.playerCount, rows.size(), game.context());
                    for (int p = 0; p < game.playerCount; p++) {
                        assertEquals(draft + 1, rows.get(p).size(), game.context() + " row of player " + p);
                        Card justPicked = game.recorder.draftHands(p).get(round * DRAFTS_PER_ROUND + draft).get(0);
                        assertEquals(justPicked, rows.get(p).get(draft), game.context());
                    }
                }
            }
        });
    }

    @Test
    @DisplayName("Req 8: hands shrink 6, 5, 4, 3, 2 and passing stops when one card is left")
    public void req8_draftingContinuesUntilOneCardRemains() {
        int[] expectedSizes = {6, 5, 4, 3, 2};
        GameHarness.forEachGame(ScriptedController.SKIP_ACTIVITY, game -> {
            for (int p = 0; p < game.playerCount; p++) {
                List<List<Card>> hands = game.recorder.draftHands(p);
                assertEquals(ROUNDS * DRAFTS_PER_ROUND, hands.size(), game.context());
                for (int i = 0; i < hands.size(); i++) {
                    assertEquals(expectedSizes[i % DRAFTS_PER_ROUND], hands.get(i).size(),
                            game.context() + " player " + p + " draft " + i);
                }
            }
        });
    }

    @Test
    @DisplayName("Req 9: the last card goes to the previous player and becomes their Catch card")
    public void req9_lastCardBecomesThePreviousPlayersCatchCard() {
        GameHarness.forEachGame(ScriptedController.SKIP_ACTIVITY, game -> {
            for (int round = 0; round < ROUNDS; round++) {
                for (int p = 0; p < game.playerCount; p++) {
                    List<Card> lastTwo = game.recorder.draftHands(p).get(round * DRAFTS_PER_ROUND + 4);
                    Card cardLeftAfterPicking = lastTwo.get(1);
                    int receiver = previous(p, game.playerCount);
                    assertEquals(cardLeftAfterPicking, game.listener.catchCards.get(round).get(receiver),
                            game.context() + " round " + round + ": last card of player " + p);
                    assertEquals(cardLeftAfterPicking, game.listener.drafts.get(round).get(receiver).catchCard(),
                            game.context());
                }
            }
        });
    }

    @Test
    @DisplayName("Req 9: a finished draft has seven different cards, Throw first and Catch last")
    public void req9_finishedDraftHasSevenDifferentCards() {
        GameHarness.forEachGame(ScriptedController.SKIP_ACTIVITY, game -> {
            for (int round = 0; round < ROUNDS; round++) {
                for (int p = 0; p < game.playerCount; p++) {
                    Draft draft = game.listener.drafts.get(round).get(p);
                    List<Card> all = new ArrayList<>(draft.allCards());
                    assertEquals(7, all.size(), game.context());
                    assertEquals(7, new HashSet<>(all).size(), game.context());
                    assertEquals(draft.throwCard(), all.get(0), game.context());
                    assertEquals(draft.catchCard(), all.get(6), game.context());
                }
            }
        });
    }
}
