package test.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import boomerang.model.AustraliaCards;
import boomerang.model.Card;
import boomerang.model.Region;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/** Requirement 2: the deck of 28 cards. The expected table is copied from the exam text, independent of the code. */
public class AustraliaCardsTest {
    /** site | name | region | number | collection | animal | activity  (empty = none). */
    private static final String[] TABLE = {
        "A|The Bungle Bungles|Western Australia|1|Leaves||Indigenous Culture",
        "B|The Pinnacles|Western Australia|1||Kangaroos|Sightseeing",
        "C|Margaret River|Western Australia|1|Shells|Kangaroos|",
        "D|Kalbarri National Park|Western Australia|1|Wildflowers||Bushwalking",
        "E|Uluru|Northern Territory|4||Emus|Indigenous Culture",
        "F|Kakadu National Park|Northern Territory|4||Wombats|Sightseeing",
        "G|Nitmiluk National Park|Northern Territory|4|Shells|Platypuses|",
        "H|King's Canyon|Northern Territory|4||Koalas|Swimming",
        "I|The Great Barrier Reef|Queensland|6|Wildflowers||Sightseeing",
        "J|The Whitsundays|Queensland|6||Kangaroos|Indigenous Culture",
        "K|Daintree Rainforest|Queensland|6|Souvenirs||Bushwalking",
        "L|Surfers Paradise|Queensland|6|Wildflowers||Swimming",
        "M|Barossa Valley|South Australia|3||Koalas|Bushwalking",
        "N|Lake Eyre|South Australia|3||Emus|Swimming",
        "O|Kangaroo Island|South Australia|3||Kangaroos|Bushwalking",
        "P|Mount Gambier|South Australia|3|Wildflowers||Sightseeing",
        "Q|Blue Mountains|New South Wales|5||Wombats|Indigenous Culture",
        "R|Sydney Harbour|New South Wales|5||Emus|Sightseeing",
        "S|Bondi Beach|New South Wales|5||Wombats|Swimming",
        "T|Hunter Valley|New South Wales|5||Emus|Bushwalking",
        "U|Melbourne|Victoria|2||Wombats|Bushwalking",
        "V|The MCG|Victoria|2|Leaves||Indigenous Culture",
        "W|Twelve Apostles|Victoria|2|Shells||Swimming",
        "X|Royal Exhibition Building|Victoria|2|Leaves|Platypuses|",
        "Y|Salamanca Markets|Tasmania|7|Leaves|Emus|",
        "Z|Mount Wellington|Tasmania|7||Koalas|Sightseeing",
        "*|Port Arthur|Tasmania|7|Leaves||Indigenous Culture",
        "-|Richmond|Tasmania|7||Kangaroos|Swimming",
    };

    private final List<Card> deck = AustraliaCards.create();

    @Test
    @DisplayName("Req 2: the deck has 28 cards with 28 different sites")
    public void req2_deckHasTwentyEightDistinctCards() {
        assertEquals(28, deck.size());
        assertEquals(28, new HashSet<>(deck).size());
    }

    @Test
    @DisplayName("Req 2: every card matches the table in the exam text")
    public void req2_everyCardMatchesTheRuleTable() {
        Map<String, Card> bySite = new HashMap<>();
        for (Card card : deck) bySite.put(card.site(), card);

        for (String row : TABLE) {
            String[] f = row.split("\\|", -1);
            Card card = bySite.get(f[0]);
            assertTrue(card != null, "Missing card for site " + f[0]);
            String where = "site " + f[0];
            assertEquals(f[1], card.name(), where + " name");
            assertEquals(f[2], card.region().toString(), where + " region");
            assertEquals(Integer.parseInt(f[3]), card.number(), where + " number");
            assertEquals(f[4], card.collection().map(Object::toString).orElse(""), where + " collection");
            assertEquals(f[5], card.animal().map(Object::toString).orElse(""), where + " animal");
            assertEquals(f[6], card.activity().map(Object::toString).orElse(""), where + " activity");
        }
        assertEquals(TABLE.length, deck.size());
    }

    @Test
    @DisplayName("Req 2: the sites are A-Z plus '*' and '-'")
    public void req2_sitesAreAToZPlusStarAndDash() {
        Set<String> expected = new HashSet<>();
        for (char c = 'A'; c <= 'Z'; c++) expected.add(String.valueOf(c));
        expected.add("*");
        expected.add("-");
        Set<String> actual = new HashSet<>();
        for (Card card : deck) actual.add(card.site());
        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("Req 2: each of the 7 regions has 4 sites and one throw number")
    public void req2_eachRegionHasFourSitesAndOneThrowNumber() {
        Map<Region, Integer> expectedNumber = new HashMap<>();
        expectedNumber.put(Region.WESTERN_AUSTRALIA, 1);
        expectedNumber.put(Region.NORTHERN_TERRITORY, 4);
        expectedNumber.put(Region.QUEENSLAND, 6);
        expectedNumber.put(Region.SOUTH_AUSTRALIA, 3);
        expectedNumber.put(Region.NEW_SOUTH_WALES, 5);
        expectedNumber.put(Region.VICTORIA, 2);
        expectedNumber.put(Region.TASMANIA, 7);

        Map<Region, Integer> sitesPerRegion = new HashMap<>();
        for (Card card : deck) {
            sitesPerRegion.merge(card.region(), 1, Integer::sum);
            assertEquals((int) expectedNumber.get(card.region()), card.number(), "number on site " + card.site());
        }
        assertEquals(7, sitesPerRegion.size());
        for (Region region : Region.values()) {
            assertEquals(4, (int) sitesPerRegion.get(region), region + " site count");
        }
    }

    @Test
    @DisplayName("Req 2: two cards are equal exactly when they show the same site")
    public void req2_cardEqualityFollowsTheSite() {
        Card first = deck.get(0);
        Card sameSite = new Card("Another name", first.site(), Region.TASMANIA, 7, null, null, null);
        assertEquals(first, sameSite);
        assertEquals(first.hashCode(), sameSite.hashCode());
        assertTrue(!first.equals(deck.get(1)));
        assertTrue(!first.equals("A"));
        assertTrue(!first.equals(null));
    }
}
