package test.scoring;

import static boomerang.model.Animal.EMUS;
import static boomerang.model.Animal.KANGAROOS;
import static boomerang.model.Animal.KOALAS;
import static boomerang.model.Animal.PLATYPUSES;
import static boomerang.model.Animal.WOMBATS;
import static org.junit.jupiter.api.Assertions.assertEquals;

import boomerang.model.Animal;
import boomerang.scoring.AnimalsRule;
import boomerang.scoring.ScoreSheet;
import test.testsupport.Fixtures;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/** Requirement 10d: each matching pair of animals scores that animal's points. */
public class AnimalsRuleTest {
    private final AnimalsRule rule = new AnimalsRule();

    private int score(Animal... animals) {
        return rule.score(Fixtures.draftWithAnimals(animals), new ScoreSheet());
    }

    @Test
    @DisplayName("Req 10d: pair values are Kangaroos 3, Emus 4, Wombats 5, Koalas 7, Platypuses 9")
    public void req10d_pairValues() {
        assertEquals(3, KANGAROOS.pairPoints());
        assertEquals(4, EMUS.pairPoints());
        assertEquals(5, WOMBATS.pairPoints());
        assertEquals(7, KOALAS.pairPoints());
        assertEquals(9, PLATYPUSES.pairPoints());
    }

    @Test
    @DisplayName("Req 10d: a pair of each animal scores its value once (not doubled)")
    public void req10d_onePairOfEachAnimal() {
        for (Animal animal : Animal.values()) {
            assertEquals(animal.pairPoints(), score(animal, animal), "pair of " + animal);
        }
    }

    @Test
    @DisplayName("Req 10d: a single animal scores nothing")
    public void req10d_singleAnimalScoresNothing() {
        assertEquals(0, score(EMUS));
    }

    @Test
    @DisplayName("Req 10d: three of a kind scores as one pair")
    public void req10d_threeOfAKindIsOnePair() {
        assertEquals(4, score(EMUS, EMUS, EMUS));
    }

    @Test
    @DisplayName("Req 10d: four of a kind scores as two pairs")
    public void req10d_fourOfAKindIsTwoPairs() {
        assertEquals(8, score(EMUS, EMUS, EMUS, EMUS));
    }

    @Test
    @DisplayName("Req 10d: rulebook example, a pair of koalas and a pair of kangaroos give 10")
    public void req10d_twoDifferentPairs() {
        assertEquals(10, score(KOALAS, KOALAS, KANGAROOS, KANGAROOS));
    }

    @Test
    @DisplayName("Req 10d: different animals that do not match score nothing")
    public void req10d_unmatchedAnimalsScoreNothing() {
        assertEquals(0, score(EMUS, KOALAS, WOMBATS, KANGAROOS, PLATYPUSES));
    }

    @Test
    @DisplayName("Req 10d: a pair plus a leftover single scores only the pair")
    public void req10d_pairPlusSingle() {
        assertEquals(5, score(WOMBATS, WOMBATS, KOALAS));
    }
}
