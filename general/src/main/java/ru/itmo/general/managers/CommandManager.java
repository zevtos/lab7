package ru.itmo.general.managers;


import lombok.Getter;
import ru.itmo.general.commands.Command;
import ru.itmo.general.commands.core.*;
import ru.itmo.general.commands.custom.*;
import ru.itmo.general.commands.special.SumOfPrice;
import ru.itmo.general.commands.update.Update;
import ru.itmo.general.models.Ticket;
import ru.itmo.general.models.forms.Form;
import ru.itmo.general.network.Request;
import ru.itmo.general.network.Response;
import ru.itmo.general.utility.base.Accessible;
import ru.itmo.general.utility.base.Registered;
import ru.itmo.general.utility.console.Console;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Управляет командами.
 *
 * @author zevtos
 */
public class CommandManager {
    /**
     * -- GETTER --
     * Получает историю команд.
     */
    @Getter
    private static final List<String> commandHistory = new ArrayList<>();
    /**
     * -- GETTER --
     * Получает словарь команд.
     */
    @Getter
    private static Map<String, Command> commands;

    /**
     * Регистрирует команду.
     *
     * @param commandName Название команды.
     * @param command     Команда
     */
    public static void register(String commandName, Command command) {
        commands.put(commandName, command);
    }

    public static void init() {
        commands = new HashMap<>();
        register("exit", new Exit());
    }

    public static void initServerCommands(CollectionManager<Ticket> ticketCollectionManager, Accessible dao, Registered userDao) {
        init();
        register("info", new Info(ticketCollectionManager));
        register("show", new Show(ticketCollectionManager));
        register("add", new Add(ticketCollectionManager));
        register("update", new Update(ticketCollectionManager, dao));
        register("remove_by_id", new Remove(ticketCollectionManager, dao));
        register("clear", new Clear(ticketCollectionManager));
        register("remove_first", new RemoveFirst(ticketCollectionManager, dao));
        register("remove_head", new RemoveHead(ticketCollectionManager, dao));
        register("add_if_min", new AddIfMin(ticketCollectionManager));
        register("sum_of_price", new SumOfPrice(ticketCollectionManager));
        register("min_by_discount", new MinByDiscount(ticketCollectionManager));
        register("max_by_name", new MaxByName(ticketCollectionManager));
        register("register", new Register(userDao));
        register("login", new Login(userDao));
    }

    public static void initClientCommands(Console console, Form<Ticket> ticketForm) {
        init();
        register("help", new Help());
        register("info", new Info());
        register("show", new Show());
        register("add", new Add(console, ticketForm));
        register("update", new Update(console, ticketForm));
        register("remove_by_id", new Remove());
        register("clear", new Clear());
        register("execute_script", new ExecuteScript());
        register("remove_first", new RemoveFirst());
        register("remove_head", new RemoveHead());
        register("add_if_min", new AddIfMin(console, ticketForm));
        register("sum_of_price", new SumOfPrice());
        register("min_by_discount", new MinByDiscount());
        register("max_by_name", new MaxByName());
        register("history", new History());
        register("register", new Register(console));
        register("login", new Login(console));
    }

    public static void initClientCommandsBeforeRegistration(Console console) {
        init();
        register("help", new Help());
        register("register", new Register(console));
        register("login", new Login(console));
    }

    public static void initClientCommandsAfterRegistration(Console console, Form<Ticket> ticketForm) {
        register("info", new Info());
        register("show", new Show());
        register("add", new Add(console, ticketForm));
        register("update", new Update(console, ticketForm));
        register("remove_by_id", new Remove());
        register("clear", new Clear());
        register("execute_script", new ExecuteScript());
        register("remove_first", new RemoveFirst());
        register("remove_head", new RemoveHead());
        register("add_if_min", new AddIfMin(console, ticketForm));
        register("sum_of_price", new SumOfPrice());
        register("min_by_discount", new MinByDiscount());
        register("max_by_name", new MaxByName());
        register("history", new History());
    }

    // Обработать команду, поступившую от клиента
    public static Response handle(Request request) {
        var command = getCommands().get(request.getCommand());
        if (command == null) return new Response(false, request.getCommand(), "Команда не найдена!");
        if (!"exit".equals(request.getCommand()) && !"save".equals(request.getCommand())) {
            return command.execute(request);
        }
        return new Response(false, "Неизвестная команда");
    }

    // Обработать команду, поступившую из консоли сервера
    public static void handleServer(Request request) {
        var command = getCommands().get(request.getCommand());
        if (command == null) return;
        if ("exit".equals(request.getCommand()) || "save".equals(request.getCommand())) command.execute(request);
    }

    /**
     * Добавляет команду в историю.
     *
     * @param command Команда.
     */
    public static void addToHistory(String command) {
        commandHistory.add(command);
    }
}
