package ru.itmo.server.main;

import lombok.SneakyThrows;
import ru.itmo.server.managers.collections.TicketCollectionManager;
import ru.itmo.server.network.TCPServer;
import sun.misc.Signal;

import java.util.Scanner;

/**
 * Главный класс приложения.
 *
 * @author zevtos
 */
public class Main {
    private static final int MISSING_FILE_ARGUMENT_EXIT_CODE = 1;
    private static final int PORT = 4093;
    private static boolean flag = true;

    /**
     * Точка входа в приложение.
     *
     * @param args аргументы командной строки
     */
    @SneakyThrows
    public static void main(String[] args) {
        // обработка сигналов

        checkFileArgument(args);

        var ticketCollectionManager = new TicketCollectionManager(args);

        setSignalProcessing("INT", "TERM", "TSTP", "BREAK", "EOF");

        TCPServer tcpServer = new TCPServer(PORT, ticketCollectionManager);
        tcpServer.start();
        Scanner scanner = new Scanner(System.in);
        String input;
        while(flag){
            input = scanner.nextLine();
            if(input.equalsIgnoreCase("exit")){
                flag = false;
            }
        }
        // Сделать класс для возврата Response который будет запускать новый поток и хранить этот цикл, а так всё збс
        tcpServer.stop_server();
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
    private static void setSignalProcessing(String... signalNames) {
        for (String signalName : signalNames) {
            try {
                Signal.handle(new Signal(signalName), signal -> flag = false);
            } catch (IllegalArgumentException ignored) {
                // Игнорируем исключение, если сигнал с таким названием уже существует или такого сигнала не существует
            }
        }
    }
}
