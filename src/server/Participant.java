package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Represents a participant in the chat.
 * Each instance runs in its own thread, handling communication with a single client.
 */
public class Participant implements Runnable {
    private static final Logger LOGGER = Logger.getLogger(Participant.class.getName());
    private Socket socket;
    private List<Participant> participants;
    private ExecutorService dispatcher;
    private PrintWriter out;
    private BufferedReader in;
    private String nickname;

    public Participant(Socket socket, List<Participant> participants, ExecutorService dispatcher) {
        this.socket = socket;
        this.participants = participants;
        this.dispatcher = dispatcher;
    }

    @Override
    public void run() {
        try {
            // Initializes input and output streams for client communication
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);

            // Reads the nickname of the participant
            nickname = in.readLine();

            // If the nickname is null or empty, do not proceed
            if (nickname == null || nickname.trim().isEmpty()) {
                LOGGER.log(Level.WARNING, "Failed to receive participant nickname.");
                return;
            }

            // Notifies all clients that a new participant has joined
            String joinMessage = "[CHAT] " + nickname + " joined the chat.";
            broadcastToAll(joinMessage);

            LOGGER.log(Level.INFO, "{0} joined the chat.", nickname);
            LOGGER.log(Level.FINE, "Participant {0} connected from {1}", new Object[]{nickname, socket.getInetAddress()});

            String message;
            while ((message = in.readLine()) != null) {
                message = message.trim();
                if (message.isEmpty()) continue;

                LOGGER.log(Level.FINE, "Message received from {0}: {1}", new Object[]{nickname, message});

                // Handles command to list all connected participants
                if (message.equalsIgnoreCase("/users")) {
                    out.println(ChatServer.listUsers());
                    continue;
                }

                // Handles the exit command
                if (message.equalsIgnoreCase("##quit##")) {
                    LOGGER.log(Level.INFO, "{0} requested to leave the chat.", nickname);
                    break;
                }

                // Dispatches the message to be sent to all participants
                dispatcher.execute(new MessageService(nickname, message, participants));
            }
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Communication error with {0}", nickname);
        } finally {
            closeConnection();
        }
    }

    public void sendMessage(String message) {
        out.println(message);
    }

    private void broadcastToAll(String message) {
        synchronized (participants) {
            for (Participant participant : participants) {
                participant.sendMessage(message);
            }
        }
    }

    public String getNickname() {
        return nickname;
    }

    private void closeConnection() {
        try {
            socket.close();
            LOGGER.log(Level.FINE, "Connection closed for {0}", nickname);
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error closing connection for {0}", nickname);
        }

        participants.remove(this);
        LOGGER.log(Level.INFO, "{0} left the chat. Total participants: {1}", new Object[]{nickname, participants.size()});
        broadcastToAll("[CHAT] " + nickname + " left the chat.");
    }
}
