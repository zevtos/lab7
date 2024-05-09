package ru.itmo.client.utility.runtime;

import ru.itmo.client.network.TCPClient;
import ru.itmo.general.exceptions.ScriptRecursionException;
import ru.itmo.general.managers.CommandManager;
import ru.itmo.general.network.Request;
import ru.itmo.general.utility.Interrogator;
import ru.itmo.general.utility.console.Console;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashSet;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.Set;

/**
 * Запускает выполнение скрипта команд.
 *
 * @author zevtos
 */
public class ScriptRunner implements ModeRunner {
    private final TCPClient tcpClient;
    private final Console console;
    private final Set<String> scriptSet = new HashSet<>();
    private Request request = null;
    protected static String login;
    protected static String password;
    /**
     * Конструктор для ScriptRunner.
     *
     * @param console Консоль.
     */
    public ScriptRunner(TCPClient tcpClient, Console console) {
        this.tcpClient = tcpClient;
        this.console = console;
    }

    /**
     * Запускает выполнение скрипта.
     *
     * @param argument Аргумент - путь к файлу скрипта.
     * @return Код завершения выполнения скрипта.
     */
    @Override
    public Runner.ExitCode run(String argument) {
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
                    console.printError(getClass(), request.toString());
                } else if (commandStatus == Runner.ExitCode.ERROR_NULL_RESPONSE) {
                    console.printError(getClass(), "Ответ от сервера не получен");
                }
                if (commandStatus != Runner.ExitCode.OK) return commandStatus;
            } while (scriptScanner.hasNextLine());

            Interrogator.setUserScanner(tmpScanner);
            Interrogator.setUserMode();

        } catch (NoSuchElementException | IllegalStateException exception) {
            console.printError(getClass(), "Ошибка ввода.");
            try {
                Interrogator.getUserScanner().hasNext();
                return run("");
            } catch (NoSuchElementException | IllegalStateException exception1) {
                console.printError(getClass(), "Экстренное завершение программы");
                userCommand = new String[2];
                userCommand[0] = "save";
                userCommand[1] = "";
                executeCommand(userCommand);
                userCommand[0] = "exit";
                executeCommand(userCommand);
                return Runner.ExitCode.ERROR;
            }
        } catch (FileNotFoundException exception) {
            console.printError(getClass(), "Файл не найден");
            return Runner.ExitCode.ERROR;
        } catch (ScriptRecursionException exception) {
            console.printError(getClass(), "Обнаружена рекурсия");
            return Runner.ExitCode.ERROR;
        } finally {
            scriptSet.remove(argument);
        }
        return Runner.ExitCode.OK;
    }

    private Runner.ExitCode executeCommand(String[] userCommand) {
        request = null;

        if (userCommand[0].isEmpty()) return Runner.ExitCode.OK;
        var command = CommandManager.getCommands().get(userCommand[0]);

        if (command == null) throw new NoSuchElementException();

        switch (userCommand[0]) {
            case "execute_script" -> {
                request = command.execute(userCommand);
                if (!request.isSuccess()) return Runner.ExitCode.ERROR;
                console.println("Выполнение скрипта '" + userCommand[1] + "'...");
                return run(userCommand[1]);
            }
            case "help", "history" -> {
                request = command.execute(userCommand);
                if (!request.isSuccess()) return Runner.ExitCode.ERROR;
                if ("help".equals(request.getCommand())) {
                    console.println("Справка по командам:");
                    CommandManager.getCommands().values().forEach(com ->
                            console.printTable(com.getName(), com.getDescription()
                            ));
                } else {
                    CommandManager.getCommandHistory().forEach(console::println);
                }
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
                    console.printError(getClass(), "Вы не авторизованы");
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
                    console.printError(getClass(), response.toString());
                    request = null;
                    return Runner.ExitCode.ERROR;
                }
            }
        }
        return Runner.ExitCode.OK;
    }
}
