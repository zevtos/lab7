package ru.itmo.client.main;


import ru.itmo.client.network.TCPClient;
import ru.itmo.client.utility.Interrogator;
import ru.itmo.client.utility.console.StandartConsole;
import ru.itmo.client.utility.runtime.Runner;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Scanner;

/**
 * Главный класс приложения.
 *
 * @author zevtos
 */
public class Main {
    private static final int PORT = 4093;

    /**
     * Точка входа в приложение.
     *
     * @param args аргументы командной строки
     */
    public static void main(String[] args) {
        Interrogator.setUserScanner(new Scanner(System.in));
        var console = new StandartConsole();
        try {
            var client = new TCPClient(InetAddress.getLocalHost().getHostAddress(), PORT);
            client.connect();
            new Runner(console, client).run();
        } catch (UnknownHostException e) {
            console.printError("");
        } catch (IOException e) {
            console.printError("Соединение прервано");
        }
    }
}
