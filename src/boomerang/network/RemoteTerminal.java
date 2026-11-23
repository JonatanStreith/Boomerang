package boomerang.network;

import boomerang.ui.Terminal;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

/**
 * Server side of one connected human: a Terminal whose screen and keyboard are on another
 * computer. Because it is just a Terminal, the game, the views and the controllers need no
 * network code. A lost connection is reported as an IllegalStateException.
 */
public final class RemoteTerminal implements Terminal {
    private final Socket socket;
    private final BufferedReader in;
    private final BufferedWriter out;

    public RemoteTerminal(Socket socket) throws IOException {
        this.socket = socket;
        this.in = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
        this.out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8));
    }

    @Override
    public void show(String text) { send(Protocol.SHOW, text); }

    @Override
    public String ask(String prompt) {
        send(Protocol.ASK, prompt);
        try {
            String line = in.readLine();
            if (line == null) {
                throw new IllegalStateException("A remote player disconnected");
            }
            return Protocol.unescape(line);
        } catch (IOException e) {
            throw new IllegalStateException("Lost the connection to a remote player: " + e.getMessage(), e);
        }
    }

    /** Tells the client why the session ends, then closes the connection. Never throws. */
    public void close(String message) {
        try {
            send(Protocol.END, message);
        } catch (IllegalStateException ignored) {
            // the client is already gone
        }
        try {
            socket.close();
        } catch (IOException ignored) {
            // nothing more to do
        }
    }

    private void send(String type, String text) {
        try {
            out.write(Protocol.encode(type, text));
            out.newLine();
            out.flush();
        } catch (IOException e) {
            throw new IllegalStateException("Lost the connection to a remote player: " + e.getMessage(), e);
        }
    }
}
