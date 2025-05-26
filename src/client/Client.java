package client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Client {
    private static final Logger LOGGER = Logger.getLogger(Client.class.getName());
    private String serverIP;
    private int port = 50123;
    private String nickname;
    private volatile boolean connected = true;

    public Client(String serverIP, String nickname) {
        this.serverIP = serverIP;
        this.nickname = nickname;
    }

    // Starts the client connection to the server
    public void start() {
        try (Socket socket = new Socket(serverIP, port);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             Scanner scanner = new Scanner(System.in)) {

            LOGGER.log(Level.INFO, "Connected to server {0}:{1}", new Object[]{serverIP, port});
            out.println(nickname);
            LOGGER.log(Level.FINE, "Nickname sent to server: {0}", nickname);

            // Thread to receive messages from the server
            Thread messageReceiver = new Thread(() -> {
                try {
                    String receivedMessage;
                    while ((receivedMessage = in.readLine()) != null) {
                        receivedMessage = receivedMessage.trim();

                        // Ignore empty messages
                        if (receivedMessage.isEmpty()) continue;

                        // Chat messages are printed directly, removing the "[CHAT]" tag
                        if (receivedMessage.startsWith("[CHAT]")) {
                            System.out.println(receivedMessage.substring(7));
                            continue;
                        }

                        // User list messages are printed directly
                        if (receivedMessage.startsWith("Usuarios conectados:") || receivedMessage.startsWith("- ")) {
                            System.out.println("\n" + receivedMessage);
                            continue;
                        }

                        // Other messages go to logger
                        LOGGER.log(Level.INFO, "{0}", receivedMessage);
                    }
                } catch (IOException e) {
                    LOGGER.log(Level.WARNING, "Connection to server lost. Client will be closed.");
                    connected = false;
                }
            });
            messageReceiver.start();

            // Loop to read and send user input
            String message;
            while (scanner.hasNextLine() && connected) {
                message = scanner.nextLine().trim();
                if (message.isEmpty()) continue;
                handleCommand(message, out);
            }
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error connecting to server", e);
        } finally {
            LOGGER.log(Level.INFO, "Client disconnected. Exiting application.");
            System.exit(0);
        }
    }

    private void handleCommand(String message, PrintWriter out) {
        switch (message.toLowerCase()) {
            case "/users":
                LOGGER.log(Level.INFO, "Requesting list of users...");
                out.println(message);
                break;
            case "/clear":
                LOGGER.log(Level.INFO, "Clearing terminal screen...");
                System.out.print("\033[H\033[2J");
                System.out.flush();
                break;
            case "/help":
                LOGGER.log(Level.INFO, "Displaying list of available commands...");
                System.out.println("\nAvailable commands:");
                System.out.println("/users - Lists all connected users.");
                System.out.println("/clear - Clears the terminal screen.");
                System.out.println("/help - Displays this list of commands.");
                System.out.println("##quit## - Leave the chat.\n");
                break;
            default:
                LOGGER.log(Level.FINE, "Sending message: {0}", message);
                out.println(message);
                if ("##quit##".equalsIgnoreCase(message)) {
                    LOGGER.log(Level.INFO, "{0} left the chat.", nickname);
                    connected = false;
                }
        }
    }

    // Launches the client application
    public static void main(String[] args) {
        if (args.length < 2) {
            LOGGER.log(Level.SEVERE, "Usage: java Client <serverIP> <nickname>");
            return;
        }

        Client client = new Client(args[0], args[1]);
        client.start();
    }
}
