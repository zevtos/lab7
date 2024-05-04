package ru.itmo.client.utility.runtime;


import ru.itmo.client.forms.TicketForm;
import ru.itmo.general.managers.CommandManager;
import ru.itmo.client.network.TCPClient;
import ru.itmo.general.utility.console.Console;
import sun.misc.Signal;

/**
 * Запускает выполнение программы.
 * @author zevtos
 */
public class Runner {
    Console console;
    InteractiveRunner interactiveRunner;
    ScriptRunner scriptRunner;

    /**
     * Конструктор для Runner.
     * @param console Консоль.
     * @param client TCPClient
     */
    public Runner(Console console, TCPClient client) {
        this.console = console;
        createCommandManager();
        this.scriptRunner = new ScriptRunner(client, console);
        this.interactiveRunner = new InteractiveRunner(client, console, scriptRunner);
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
        EXIT, ERROR_NULL_RESPONSE,
    }
    /**
     * Создает менеджер команд приложения.
     *
     * @return менеджер команд
     */
    private void createCommandManager() {
        CommandManager.initClientCommands(console, new TicketForm(console));
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
