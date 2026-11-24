package test.testsupport;

import boomerang.model.Activity;
import boomerang.model.Animal;
import boomerang.model.Card;
import boomerang.model.CollectionType;
import boomerang.model.Draft;
import boomerang.model.Region;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** Builders for small, readable test data. Cards made here are plain unless a symbol is requested. */
public final class Fixtures {
    public static final String LETTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ*-";

    private Fixtures() { }

    public static Card card(String site, int number, CollectionType collection, Animal animal, Activity activity) {
        return new Card("Site " + site, site, Region.VICTORIA, number, collection, animal, activity);
    }

    /** A draft whose Throw card has {@code throwNumber} and Catch card has {@code catchNumber}. */
    public static Draft draftWithNumbers(int throwNumber, int catchNumber) {
        return build(new CollectionType[0], new Animal[0], new Activity[0], throwNumber, catchNumber);
    }

    /** A draft where the first cards carry the given collection items and the rest carry none. */
    public static Draft draftWithCollections(CollectionType... collections) {
        return build(collections, new Animal[0], new Activity[0], 1, 1);
    }

    public static Draft draftWithAnimals(Animal... animals) {
        return build(new CollectionType[0], animals, new Activity[0], 1, 1);
    }

    public static Draft draftWithActivities(Activity... activities) {
        return build(new CollectionType[0], new Animal[0], activities, 1, 1);
    }

    /** Seven real cards by site letter: the first is the Throw card, the last is the Catch card. */
    public static Draft draftOfSites(List<Card> deck, String... sites) {
        if (sites.length != 7) {
            throw new IllegalArgumentException("A draft needs exactly 7 sites but got " + sites.length);
        }
        Map<String, Card> bySite = bySite(deck);
        List<Card> cards = new ArrayList<>();
        for (String site : sites) {
            cards.add(bySite.get(site));
        }
        return draftFrom(cards);
    }

    public static Draft draftFrom(List<Card> sevenCards) {
        return new Draft(sevenCards.get(0), sevenCards.subList(1, 6), sevenCards.get(6));
    }

    public static Map<String, Card> bySite(List<Card> cards) {
        Map<String, Card> map = new HashMap<>();
        for (Card card : cards) {
            map.put(card.site(), card);
        }
        return map;
    }

    private static Draft build(CollectionType[] collections, Animal[] animals, Activity[] activities,
                               int throwNumber, int catchNumber) {
        if (collections.length > 7 || animals.length > 7 || activities.length > 7) {
            throw new IllegalArgumentException("A draft has only 7 cards");
        }
        List<Card> cards = new ArrayList<>();
        for (int i = 0; i < 7; i++) {
            int number = i == 0 ? throwNumber : (i == 6 ? catchNumber : 1);
            cards.add(card(String.valueOf(LETTERS.charAt(i)), number,
                    i < collections.length ? collections[i] : null,
                    i < animals.length ? animals[i] : null,
                    i < activities.length ? activities[i] : null));
        }
        return draftFrom(cards);
    }
}
