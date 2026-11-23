package boomerang.ui;

import java.io.PrintStream;
import java.util.Scanner;

/** A Terminal on the local console. The streams are passed in; this class never touches System itself. */
public final class ConsoleTerminal implements Terminal {
    private final Scanner in;
    private final PrintStream out;

    public ConsoleTerminal(Scanner in, PrintStream out) {
        this.in = in;
        this.out = out;
    }

    /** A terminal that can only show text, e.g. a server console that just watches the game. */
    public static ConsoleTerminal outputOnly(PrintStream out) { return new ConsoleTerminal(null, out); }

    @Override
    public void show(String text) { out.println(text); }

    @Override
    public String ask(String prompt) {
        if (in == null) {
            throw new UnsupportedOperationException("This terminal cannot read input");
        }
        out.print(prompt);
        out.flush();
        if (!in.hasNextLine()) {
            throw new IllegalStateException("Input was closed before the game finished");
        }
        return in.nextLine();
    }
}
