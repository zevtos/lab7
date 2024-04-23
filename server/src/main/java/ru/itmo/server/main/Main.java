package ru.itmo.server.main;

import ru.itmo.server.managers.collections.TicketCollectionManager;
import ru.itmo.server.network.TCPServer;
import sun.misc.Signal;

/**
 * Главный класс приложения.
 *
 * @author zevtos
 */
public class Main {
    private static final int MISSING_FILE_ARGUMENT_EXIT_CODE = 1;
    private static final int PORT = 4093;

    /**
     * Точка входа в приложение.
     *
     * @param args аргументы командной строки
     */
    public static void main(String[] args) {
        // обработка сигналов
        setSignalProcessing('\n' + "Для получения справки введите 'help', для завершения программы введите 'exit'" + '\n',
                "INT", "TERM", "TSTP", "BREAK", "EOF");

        checkFileArgument(args);

        var ticketCollectionManager = new TicketCollectionManager(args);

        new TCPServer(PORT, ticketCollectionManager).start();
    }

    /**
     * Проверяет наличие аргумента файла в командной строке.
     *
     * @param args аргументы командной строки
     */
    private static void checkFileArgument(String[] args) {
        if (args.length != 1 && args.length != 2) {
            //console.println("Введите имя загружаемого файла как аргумент командной строки");
            System.exit(MISSING_FILE_ARGUMENT_EXIT_CODE);
        }
    }

    private static void setSignalProcessing(String messageString, String... signalNames) {
        for (String signalName : signalNames) {
            try {
                Signal.handle(new Signal(signalName), signal -> System.out.print(messageString));
            } catch (IllegalArgumentException ignored) {
                // Игнорируем исключение, если сигнал с таким названием уже существует или такого сигнала не существует
            }
        }
    }

}
