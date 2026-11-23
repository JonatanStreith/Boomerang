package boomerang.ui;

/**
 * A two-way text channel to one human: show text, or ask a question and wait for the answer.
 * The local console and a remote network player both implement it, so the views and
 * controllers do not know (or care) where the person is sitting.
 */
public interface Terminal {
    /** Shows text (may contain several lines). */
    void show(String text);

    /** Shows a prompt and returns the line the person types. */
    String ask(String prompt);
}
