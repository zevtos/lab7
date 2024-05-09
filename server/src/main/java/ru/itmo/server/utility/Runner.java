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

    /**
     * Overrides the run method to provide the main logic of the Runner.
     */
    @Override
    public void run() {

        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        String input;
        while (true) {
            try {
                input = in.readLine();
                if (input.equals("exit")) {
                    // Handles exit command
                    CommandManager.handleServer(new Request(true, input, null));
                    System.exit(0);
                    break;
                }
            } catch (Exception e) {
                logger.error("Error reading from console");
                System.exit(0);
                break;
            }
        }
    }
}
