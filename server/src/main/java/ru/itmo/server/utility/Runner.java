package ru.itmo.server.utility;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.itmo.general.managers.CommandManager;
import ru.itmo.general.network.Request;
import ru.itmo.server.network.TCPServer;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class Runner extends Thread {
    private static final Logger logger = LoggerFactory.getLogger(Runner.class);
    private final TCPServer server;
    private final ThreadGroup requestReaders;
    private final ThreadGroup requestHandlers;

    public Runner() {
        server = null;
        requestReaders = new ThreadGroup("RequestReaders");
        requestHandlers = new ThreadGroup("RequestHandlers");
    }

    public Runner(TCPServer server, ThreadGroup RequestReaders, ThreadGroup RequestHandlers) {
        this.server = server;
        this.requestReaders = RequestReaders;
        this.requestHandlers = RequestHandlers;
    }

    @Override
    public void run() {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            if (CommandManager.getCommands() != null) {
                logger.info("Сохранение перед завершением работы приложения...");
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
                input = in.readLine();
                if (input.equals("exit")) {
                    CommandManager.handleServer(new Request(true, input, null));
                    System.exit(0);
                    break;
                } else if (input.equals("save")) {
                    CommandManager.handleServer(new Request(true, input, null));
                    logger.info("Билеты сохранены в файл");
                }
            } catch (Exception e) {
                logger.error("Ошибка чтения с консоли");
                System.exit(0);
                break;
            }
        }
    }
}
