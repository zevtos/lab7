package ru.itmo.client.main;


import ru.itmo.client.network.TCPClient;
import ru.itmo.general.utility.Interrogator;
import ru.itmo.client.utility.console.StandartConsole;
import ru.itmo.client.utility.runtime.Runner;

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
        console.setErr(System.err);
        console.setOut(System.out);
        var client = new TCPClient("localhost", PORT, console);
        new Runner(console, client).run();
    }
}
