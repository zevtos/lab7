package ru.itmo.client.utility.runtime;

import ru.itmo.client.managers.CommandManager;
import ru.itmo.client.network.TCPClient;
import ru.itmo.client.utility.Interrogator;
import ru.itmo.client.utility.console.Console;
import ru.itmo.general.exceptions.ScriptRecursionException;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

/**
 * Запускает выполнение скрипта команд.
 * @author zevtos
 */
public class ScriptRunner implements ModeRunner {
    private final TCPClient tcpClient;
    private final Console console;
    private final CommandManager commandManager;
    private final Set<String> scriptSet = new HashSet<>();

    /**
     * Конструктор для ScriptRunner.
     * @param console Консоль.
     * @param commandManager Менеджер команд.
     */
    public ScriptRunner(TCPClient tcpClient, Console console, CommandManager commandManager) {
        this.tcpClient = tcpClient;
        this.console = console;
        this.commandManager = commandManager;
    }

    /**
     * Запускает выполнение скрипта.
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
                if (commandStatus != Runner.ExitCode.OK) return commandStatus;
            } while (scriptScanner.hasNextLine());

            Interrogator.setUserScanner(tmpScanner);
            Interrogator.setUserMode();

        } catch (NoSuchElementException | IllegalStateException exception) {
            console.printError("Ошибка ввода.");
            try {
                Interrogator.getUserScanner().hasNext();
                return run("");
            } catch (NoSuchElementException | IllegalStateException exception1){
                console.printError("Экстренное завершение программы");
                userCommand = new String[2];
                userCommand[0] = "save";
                userCommand[1] = "";
                executeCommand(userCommand);
                userCommand[0] = "exit";
                executeCommand(userCommand);
                return Runner.ExitCode.ERROR;
            }
        } catch (FileNotFoundException exception){
            console.printError("Файл не найден");
            return Runner.ExitCode.ERROR;
        } catch (ScriptRecursionException exception){
            console.printError("Обнаружена рекурсия");
            return Runner.ExitCode.ERROR;
        } finally {
            scriptSet.remove(argument);
        }
        return Runner.ExitCode.OK;
    }

    private Runner.ExitCode executeCommand(String[] userCommand) {
        if (userCommand[0].isEmpty()) return Runner.ExitCode.OK;
        var command = commandManager.getCommands().get(userCommand[0]);

        if (command == null) throw new NoSuchElementException();

        switch (userCommand[0]) {
            case "exit" -> {
                var req = command.execute(userCommand);
                if (!req.isSuccess()) return Runner.ExitCode.ERROR;
                tcpClient.sendCommand(req);
                return Runner.ExitCode.EXIT;
            }
            case "execute_script" -> {
                var req = command.execute(userCommand);
                if (!req.isSuccess()) return Runner.ExitCode.ERROR;
                else return run(userCommand[1]); // Interactive mode doesn't support script execution.
            }
            default -> {
                var req = command.execute(userCommand);
                if (!req.isSuccess()) return Runner.ExitCode.ERROR;
                tcpClient.sendCommand(req);
            }
        }
        return Runner.ExitCode.OK;
    }
}
