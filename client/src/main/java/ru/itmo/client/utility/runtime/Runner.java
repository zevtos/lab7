package ru.itmo.client.utility.runtime;


import ru.itmo.client.network.TCPClient;
import ru.itmo.general.exceptions.ScriptRecursionException;
import ru.itmo.general.managers.CommandManager;
import ru.itmo.general.models.forms.TicketForm;
import ru.itmo.general.network.Request;
import ru.itmo.general.utility.Interrogator;
import ru.itmo.general.utility.console.Console;
import sun.misc.Signal;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashSet;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.Set;
import java.util.concurrent.TimeoutException;

/**
 * Запускает выполнение программы.
 *
 * @author zevtos
 */
public class Runner {
    Console console;
    private final Set<String> scriptSet = new HashSet<>();
    protected static String login;
    protected static String password;
    private Request request;
    private TCPClient tcpClient;

    /**
     * Конструктор для Runner.
     *
     * @param console Консоль.
     * @param client  TCPClient
     */
    public Runner(Console console, TCPClient client) {
        this.console = console;
        this.tcpClient = client;
        createCommandManager();
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

    /**
     * Запускает интерактивный режим выполнения программы.
     */
    public void run() {
        // обработка сигналов
        setSignalProcessing('\n' + "Для получения справки введите 'help', для завершения программы введите 'exit'" +
                        '\n' + console.getPrompt(),
                "INT", "TERM", "TSTP", "BREAK", "EOF");
        console.println("Подключение к серверу...");
        try {
            tcpClient.connect();
        } catch (TimeoutException e) {
            console.printError("Тайм-аут при подключении к серверу" + '\n' + "Подключение не установлено!");
        }
        interactiveMode();
    }

    public void interactiveMode() {
        var userScanner = Interrogator.getUserScanner();
        ExitCode commandStatus;
        String[] userCommand;
        try {
            do {
                console.prompt();
                userCommand = (userScanner.nextLine().trim() + " ").split(" ", 2);
                userCommand[1] = userCommand[1].trim();
                commandStatus = executeCommand(userCommand);
                if (commandStatus == Runner.ExitCode.ERROR) {
                    if (request != null) {
                        console.printError(request.toString());
                    }
                } else if (commandStatus == Runner.ExitCode.ERROR_NULL_RESPONSE) {
                    console.printError("Ответ от сервера не получен");
                }
            } while (commandStatus != ExitCode.EXIT);

        } catch (NoSuchElementException exception) {
            console.printError("Пользовательский ввод не обнаружен!");
        } catch (IllegalStateException exception) {
            console.printError("Непредвиденная ошибка!");
        }
    }

    public Runner.ExitCode scriptMode(String argument) {
        scriptSet.add(argument);
        if (!new File(argument).exists()) {
            argument = "../" + argument;
        }

        String[] userCommand;
        try (Scanner scriptScanner = new Scanner(new File(argument))) {
            if (!scriptScanner.hasNext()) throw new NoSuchElementException();
            Scanner tmpScanner = Interrogator.getUserScanner();
            Interrogator.setUserScanner(scriptScanner);
            Interrogator.setFileMode();

            do {
                userCommand = (scriptScanner.nextLine().trim() + " ").split(" ", 2);
                userCommand[1] = userCommand[1].trim();
                console.println(console.getPrompt() + String.join(" ", userCommand));
                if (userCommand[0].equals("execute_script")) {
                    if (scriptSet.contains(userCommand[1])) throw new ScriptRecursionException();
                }
                Runner.ExitCode commandStatus = executeCommand(userCommand);
                if (commandStatus == Runner.ExitCode.ERROR) {
                    console.printError(request.toString());
                } else if (commandStatus == Runner.ExitCode.ERROR_NULL_RESPONSE) {
                    console.printError("Ответ от сервера не получен");
                }
                if (commandStatus != Runner.ExitCode.OK) return commandStatus;
            } while (scriptScanner.hasNextLine());

            Interrogator.setUserScanner(tmpScanner);
            Interrogator.setUserMode();

        } catch (NoSuchElementException | IllegalStateException exception) {
            console.printError("Ошибка чтения из скрипта.");
            try {
                Interrogator.getUserScanner().hasNext();
                return scriptMode("");
            } catch (NoSuchElementException | IllegalStateException exception1) {
                console.printError("Экстренное завершение программы");
                userCommand = new String[2];
                userCommand[0] = "save";
                userCommand[1] = "";
                executeCommand(userCommand);
                userCommand[0] = "exit";
                executeCommand(userCommand);
                return Runner.ExitCode.ERROR;
            }
        } catch (FileNotFoundException exception) {
            console.printError("Файл не найден");
            return Runner.ExitCode.ERROR;
        } catch (ScriptRecursionException exception) {
            console.printError("Обнаружена рекурсия");
            return Runner.ExitCode.ERROR;
        } finally {
            scriptSet.remove(argument);
        }
        return Runner.ExitCode.OK;
    }

    /**
     * Создает менеджер команд приложения.
     *
     * @return менеджер команд
     */
    private void createCommandManager() {
        CommandManager.initClientCommandsBeforeRegistration(console);
    }

    /**
     * Коды завершения выполнения программы.
     */
    public enum ExitCode {
        OK,
        ERROR,
        EXIT, ERROR_NULL_RESPONSE,
    }

    private Runner.ExitCode executeCommand(String[] userCommand) {
        request = null;
        if (userCommand[0].isEmpty()) return Runner.ExitCode.OK;
        var command = CommandManager.getCommands().get(userCommand[0]);

        if (command == null) {
            console.printError("Команда '" + userCommand[0] + "' не найдена. Наберите 'help' для справки");
            return ExitCode.ERROR;
        }

        switch (userCommand[0]) {
            case "execute_script" -> {
                request = command.execute(userCommand);
                if (!request.isSuccess()) return Runner.ExitCode.ERROR;
                console.println("Выполнение скрипта '" + userCommand[1] + "'...");
                return scriptMode(userCommand[1]);
            }
            case "help", "history" -> {
                request = command.execute(userCommand);
                if (!request.isSuccess()) return Runner.ExitCode.ERROR;
                if ("help".equals(request.getCommand())) {
                    console.println("Справка по командам:");
                    CommandManager.getCommands().values().forEach(
                            com -> console.printTable(com.getName(), com.getDescription()));
                } else {
                    CommandManager.getCommandHistory().forEach(console::println);
                }
                return Runner.ExitCode.OK;
            }
            case "register", "login" -> {
                if (login != null || password != null) {
                    console.printError("Вы уже авторизованы");
                    return Runner.ExitCode.ERROR;
                }
                request = command.execute(userCommand);
                if (!request.isSuccess()) return Runner.ExitCode.ERROR;
                login = request.getLogin();
                password = request.getPassword();
                var response = tcpClient.sendCommand(request);
                if (response == null) return Runner.ExitCode.ERROR_NULL_RESPONSE;
                if (response.isSuccess()) {
                    console.println(response.toString());
                } else {
                    console.printError(response.toString());
                    login = null;
                    password = null;
                    request = null;
                    return Runner.ExitCode.ERROR;
                }
                CommandManager.initClientCommandsAfterRegistration(console, new TicketForm(console));
                return Runner.ExitCode.OK;
            }
            default -> {
                if (userCommand[0].equals("exit")) {
                    request = command.execute(userCommand);
                    try {
                        tcpClient.sendRequest(request);
                    } catch (IOException ignored) {
                    }
                    console.println(request.getData());
                    return Runner.ExitCode.EXIT;
                }
                if (login == null || password == null) {
                    console.printError("Вы не авторизованы");
                    return Runner.ExitCode.ERROR;
                }
                request = command.execute(userCommand);
                if (!request.isSuccess()) return Runner.ExitCode.ERROR;
                request.setLogin(login);
                request.setPassword(password);
                var response = tcpClient.sendCommand(request);
                if (response == null) return Runner.ExitCode.ERROR_NULL_RESPONSE;
                if (response.isSuccess()) {
                    console.println(response.toString());
                } else {
                    console.printError(response.toString());
                    request = null;
                    return Runner.ExitCode.ERROR;
                }
            }
        }
        return Runner.ExitCode.OK;
    }
}
