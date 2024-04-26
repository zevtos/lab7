package ru.itmo.server.main;

import lombok.SneakyThrows;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.itmo.general.network.Request;
import ru.itmo.server.managers.CommandManager;
import ru.itmo.server.managers.collections.TicketCollectionManager;
import ru.itmo.server.network.TCPServer;
import sun.misc.Signal;

/**
 * Главный класс приложения.
 *
 * @author zevtos
 */
public class Main {
    public static final Logger LOGGER = LoggerFactory.getLogger(Main.class);
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
        // обработка сигналов
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            LOGGER.info("Сохранение перед завершением работы приложения...");
            try {
                CommandManager.handleServer(new Request(true, "save", null));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }));
        checkFileArgument(args);

        var ticketCollectionManager = new TicketCollectionManager(args);

        setSignalProcessing("INT", "TERM", "TSTP", "BREAK", "EOF");

        TCPServer tcpServer = new TCPServer(PORT, ticketCollectionManager);
        tcpServer.start();
    }

    /**
     * Проверяет наличие аргумента файла в командной строке.
     *
     * @param args аргументы командной строки
     */
    private static void checkFileArgument(String[] args) {
        if (args.length != 1 && args.length != 2) {
            logger.info("Введите имя загружаемого файла как аргумент командной строки");
            System.exit(MISSING_FILE_ARGUMENT_EXIT_CODE);
        }
    }
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
