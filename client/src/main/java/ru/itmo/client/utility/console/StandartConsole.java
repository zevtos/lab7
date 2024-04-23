package ru.itmo.client.utility.console;

import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

/**
 * Обеспечивает ввод команд и вывод результатов в стандартной консоли.
 *
 * @author zevtos
 */
public class StandartConsole implements Console {
    private static final String PROMPT = "$ ";

    /**
     * Выводит объект в консоль.
     *
     * @param obj Объект для печати.
     */
    public void print(Object obj) {
        System.out.print(obj);
    }

    /**
     * Выводит объект в консоль с переводом строки.
     *
     * @param obj Объект для печати.
     */
    public void println(Object obj) {
        System.out.println(obj);
    }

    /**
     * Выводит ошибку в консоль.
     *
     * @param obj Ошибка для печати.
     */
    public void printError(Object obj) {
        System.err.print("Error: " + obj + '\n');
        try {
            TimeUnit.MILLISECONDS.sleep(20); // Пауза
        } catch (InterruptedException ignored) {
        }
    }

    /**
     * Выводит два элемента в формате таблицы.
     *
     * @param elementLeft  Левый элемент колонки.
     * @param elementRight Правый элемент колонки.
     */
    public void printTable(Object elementLeft, Object elementRight) {
        System.out.printf(" %-35s%-1s%n", elementLeft, elementRight);
    }

    /**
     * Выводит приглашение для ввода команды.
     */
    public void prompt() {
        print(PROMPT);
    }

    /**
     * Возвращает приглашение для ввода команды.
     *
     * @return Приглашение для ввода команды.
     */
    public String getPrompt() {
        return PROMPT;
    }

}
