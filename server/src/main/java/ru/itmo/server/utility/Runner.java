package ru.itmo.server.utility;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.itmo.general.managers.CommandManager;
import ru.itmo.general.network.Request;
import ru.itmo.server.utility.network.TCPServer;

import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
 * A class responsible for running and managing the server application.
 *
 * @author zevtos
 */
public class Runner extends Thread {
    private static final Logger logger = LoggerFactory.getLogger(Runner.class);
    private final TCPServer server;
    private final ThreadGroup requestReaders;
    private final ThreadGroup requestHandlers;

    /**
     * Constructs a Runner without a server, requestReaders, and requestHandlers.
     */
    public Runner() {
        server = null;
        requestReaders = new ThreadGroup("RequestReaders");
        requestHandlers = new ThreadGroup("RequestHandlers");
    }

    /**
     * Constructs a Runner with the specified server, requestReaders, and requestHandlers.
     *
     * @param server          The TCPServer instance.
     * @param requestReaders  The ThreadGroup for request readers.
     * @param requestHandlers The ThreadGroup for request handlers.
     */
    public Runner(TCPServer server, ThreadGroup requestReaders, ThreadGroup requestHandlers) {
        this.server = server;
        this.requestReaders = requestReaders;
        this.requestHandlers = requestHandlers;
    }

    /**
     * Overrides the run method to provide the main logic of the Runner.
     */
    @Override
    public void run() {
        // Adds a shutdown hook to save data before application termination
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            if (CommandManager.getCommands() != null) {
                logger.info("Saving data before application termination...");
                try {
                    CommandManager.handleServer(new Request(true, "save", null));
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }));

        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        String input;
        while (true) {
            try {
                // Reads input from the console
                input = in.readLine();
                if (input.equals("exit")) {
                    // Handles exit command
                    CommandManager.handleServer(new Request(true, input, null));
                    System.exit(0);
                    break;
                } else if (input.equals("save")) {
                    // Handles save command
                    CommandManager.handleServer(new Request(true, input, null));
                    logger.info("Tickets saved to file");
                }
            } catch (Exception e) {
                logger.error("Error reading from console");
                System.exit(0);
                break;
            }
        }
    }
}
