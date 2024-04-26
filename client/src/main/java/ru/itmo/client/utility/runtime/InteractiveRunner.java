package ru.itmo.client.utility.runtime;

import ru.itmo.client.managers.CommandManager;
import ru.itmo.client.network.TCPClient;
import ru.itmo.client.utility.Interrogator;
import ru.itmo.client.utility.console.Console;

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
    private final CommandManager commandManager;
    private final ScriptRunner scriptRunner;

    /**
     * Конструктор для InteractiveRunner.
     *
     * @param console        Консоль.
     * @param commandManager Менеджер команд.
     * @param scriptRunner   Запускает выполнение скриптов.
     */
    public InteractiveRunner(TCPClient tcpClient, Console console, CommandManager commandManager, ScriptRunner scriptRunner) {
        this.tcpClient = tcpClient;
        this.console = console;
        this.commandManager = commandManager;
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
        String[] userCommand;
        try (Scanner userScanner = Interrogator.getUserScanner()) {
            Runner.ExitCode commandStatus;
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
                commandManager.addToHistory(userCommand[0]);
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

    private Runner.ExitCode executeCommand(String[] userCommand) {
        if (userCommand[0].isEmpty()) return Runner.ExitCode.OK;
        var command = commandManager.getCommands().get(userCommand[0]);

        if (command == null) throw new NoSuchElementException();

        switch (userCommand[0]) {
            case "execute_script" -> {
                var req = command.execute(userCommand);
                if (!req.isSuccess()) return Runner.ExitCode.ERROR;
                else return scriptRunner.run(userCommand[1]);
            }
            case "help", "history" -> {
                var req = command.execute(userCommand);
                if (!req.isSuccess()) return Runner.ExitCode.ERROR;
                else return Runner.ExitCode.OK;
            }
            default -> {
                var req = command.execute(userCommand);
                if (!req.isSuccess()) return Runner.ExitCode.ERROR;
                if (req.getCommand().equals("exit")) {
                    tcpClient.sendCommand(req);
                    return Runner.ExitCode.EXIT;
                }
                var response = tcpClient.sendCommand(req);
                if (response.isSuccess()) {
                    console.println(response.toString());
                } else {
                    console.printError(getClass(), response.toString());
                }
            }
        }
        return Runner.ExitCode.OK;
    }
}

