package ru.itmo.client.utility.runtime;


import ru.itmo.client.commands.core.*;
import ru.itmo.client.commands.custom.*;
import ru.itmo.client.commands.special.SumOfPrice;
import ru.itmo.client.commands.update.Update;
import ru.itmo.client.managers.CommandManager;
import ru.itmo.client.network.TCPClient;
import ru.itmo.client.utility.console.Console;
import sun.misc.Signal;

import java.util.NoSuchElementException;

/**
 * Запускает выполнение программы.
 * @author zevtos
 */
public class Runner {
    Console console;
    CommandManager commandManager;
    InteractiveRunner interactiveRunner;
    ScriptRunner scriptRunner;

    /**
     * Конструктор для Runner.
     * @param console Консоль.
     * @param commandManager Менеджер команд.
     */
    public Runner(Console console, CommandManager commandManager, TCPClient client) {
        this.console = console;
        this.commandManager = commandManager;
        this.scriptRunner = new ScriptRunner(client, console, commandManager);
        this.interactiveRunner = new InteractiveRunner(client, console, commandManager, scriptRunner);
    }

    public Runner(Console console, TCPClient client) {
        this.console = console;
        this.commandManager = this.createCommandManager(client);
        this.scriptRunner = new ScriptRunner(client, console, commandManager);
        this.interactiveRunner = new InteractiveRunner(client, console, commandManager, scriptRunner);
    }

    /**
     * Запускает интерактивный режим выполнения программы.
     */
    public void run() {
        // обработка сигналов
        setSignalProcessing('\n' + "Для получения справки введите 'help', для завершения программы введите 'exit'" + '\n' + console.getPrompt(),
                "INT", "TERM", "TSTP", "BREAK", "EOF");

        interactiveRunner.run("");
    }

    /**
     * Запускает выполнение скрипта.
     * @param argument Аргумент - путь к файлу скрипта.
     */
    public void run_script(String argument){
        scriptRunner.run(argument);
    }

    /**
     * Коды завершения выполнения программы.
     */
    public enum ExitCode {
        OK,
        ERROR,
        EXIT,
    }
    /**
     * Создает менеджер команд приложения.
     *
     * @return менеджер команд
     */
    private CommandManager createCommandManager(TCPClient client) {
        return new CommandManager() {{
            register("help", new Help(console, this));
            register("info", new Info(console));
            register("show", new Show(console));
            register("add", new Add(console));
            register("update", new Update(console));
            register("remove_by_id", new Remove(console));
            register("clear", new Clear(console));
            register("execute_script", new ExecuteScript(console));
            register("exit", new Exit(console));
            register("remove_first", new RemoveFirst(console));
            register("remove_head", new RemoveHead(console));
            register("add_if_min", new AddIfMin(console));
            register("sum_of_price", new SumOfPrice(console));
            register("min_by_discount", new MinByDiscount(console));
            register("max_by_name", new MaxByName(console));
            register("history", new History(console, this));
            register("add_person", new AddPerson(console));
        }};
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
}
