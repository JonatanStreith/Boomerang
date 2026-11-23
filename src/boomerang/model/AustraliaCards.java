package boomerang.model;

import static boomerang.model.Activity.*;
import static boomerang.model.Animal.*;
import static boomerang.model.CollectionType.*;
import static boomerang.model.Region.*;

import java.util.ArrayList;
import java.util.List;

/** The 28 cards of Boomerang Australia (rule 2). */
public final class AustraliaCards {
    private AustraliaCards() { }

    public static List<Card> create() {
        List<Card> c = new ArrayList<>();
        c.add(new Card("The Bungle Bungles", "A", WESTERN_AUSTRALIA, 1, LEAVES, null, INDIGENOUS_CULTURE));
        c.add(new Card("The Pinnacles", "B", WESTERN_AUSTRALIA, 1, null, KANGAROOS, SIGHTSEEING));
        c.add(new Card("Margaret River", "C", WESTERN_AUSTRALIA, 1, SHELLS, KANGAROOS, null));
        c.add(new Card("Kalbarri National Park", "D", WESTERN_AUSTRALIA, 1, WILDFLOWERS, null, BUSHWALKING));
        c.add(new Card("Uluru", "E", NORTHERN_TERRITORY, 4, null, EMUS, INDIGENOUS_CULTURE));
        c.add(new Card("Kakadu National Park", "F", NORTHERN_TERRITORY, 4, null, WOMBATS, SIGHTSEEING));
        c.add(new Card("Nitmiluk National Park", "G", NORTHERN_TERRITORY, 4, SHELLS, PLATYPUSES, null));
        c.add(new Card("King's Canyon", "H", NORTHERN_TERRITORY, 4, null, KOALAS, SWIMMING));
        c.add(new Card("The Great Barrier Reef", "I", QUEENSLAND, 6, WILDFLOWERS, null, SIGHTSEEING));
        c.add(new Card("The Whitsundays", "J", QUEENSLAND, 6, null, KANGAROOS, INDIGENOUS_CULTURE));
        c.add(new Card("Daintree Rainforest", "K", QUEENSLAND, 6, SOUVENIRS, null, BUSHWALKING));
        c.add(new Card("Surfers Paradise", "L", QUEENSLAND, 6, WILDFLOWERS, null, SWIMMING));
        c.add(new Card("Barossa Valley", "M", SOUTH_AUSTRALIA, 3, null, KOALAS, BUSHWALKING));
        c.add(new Card("Lake Eyre", "N", SOUTH_AUSTRALIA, 3, null, EMUS, SWIMMING));
        c.add(new Card("Kangaroo Island", "O", SOUTH_AUSTRALIA, 3, null, KANGAROOS, BUSHWALKING));
        c.add(new Card("Mount Gambier", "P", SOUTH_AUSTRALIA, 3, WILDFLOWERS, null, SIGHTSEEING));
        c.add(new Card("Blue Mountains", "Q", NEW_SOUTH_WALES, 5, null, WOMBATS, INDIGENOUS_CULTURE));
        c.add(new Card("Sydney Harbour", "R", NEW_SOUTH_WALES, 5, null, EMUS, SIGHTSEEING));
        c.add(new Card("Bondi Beach", "S", NEW_SOUTH_WALES, 5, null, WOMBATS, SWIMMING));
        c.add(new Card("Hunter Valley", "T", NEW_SOUTH_WALES, 5, null, EMUS, BUSHWALKING));
        c.add(new Card("Melbourne", "U", VICTORIA, 2, null, WOMBATS, BUSHWALKING));
        c.add(new Card("The MCG", "V", VICTORIA, 2, LEAVES, null, INDIGENOUS_CULTURE));
        c.add(new Card("Twelve Apostles", "W", VICTORIA, 2, SHELLS, null, SWIMMING));
        c.add(new Card("Royal Exhibition Building", "X", VICTORIA, 2, LEAVES, PLATYPUSES, null));
        c.add(new Card("Salamanca Markets", "Y", TASMANIA, 7, LEAVES, EMUS, null));
        c.add(new Card("Mount Wellington", "Z", TASMANIA, 7, null, KOALAS, SIGHTSEEING));
        c.add(new Card("Port Arthur", "*", TASMANIA, 7, LEAVES, null, INDIGENOUS_CULTURE));
        c.add(new Card("Richmond", "-", TASMANIA, 7, null, KANGAROOS, SWIMMING));
        return c;
    }
}
