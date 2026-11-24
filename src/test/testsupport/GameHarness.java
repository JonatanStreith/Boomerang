package test.testsupport;

import boomerang.game.BoomerangAustralia;
import boomerang.game.GameResult;
import boomerang.game.NextPlayerPassDirection;
import boomerang.model.Activity;
import boomerang.model.AustraliaCards;
import boomerang.model.Card;
import boomerang.player.Player;
import boomerang.scoring.ScoreCalculator;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.function.Consumer;
import java.util.function.Function;

/** Plays one complete, repeatable game with scripted players and keeps everything that was recorded. */
public final class GameHarness {
    public final int playerCount;
    public final long seed;
    public final Recorder recorder = new Recorder();
    public final RecordingListener listener = new RecordingListener();
    public final List<Player> players = new ArrayList<>();
    public final GameResult result;

    private GameHarness(int playerCount, long seed,
                        Function<Map<Activity, Integer>, Optional<Activity>> activityPolicy) {
        this.playerCount = playerCount;
        this.seed = seed;
        for (int i = 0; i < playerCount; i++) {
            players.add(new Player(i, "P" + i, new ScriptedController(i, recorder, activityPolicy)));
        }
        List<Card> cards = AustraliaCards.create();
        Random random = new Random(seed);
        this.result = new BoomerangAustralia(players, cards, ScoreCalculator.forCards(cards),
                new NextPlayerPassDirection(), listener, random).play();
    }

    public static GameHarness play(int playerCount, long seed,
                                   Function<Map<Activity, Integer>, Optional<Activity>> activityPolicy) {
        return new GameHarness(playerCount, seed, activityPolicy);
    }

    /** Runs the check on games of 2, 3 and 4 players with several seeds. */
    public static void forEachGame(Function<Map<Activity, Integer>, Optional<Activity>> activityPolicy,
                                   Consumer<GameHarness> check) {
        for (int players = 2; players <= 4; players++) {
            for (long seed = 1; seed <= 5; seed++) {
                check.accept(play(players, seed, activityPolicy));
            }
        }
    }

    /** Text that identifies the game, for assertion messages. */
    public String context() { return "players=" + playerCount + " seed=" + seed; }
}
