package boomerang.network;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

/** The player's side of a network game: shows what the server sends and forwards typed answers. */
public final class GameClient {
    private GameClient() { }

    public static void run(String host, int port, Scanner keyboard, PrintStream screen) throws IOException {
        try (Socket socket = new Socket(host, port)) {
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
            BufferedWriter out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8));
            String line;
            while ((line = in.readLine()) != null) {
                int space = line.indexOf(' ');
                String type = space < 0 ? line : line.substring(0, space);
                String text = space < 0 ? "" : Protocol.unescape(line.substring(space + 1));
                switch (type) {
                    case Protocol.SHOW -> screen.println(text);
                    case Protocol.ASK -> {
                        screen.print(text);
                        screen.flush();
                        if (!keyboard.hasNextLine()) {
                            return;   // keyboard closed: leave, the server reports the disconnect
                        }
                        out.write(Protocol.escape(keyboard.nextLine()));
                        out.newLine();
                        out.flush();
                    }
                    case Protocol.END -> {
                        screen.println(text);
                        return;
                    }
                    default -> { /* ignore unknown messages from a newer server */ }
                }
            }
            screen.println("The server closed the connection.");
        }
    }
}
