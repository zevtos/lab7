package ru.itmo.client.utility.runtime;

import ru.itmo.general.exceptions.ScriptRecursionException;
import ru.itmo.general.managers.CommandManager;
import ru.itmo.general.models.Ticket;
import ru.itmo.general.network.Request;
import ru.itmo.general.network.Response;
import ru.itmo.general.utility.Interrogator;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

/**
 * Запускает выполнение программы.
 */
public class Runner {
    private final Set<String> scriptSet = new HashSet<>();
    protected String login;
    protected String password;
    private Request request;
    private ServerConnection connection;

    /**
     * Конструктор для Runner.
     *
     * @param connection TCPClient
     */
    public Runner(ServerConnection connection) {
        this.connection = connection;
        createCommandManager();
    }

    public ExitCode scriptMode(String argument) {
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
                if (userCommand[0].equals("execute_script")) {
                    if (scriptSet.contains(userCommand[1])) throw new ScriptRecursionException();
                }
                ExitCode commandStatus = executeCommand(userCommand);
                if (commandStatus != ExitCode.OK) return commandStatus;
            } while (scriptScanner.hasNextLine());

            Interrogator.setUserScanner(tmpScanner);
            Interrogator.setUserMode();

        } catch (NoSuchElementException | IllegalStateException exception) {
            showError("Ошибка чтения из скрипта.");
            return ExitCode.ERROR;
        } catch (FileNotFoundException exception) {
            showError("Файл не найден");
            return ExitCode.ERROR;
        } catch (ScriptRecursionException exception) {
            showError("Обнаружена рекурсия");
            return ExitCode.ERROR;
        } finally {
            scriptSet.remove(argument);
        }
        return ExitCode.OK;
    }

    /**
     * Создает менеджер команд приложения.
     */
    private void createCommandManager() {
        CommandManager.initClientCommandsBeforeRegistration();
    }

    public ExitCode executeRegister(String username, String password) {
        return null;
    }

    public ExitCode executeLogin(String username, String password) {
        return executeCommand(new String[]{"login", username, password});
    }

    public List<Ticket> fetchTickets() {
        List<Ticket> tickets = connection.receiveTickets();
        return tickets != null ? tickets : new ArrayList<>();
    }

    public void addTicket(Ticket newTicket) {
        connection.sendCommand("add", newTicket);
    }

    public void updateTicket(Ticket selectedTicket) {
        connection.sendCommand("update", selectedTicket);
    }

    public void deleteTicket(Ticket selectedTicket) {
        connection.sendCommand("remove_by_id", selectedTicket);
    }

    /**
     * Коды завершения выполнения программы.
     */
    public enum ExitCode {
        OK,
        ERROR,
        EXIT,
        ERROR_NULL_RESPONSE,
    }

    private ExitCode executeCommand(String[] userCommand) {
        request = null;
        if (userCommand[0].isEmpty()) return ExitCode.OK;
        var command = CommandManager.getCommands().get(userCommand[0]);

        if (command == null) {
            showError("Команда '" + userCommand[0] + "' не найдена. Наберите 'help' для справки");
            return ExitCode.ERROR;
        }

        request = command.execute(userCommand);
        System.out.println(request);
        if (!request.isSuccess()) {
            showError("Ошибка выполнения команды: " + userCommand[0]);
            return ExitCode.ERROR;
        }

        if ("execute_script".equals(userCommand[0])) {
            return scriptMode(userCommand[1]);
        }
        Response response = connection.sendCommand(request);
        System.out.println(response);
        if (response.isSuccess()) {
            return ExitCode.OK;
        } else {
            showError(response.getMessage());
            return ExitCode.ERROR;
        }
    }

    private void showError(String message) {
        // Здесь можно добавить код для отображения предупреждения в GUI
        System.err.println(message); // Временно выводим в консоль
    }
}
