package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Server class that accepts multiple client connections via sockets.
 * Each client is handled in its own thread, and messages are distributed using a thread pool ("dispatcher").
 */
public class ChatServer {
    private static final int PORT = 50123;
    private static final Logger LOGGER = Logger.getLogger(ChatServer.class.getName());

    // Thread-safe list to store connected participants
    private static List<Participant> participants = new CopyOnWriteArrayList<>();

    // Thread pool for distributing messages (Worker Thread "dispatcher")
    private static ExecutorService dispatcher = Executors.newFixedThreadPool(10);

    public static void main(String[] args) {
        LoggerConfig.configureLogger(LOGGER);
        LOGGER.log(Level.INFO, "Server started on port {0}", PORT);

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            LOGGER.log(Level.FINE, "Waiting for client connections...");
            while (true) {
                // Accept a new client connection
                Socket socket = serverSocket.accept();
                LOGGER.log(Level.FINE, "Client connected: {0}", socket.getInetAddress());

                // Create a new participant and add to the list
                Participant participant = new Participant(socket, participants, dispatcher);
                participants.add(participant);

                // Start a new thread to handle this participant
                new Thread(participant).start();

                // Log connection
                LOGGER.log(Level.INFO, "New participant connected: {0}:{1}. Total participants: {2}",
                        new Object[]{socket.getInetAddress(), PORT, participants.size()});
            }
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error starting the server", e);
        }
    }

    // Returns the list of currently connected users
    public static String listUsers() {
        if (participants.isEmpty()) {
            return "No users connected.";
        }
        StringBuilder users = new StringBuilder("Connected users:\n");
        for (Participant participant : participants) {
            users.append("- ").append(participant.getNickname()).append("\n");
        }
        return users.toString();
    }

    // Sends a message to all connected participants
    private static void broadcastMessage(String message) {
        synchronized (participants) {
            for (Participant participant : participants) {
                participant.sendMessage(message);
            }
        }
    }
}
