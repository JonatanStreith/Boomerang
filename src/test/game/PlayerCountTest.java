package test.game;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import boomerang.game.BoomerangAustralia;
import boomerang.game.GameListener;
import boomerang.game.NextPlayerPassDirection;
import boomerang.model.AustraliaCards;
import boomerang.model.Card;
import boomerang.player.Player;
import boomerang.player.RandomBotController;
import boomerang.scoring.ScoreCalculator;
import test.testsupport.GameHarness;
import test.testsupport.ScriptedController;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/** Requirement 1: a game has between 2 and 4 players. */
public class PlayerCountTest {
    private BoomerangAustralia gameWith(int playerCount) {
        Random random = new Random(1);
        List<Player> players = new ArrayList<>();
        for (int i = 0; i < playerCount; i++) {
            players.add(new Player(i, "P" + i, new RandomBotController(random)));
        }
        List<Card> cards = AustraliaCards.create();
        return new BoomerangAustralia(players, cards, ScoreCalculator.forCards(cards),
                new NextPlayerPassDirection(), new GameListener() { }, random);
    }

    @Test
    @DisplayName("Req 1: zero or one player is rejected")
    public void req1_tooFewPlayersIsRejected() {
        assertThrows(IllegalArgumentException.class, () -> gameWith(0));
        assertThrows(IllegalArgumentException.class, () -> gameWith(1));
    }

    @Test
    @DisplayName("Req 1: five or more players is rejected")
    public void req1_tooManyPlayersIsRejected() {
        assertThrows(IllegalArgumentException.class, () -> gameWith(5));
        assertThrows(IllegalArgumentException.class, () -> gameWith(6));
    }

    @Test
    @DisplayName("Req 1: two, three and four players are accepted")
    public void req1_twoToFourPlayersAreAccepted() {
        for (int count = 2; count <= 4; count++) {
            gameWith(count);
        }
    }

    @Test
    @DisplayName("Req 1: a game with each allowed number of players can be played to the end")
    public void req1_everyAllowedCountPlaysAFullGame() {
        for (int count = 2; count <= 4; count++) {
            GameHarness game = GameHarness.play(count, 3, ScriptedController.SKIP_ACTIVITY);
            assertEquals(4, game.listener.drafts.size(), "rounds scored with " + count + " players");
            assertEquals(count, game.result.players().size());
        }
    }

    @Test
    @DisplayName("Req 1: the error message names the allowed range")
    public void req1_messageExplainsTheLimits() {
        IllegalArgumentException error = assertThrows(IllegalArgumentException.class, () -> gameWith(5));
        assertEquals(true, error.getMessage().contains("2") && error.getMessage().contains("4"),
                "message was: " + error.getMessage());
    }
}
