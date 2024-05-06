package ru.itmo.server.main;

import lombok.SneakyThrows;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.itmo.general.managers.CommandManager;
import ru.itmo.server.dao.TicketDAO;
import ru.itmo.server.dao.UserDAO;
import ru.itmo.server.managers.collections.TicketCollectionManager;
import ru.itmo.server.utility.network.TCPServer;
import ru.itmo.server.utility.Runner;
import sun.misc.Signal;

import static ru.itmo.server.managers.DatabaseManager.createDatabaseIfNotExists;

/**
 * Главный класс приложения.
 *
 * @author zevtos
 */
public class Main {
    private static final int MISSING_FILE_ARGUMENT_EXIT_CODE = 1;
    private static final int PORT = 4093;
    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    /**
     * Точка входа в приложение.
     *
     * @param args аргументы командной строки
     */
    @SneakyThrows
    public static void main(String[] args) {
        setSignalProcessing("INT", "TERM", "TSTP", "BREAK", "EOF");

        createDatabaseIfNotExists();

        Thread runner = new Runner();
        runner.setDaemon(true);
        runner.start();

        var ticketCollectionManager = new TicketCollectionManager();


        UserDAO userDAO = new UserDAO();
        CommandManager.initServerCommands(ticketCollectionManager, new TicketDAO(), userDAO);
        TCPServer tcpServer = new TCPServer(PORT);
        tcpServer.start();
    }

    /**
     * Обработка сигналов, таких как ctrl z, ctrl c...
     *
     * @param signalNames названия сигналов
     */
    private static void setSignalProcessing(String... signalNames) {
        for (String signalName : signalNames) {
            try {
                Signal.handle(new Signal(signalName), signal -> {
                });
            } catch (IllegalArgumentException ignored) {
                // Игнорируем исключение, если сигнал с таким названием уже существует или такого сигнала не существует
            }
        }
    }

}
