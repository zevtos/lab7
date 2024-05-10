package ru.itmo.general.utility.console;

import lombok.SneakyThrows;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Обеспечивает ввод команд и вывод результатов в стандартной консоли.
 *
 * @author zevtos
 */
public class StandartConsole implements Console {
    private static final String PROMPT = "$ ";
    private static List<OutputStream> streams = new ArrayList<>();
    private static OutputStream lastStream = null;

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

    public void println() {
        System.out.println();
    }

    @Override
    public char[] readPassword(String prompt, int minLength) {
        java.io.Console console = System.console();
        char[] password = new char[0];
        int i = 0;
        while (password.length < minLength) {
            try {
                if (console != null) {
                    password = console.readPassword(prompt);
                } else {
                    Scanner scanner = new Scanner(System.in);
                    System.out.print(prompt);
                    password = scanner.nextLine().toCharArray();
                }
                return password;
            } catch (Exception e) {
                printError("Ошибка чтения пароля: " + e.getMessage());
            }
            i++;
            if (i >= password.length) {
                return new char[0];
            }
        }
        return password;
    }


    /**
     * Выводит ошибку в консоль.
     *
     * @param obj Ошибка для печати.
     */
    @SneakyThrows
    public void printError(Object obj) {
        System.err.println("Error:" + obj);
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

    public void setErr(OutputStream stream) {
        System.setErr(new PrintStream(new FixedStream(stream)));
    }

    public void setOut(OutputStream stream) {
        System.setOut(new PrintStream(new FixedStream(stream)));
    }

    /**
     * Возвращает приглашение для ввода команды.
     *
     * @return Приглашение для ввода команды.
     */
    public String getPrompt() {
        return PROMPT;
    }

    private static class FixedStream extends OutputStream {
        private final OutputStream target;

        public FixedStream(OutputStream originalStream) {
            target = originalStream;
            streams.add(this);
        }

        @Override
        public void write(int b) throws IOException {
            if (lastStream != this) swap();
            target.write(b);
        }

        @Override
        public void write(byte[] b) throws IOException {
            if (lastStream != this) swap();
            target.write(b);
        }

        @Override
        public void write(byte[] b, int off, int len) throws IOException {
            if (lastStream != this) swap();
            target.write(b, off, len);
        }

        private void swap() throws IOException {
            if (lastStream != null) {
                lastStream.flush();
                try {
                    Thread.sleep(100);
                } catch (InterruptedException ignored) {
                }
            }
            lastStream = this;
        }

        @Override
        public void close() throws IOException {
            target.close();
        }

        @Override
        public void flush() throws IOException {
            target.flush();
        }
    }
}

