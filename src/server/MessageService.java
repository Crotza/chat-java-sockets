package server;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * MessageService is a task executed by the "gossip" worker thread.
 * It is responsible for sending a message from a participant to all connected clients.
 */
public class MessageService implements Runnable {
    private static final Logger LOGGER = Logger.getLogger(MessageService.class.getName());
    private String nickname;
    private String text;
    private List<Participant> participants;
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy HH:mm");

    public MessageService(String nickname, String text, List<Participant> participants) {
        this.nickname = nickname;
        this.text = text;
        this.participants = participants;
    }

    @Override
    public void run() {
        if (participants.isEmpty()) {
            LOGGER.log(Level.WARNING, "No participants available to receive the message.");
            return;
        }

        String timestamp = DATE_FORMAT.format(new Date());
        String formattedMessage = String.format("[CHAT] %s (%s) - %s", timestamp, nickname, text);
        LOGGER.log(Level.FINE, "Formatted message to send: {0}", formattedMessage);

        // Safely sends the message to all connected participants
        synchronized (participants) {
            for (Participant participant : participants) {
                participant.sendMessage(formattedMessage);
            }
        }
    }
}
