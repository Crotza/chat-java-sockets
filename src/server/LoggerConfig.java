/*
 * Logging Levels:
 * - SEVERE: Displays critical errors that may cause the application to fail.
 *   Example: Error starting the server, failure connecting to a client.
 *
 * - WARNING: Indicates unexpected situations that do not cause failure but might be problematic.
 *   Example: No participants available to receive messages.
 *
 * - INFO: Displays important information about normal application events.
 *   Example: Client connected, user left the chat.
 *
 * - FINE: Details useful events for debugging during development.
 *   Example: Message received/sent, internal server actions.
 *
 * - ALL: Captures all log levels, useful for full debugging.
 */
package server;

import java.io.File;
import java.io.IOException;
import java.util.logging.*;

public class LoggerConfig {
    // Set log level to FINE for development to capture detailed logs.
    // Change to INFO in production to reduce log verbosity.
    private static final Level LOG_LEVEL = Level.INFO;

    public static void configureLogger(Logger logger) {
        logger.setUseParentHandlers(false); // Disable default console output

        // Create 'logs' directory if it does not exist
        File logDir = new File("logs");
        if (!logDir.exists() && logDir.mkdirs()) {
            System.out.println("Directory 'logs' created.");
        }

        // Console output configuration
        ConsoleHandler consoleHandler = new ConsoleHandler();
        consoleHandler.setLevel(LOG_LEVEL);
        consoleHandler.setFormatter(new SimpleFormatter());
        logger.addHandler(consoleHandler);

        try {
            // Log file output configuration
            FileHandler fileHandler = new FileHandler("logs/server.log", true);
            fileHandler.setFormatter(new SimpleFormatter());

            // Log all levels to the file
            fileHandler.setLevel(Level.ALL);
            logger.addHandler(fileHandler);

        } catch (IOException e) {
            System.err.println("Error configuring logger: " + e.getMessage());
            logger.log(Level.SEVERE, "Error configuring logger", e);
        }
    }
}
