package ru.itmo.general.commands.core;

import ru.itmo.general.commands.Command;
import ru.itmo.general.commands.CommandName;
import ru.itmo.general.exceptions.InvalidNumberOfElementsException;
import ru.itmo.general.network.Request;
import ru.itmo.general.network.Response;
import ru.itmo.general.utility.base.Registered;
import ru.itmo.general.utility.console.Console;

/**
 * Command 'register'. Registers a new user in the system.
 *
 * @author [Your Name]
 */
public class Register extends Command {
    private Console console;
    private Registered userDAO;

    public Register() {
        super(CommandName.REGISTER, "{username} регистрация нового пользователя");
    }

    /**
     * Constructor for creating an instance of the Register command.
     *
     * @param userDAO the user manager
     */
    public Register(Registered userDAO) {
        this();
        this.userDAO = userDAO;
    }

    public Register(Console console) {
        this();
        this.console = console;
    }

    /**
     * Executes the command.
     *
     * @param request the request to register a user
     * @return the response indicating the success or failure of the command execution
     */
    @Override
    public Response execute(Request request) {
        try {
            var user = userDAO.insertUser(request.getLogin(), request.getPassword());
            if (request.getUserId() != null || user == null) {
                return new Response(false, "Пользователь с таким именем уже существует", null);
            }
            System.out.println(user.toString());
            if (!user.validate() || request.getPassword().length() < 8) {
                return new Response(false, "Пользователь не зарегистрирован, поля пользователя не валидны!");
            }

            return new Response(true, null, null);
        } catch (Exception e) {
            return new Response(false, e.toString(), -1);
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
            console.println("* Регистрация нового пользователя:");

            String username = arguments[1];

            // Use Console to read the password without echoing characters
            char[] passwordChars = console.readPassword("Enter password: ");
            if (passwordChars == null || passwordChars.length < 8) {
                return new Request(false, getName(), "Cannot read password.");
            }
            String password = new String(passwordChars);
            Request request = new Request(true, getName(), null);
            request.setLogin(username);
            request.setPassword(password);
            return request;

        } catch (InvalidNumberOfElementsException exception) {
            return new Request(false, getName(), getUsingError());
        }
    }
}
