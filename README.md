# Boomerang Australia (refactored)

Java 17 or newer is needed (developed and checked with Java 21). Get a JDK from https://adoptium.net
if `javac -version` does not work in your terminal.

```
src/boomerang/   the game (model, scoring, player, game, ui, network, Main)
test/boomerang/  JUnit 5 tests (kept out of src, nothing in the game depends on them)
```

## Compile

macOS / Linux:

```
mkdir -p out/main
javac -d out/main $(find src -name "*.java")
```

Windows (Command Prompt):

```
dir /s /b src\*.java > main-sources.txt
mkdir out\main
javac -d out\main @main-sources.txt
```

## Play

```
java -cp out/main boomerang.Main local 1 2        one human and two bots on this console
java -cp out/main boomerang.Main local 0 3        watch three bots play
java -cp out/main boomerang.Main host 2 1         network game: 2 humans and 1 bot (default port 2048)
java -cp out/main boomerang.Main join <host-ip>   each human joins the host
```

On Windows use `out\main` instead of `out/main`. Start `host` first. The host does not play itself, so every
human, including the person who started the host, runs `join`. Use `127.0.0.1` as `<host-ip>` on the same
computer. Players type the letter shown in front of a card to choose it.

## Run the unit tests

Download `junit-platform-console-standalone-<version>.jar` (JUnit 5, for example 1.10.2) from
https://repo1.maven.org/maven2/org/junit/platform/junit-platform-console-standalone/ and put it in this
folder. The commands below use the name `junit.jar`; rename the file or change the name.

macOS / Linux:

```
mkdir -p out/test
javac -cp out/main:junit.jar -d out/test $(find test -name "*.java")
java -jar junit.jar execute -cp out/main:out/test --scan-classpath out/test
```

Windows (Command Prompt):

```
dir /s /b test\*.java > test-sources.txt
mkdir out\test
javac -cp "out\main;junit.jar" -d out\test @test-sources.txt
java -jar junit.jar execute -cp "out\main;out\test" --scan-classpath out\test
```

The output ends with a summary of tests found, successful and failed. A run with no failures is a pass.

## Which tests cover which requirement

| Requirement | Test class |
|---|---|
| 1 (2-4 players) | `game/PlayerCountTest` |
| 2 (28 cards) | `model/AustraliaCardsTest` |
| 3 (shuffle), 4 (deal 7) | `model/DeckTest`, `game/RoundFlowTest` |
| 5 (Throw card, hidden) | `player/PlayerTest`, `game/RoundFlowTest` |
| 6 (pass to next player) | `game/RoundFlowTest` |
| 7 (draft and show), 8 (until one card remains) | `game/RoundFlowTest`, `player/PlayerTest` |
| 9 (Catch card from previous player) | `game/RoundFlowTest`, `player/PlayerTest` |
| 10a Throw & Catch | `scoring/ThrowAndCatchRuleTest` |
| 10b Tourist sites and region bonus | `scoring/TouristSitesAndRegionBonusTest` |
| 10c Collections | `scoring/CollectionsRuleTest` |
| 10d Animals | `scoring/AnimalsRuleTest` |
| 10e Activities | `scoring/ActivityScoringTest`, `game/GameFlowTest` |
| 11 (rounds) | `game/GameFlowTest`, `scoring/ScoreSheetTest` |
| 12 (game end, winner, tie-break) | `game/GameFlowTest`, `game/WinnerSelectorTest`, `scoring/ScoreSheetTest` |

Shared helpers are in `test/boomerang/testsupport`: `Fixtures` builds small test data, `ScriptedController`
is a player that always takes its first card, and `GameHarness` plays a complete repeatable game with seeded
shuffling and records everything that happened.
