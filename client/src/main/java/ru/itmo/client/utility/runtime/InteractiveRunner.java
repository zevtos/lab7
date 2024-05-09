package ru.itmo.client.utility.runtime;

import ru.itmo.client.network.TCPClient;
import ru.itmo.general.managers.CommandManager;
import ru.itmo.general.models.forms.TicketForm;
import ru.itmo.general.network.Request;
import ru.itmo.general.utility.Interrogator;
import ru.itmo.general.utility.console.Console;

import java.io.IOException;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.concurrent.TimeoutException;

/**
 * Запускает выполнение интерактивного режима.
 *
 * @author zevtos
 */
public class InteractiveRunner implements ModeRunner {
    private final TCPClient tcpClient;
    private final Console console;
    private final ScriptRunner scriptRunner;
    private Request request = null;
    protected String login = null;
    protected String password = null;

    /**
     * Конструктор для InteractiveRunner.
     *
     * @param console      Консоль.
     * @param scriptRunner Запускает выполнение скриптов.
     */
    public InteractiveRunner(TCPClient tcpClient, Console console, ScriptRunner scriptRunner) {
        this.tcpClient = tcpClient;
        this.console = console;
        this.scriptRunner = scriptRunner;
    }

    /**
     * Запускает выполнение интерактивного режима.
     *
     * @param argument Не используется в интерактивном режиме.
     * @return Код завершения выполнения интерактивного режима.
     */
    @Override
    public Runner.ExitCode run(String argument) {
        console.println("Подключение к серверу...");
        try {
            tcpClient.connect();
        } catch (TimeoutException e) {
            console.printError(getClass(), "Тайм-аут при подключении к серверу" + '\n' + "Подключение не установлено!");
        }
        Runner.ExitCode commandStatus;
        String[] userCommand;
        try (Scanner userScanner = Interrogator.getUserScanner()) {
            do {
                console.prompt();
                String inputLine = "";
                inputLine = userScanner.nextLine().trim();
                userCommand = (inputLine + " ").split(" ", 2);
                userCommand[1] = userCommand[1].trim();

                try {
                    commandStatus = executeCommand(userCommand);
                } catch (NoSuchElementException e) {
                    console.printError(getClass(), "Команда '" + userCommand[0] + "' не найдена. Введите 'help' для помощи");
                    commandStatus = Runner.ExitCode.ERROR;
                }
                if (commandStatus == Runner.ExitCode.ERROR) {
                    if (request != null) {
                        console.printError(getClass(), request.toString());
                    }
                } else if (commandStatus == Runner.ExitCode.ERROR_NULL_RESPONSE) {
                    console.printError(getClass(), "Ответ от сервера не получен");
                }
                CommandManager.addToHistory(userCommand[0]);
            } while (commandStatus != Runner.ExitCode.EXIT);
            return commandStatus;
        } catch (NoSuchElementException | IllegalStateException exception) {
            console.printError(getClass(), "Ошибка ввода.");
            try {
                Interrogator.getUserScanner().hasNext();
                return run("");
            } catch (NoSuchElementException | IllegalStateException exception1) {
                console.printError(getClass(), "Экстренное завершение программы");
                userCommand = new String[2];
                userCommand[1] = "";
                userCommand[0] = "exit";
                executeCommand(userCommand);
                return Runner.ExitCode.ERROR;
            }
        }
    }

    private Runner.ExitCode executeCommand(String[] userCommand) throws NoSuchElementException {
        request = null;
        if (userCommand[0].isEmpty()) return Runner.ExitCode.OK;
        var command = CommandManager.getCommands().get(userCommand[0]);

        if (command == null) throw new NoSuchElementException();

        switch (userCommand[0]) {
            case "execute_script" -> {
                request = command.execute(userCommand);
                if (!request.isSuccess()) return Runner.ExitCode.ERROR;
                console.println("Выполнение скрипта '" + userCommand[1] + "'...");
                return scriptRunner.run(userCommand[1]);
            }
            case "help", "history" -> {
                request = command.execute(userCommand);
                if (!request.isSuccess()) return Runner.ExitCode.ERROR;
                if ("help".equals(request.getCommand())) {
                    console.println("Справка по командам:");
                    CommandManager.getCommands().values().forEach(com -> console.printTable(com.getName(), com.getDescription()));
                } else {
                    CommandManager.getCommandHistory().forEach(console::println);
                }
                return Runner.ExitCode.OK;
            }
            case "register", "login" -> {
                if (login != null || password != null) {
                    console.printError(getClass(), "Вы уже авторизованы");
                    return Runner.ExitCode.ERROR;
                }
                request = command.execute(userCommand);
                if (!request.isSuccess()) return Runner.ExitCode.ERROR;
                login = request.getLogin();
                password = request.getPassword();
                ScriptRunner.login = request.getLogin();
                ScriptRunner.password = request.getPassword();
                var response = tcpClient.sendCommand(request);
                if (response == null) return Runner.ExitCode.ERROR_NULL_RESPONSE;
                if (response.isSuccess()) {
                    console.println(response.toString());
                } else {
                    console.printError(getClass(), response.toString());
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

