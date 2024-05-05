package ru.itmo.general.commands.core;

import ru.itmo.general.commands.CommandName;
import ru.itmo.general.exceptions.InvalidNumberOfElementsException;
import ru.itmo.general.network.Request;
import ru.itmo.general.network.Response;
import ru.itmo.general.commands.Command;
import ru.itmo.general.utility.base.Registered;
import ru.itmo.general.utility.console.Console;

/**
 * Command 'login'. Logs in a user to the system.
 *
 * @author [Your Name]
 */
public class Login extends Command {
    private Console console;
    private Registered userDAO;

    public Login() {
        super(CommandName.LOGIN, "{username} вход в систему");
    }

    /**
     * Constructor for creating an instance of the Login command.
     *
     * @param userDAO the user manager
     */
    public Login(Registered userDAO) {
        this();
        this.userDAO = userDAO;
    }

    public Login(Console console) {
        this();
        this.console = console;
    }

    /**
     * Executes the command.
     *
     * @param request the request to log in a user
     * @return the response indicating the success or failure of the command execution
     */
    @Override
    public Response execute(Request request) {
        try {
            String username = request.getLogin();
            String password = request.getPassword();

            if (!userDAO.verifyUserPassword(username, password)) {
                return new Response(false, "Неверное имя пользователя или пароль", null);
            }
            return new Response(true, "Вы успешно вошли в систему", null);
        } catch (Exception e) {
            return new Response(false, e.toString(), null);
        }
    }

    /**
     * Executes the command.
     *
     * @param arguments the command arguments (expects the username and password)
     * @return the request indicating the success or failure of the command execution
     */
    @Override
    public Request execute(String[] arguments) {
        try {
            if (arguments.length < 2 || arguments[1].isEmpty()) throw new InvalidNumberOfElementsException();
            console.println("* Вход в систему:");
            Request request = new Request(true, getName(), null);
            request.setLogin(arguments[1]);
            char[] passwordChars = console.readPassword("Enter password: ");
            if (passwordChars == null) {
                return new Request(false, getName(), "Cannot read password.");
            }
            if (passwordChars.length < 8) {
                return new Request(false, getName(), "Пароль слишком короткий. Введите более 8 символов");
            }
            request.setPassword(new String(passwordChars));
            return request;

        } catch (InvalidNumberOfElementsException exception) {
            return new Request(false, getName(), getUsingError());
        }
    }
}
