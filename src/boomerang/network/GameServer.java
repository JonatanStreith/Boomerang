package boomerang.network;

import java.io.IOException;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/** Waits for the human players to connect. It does not host a player itself. */
public final class GameServer {
    private GameServer() { }

    /** Blocks until {@code count} clients have connected to {@code port}, in connection order. */
    public static List<RemoteTerminal> acceptClients(int count, int port, PrintStream log) throws IOException {
        List<RemoteTerminal> terminals = new ArrayList<>();
        try (ServerSocket server = new ServerSocket(port)) {
            log.println("Waiting for " + count + " player(s) on port " + server.getLocalPort() + "...");
            for (int i = 0; i < count; i++) {
                Socket socket = server.accept();
                RemoteTerminal terminal = new RemoteTerminal(socket);
                terminals.add(terminal);
                log.println("Player " + (i + 1) + " connected from " + socket.getInetAddress().getHostAddress());
                terminal.show("Connected. You are Player " + (i + 1) + ". Waiting for the other players...");
            }
        } catch (IOException | RuntimeException e) {
            terminals.forEach(t -> t.close("The server could not start the game: " + e.getMessage()));
            throw e;
        }
        return terminals;
    }
}
