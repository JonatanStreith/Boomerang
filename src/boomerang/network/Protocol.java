package boomerang.network;

/**
 * The wire format: one line of UTF-8 text per message, "TYPE text".
 * Newlines and backslashes inside the text are escaped so a message is always one line.
 *
 *   server -> client   SHOW text     display this text
 *   server -> client   ASK prompt    display the prompt, then send back the typed line
 *   server -> client   END text      display this text and disconnect
 *   client -> server   (the typed line, escaped)
 */
public final class Protocol {
    public static final String SHOW = "SHOW";
    public static final String ASK = "ASK";
    public static final String END = "END";
    public static final int DEFAULT_PORT = 2048;

    private Protocol() { }

    public static String encode(String type, String text) { return type + " " + escape(text); }

    public static String escape(String text) {
        StringBuilder out = new StringBuilder();
        for (char c : text.toCharArray()) {
            switch (c) {
                case '\\' -> out.append("\\\\");
                case '\n' -> out.append("\\n");
                case '\r' -> { }
                default -> out.append(c);
            }
        }
        return out.toString();
    }

    public static String unescape(String text) {
        StringBuilder out = new StringBuilder();
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            if (c == '\\' && i + 1 < text.length()) {
                char next = text.charAt(++i);
                out.append(next == 'n' ? '\n' : next);
            } else {
                out.append(c);
            }
        }
        return out.toString();
    }
}
