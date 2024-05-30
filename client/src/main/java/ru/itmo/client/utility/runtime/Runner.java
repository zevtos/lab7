package ru.itmo.client.utility.runtime;

import ru.itmo.client.MainApp;
import ru.itmo.client.utility.console.StandartConsole;
import ru.itmo.general.exceptions.ScriptRecursionException;
import ru.itmo.general.managers.CommandManager;
import ru.itmo.general.models.Ticket;
import ru.itmo.general.models.forms.TicketForm;
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

    public ExitCode scriptMode(File file) {
        System.out.println("Script mode: " + file.getAbsolutePath());
        String argument = file.getAbsolutePath();
        scriptSet.add(argument);
        if (!file.exists()) {
            return ExitCode.ERROR;
        }
        String[] userCommand;
        try (Scanner scriptScanner = new Scanner(file)) {
            System.out.println("started successfully");
            if (!scriptScanner.hasNext()) throw new NoSuchElementException();
            Interrogator.setUserScanner(scriptScanner);
            Interrogator.setFileMode();
            do {
                System.out.println("starting script");
                userCommand = (scriptScanner.nextLine().trim() + " ").split(" ", 2);
                userCommand[1] = userCommand[1].trim();
                if (userCommand[0].equals("execute_script")) {
                    if (scriptSet.contains(userCommand[1])) throw new ScriptRecursionException();
                }
                var command = CommandManager.getCommands().get(userCommand[0]);
                if (command == null) {
                    return ExitCode.ERROR;
                }
                var req = command.execute(userCommand);
                if (!req.isSuccess()) return Runner.ExitCode.ERROR;
                Response response = connection.sendCommand(req);
                System.out.println(response.toString());
                ExitCode commandStatus;
                if (response.isSuccess()) {
                    commandStatus = ExitCode.OK;
                } else {
                    commandStatus = ExitCode.ERROR;
                }
                if (commandStatus != ExitCode.OK) return commandStatus;
            } while (scriptScanner.hasNextLine());

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
        CommandManager.initClientCommandsAfterRegistration(new TicketForm(new StandartConsole()));
    }

    public ExitCode executeRegister(String username, String password) {
        Response response = connection.sendCommand(new String[]{"register", username, password});
        if (response.isSuccess()) {
            return ExitCode.OK;
        } else {
            MainApp.showAlert("Ошибка регистрации", response.getMessage(), (response.getData() == null) ? "" : response.getData().toString());
            return ExitCode.ERROR;
        }
    }

    public ExitCode executeLogin(String username, String password) {
        Response response = connection.sendCommand(new String[]{"login", username, password});
        if (response.isSuccess()) {
            return ExitCode.OK;
        } else {
            MainApp.showAlert("Ошибка входа", response.getMessage(), (response.getData() == null) ? "" : response.getData().toString());
            return ExitCode.ERROR;
        }
    }

    public List<Ticket> fetchTickets() {
        List<Ticket> tickets = connection.receiveTickets();
        return tickets != null ? tickets : new ArrayList<>();
    }

    public boolean addTicket(Ticket newTicket) {
        Response response = connection.sendCommand("add", newTicket);

        if (response.isSuccess()) {
            newTicket.setId((Integer) response.getData());
            return true;
        } else {
            MainApp.showAlert("Ошибка добавления", "Билет не был добавлен", response.getMessage());
            return false;
        }
    }


    public void updateTicket(Ticket selectedTicket) {
        connection.sendCommand("update", selectedTicket);
    }

    public void deleteTicket(Ticket selectedTicket) {
        connection.sendCommand("remove_by_id", selectedTicket);
    }

    public boolean clearTickets() {
        Response response = connection.sendCommand("clear", null);
        if (response.isSuccess()) {
            return true;
        } else {
            MainApp.showAlert("Ошибка очистки билетов", "Билеты не были добавлены", response.getMessage());
            return false;
        }
    }

    // В классе Runner
    public String getInfo() {
        Response response = connection.sendCommand("info", null);
        return (String) response.getData();
    }

    public Integer getCurrentUserId() {
        return connection.getCurrentUserId();
    }

    public boolean addTicketIfMin(Ticket newTicket) {
        Response response = connection.sendCommand("add_if_min", newTicket);

        if (response.isSuccess()) {
            newTicket.setId((Integer) response.getData());
            return true;
        } else {
            MainApp.showAlert("Ошибка добавления", "Билет не был добавлен", response.getMessage());
            return false;
        }
    }

    public Response sumOfPrice() {
        return connection.sendCommand("sum_of_price", null);
    }

    public String getCurrentUsername() {
        return connection.getLogin();
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

    private void showError(String message) {
        // Здесь можно добавить код для отображения предупреждения в GUI
        System.err.println(message); // Временно выводим в консоль
    }
}
