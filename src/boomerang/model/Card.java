package boomerang.model;

import java.util.Objects;
import java.util.Optional;

/**
 * One immutable card. Two cards are equal when they show the same tourist site.
 * The symbols (collection, animal, activity) are optional, as on the real cards.
 */
public final class Card {
    private final String name;
    private final String site;
    private final Region region;
    private final int number;
    private final CollectionType collection;
    private final Animal animal;
    private final Activity activity;

    public Card(String name, String site, Region region, int number,
                CollectionType collection, Animal animal, Activity activity) {
        this.name = Objects.requireNonNull(name, "name");
        this.site = Objects.requireNonNull(site, "site");
        this.region = Objects.requireNonNull(region, "region");
        if (number < 1) {
            throw new IllegalArgumentException("Throw number must be at least 1: " + number);
        }
        this.number = number;
        this.collection = collection;
        this.animal = animal;
        this.activity = activity;
    }

    public String name() { return name; }
    /** The site letter (A-Z, '*' or '-'). */
    public String site() { return site; }
    public Region region() { return region; }
    /** The number used for the Throw and Catch score (rule 2b). */
    public int number() { return number; }
    public Optional<CollectionType> collection() { return Optional.ofNullable(collection); }
    public Optional<Animal> animal() { return Optional.ofNullable(animal); }
    public Optional<Activity> activity() { return Optional.ofNullable(activity); }

    @Override
    public boolean equals(Object o) {
        return o instanceof Card other && site.equals(other.site);
    }

    @Override
    public int hashCode() { return site.hashCode(); }

    @Override
    public String toString() {
        StringBuilder symbols = new StringBuilder();
        if (collection != null) symbols.append(collection);
        if (animal != null) symbols.append(symbols.length() > 0 ? ", " : "").append(animal);
        if (activity != null) symbols.append(symbols.length() > 0 ? ", " : "").append(activity);
        return String.format("%s: %s (%s) #%d [%s]", site, name, region, number, symbols);
    }
}
