package boomerang.model;

/** The seven regions of Australia (rule 10b). Which sites belong to a region is defined by the cards. */
public enum Region {
    WESTERN_AUSTRALIA("Western Australia"),
    NORTHERN_TERRITORY("Northern Territory"),
    QUEENSLAND("Queensland"),
    SOUTH_AUSTRALIA("South Australia"),
    NEW_SOUTH_WALES("New South Wales"),
    VICTORIA("Victoria"),
    TASMANIA("Tasmania");

    private final String displayName;

    Region(String displayName) { this.displayName = displayName; }

    @Override public String toString() { return displayName; }
}
