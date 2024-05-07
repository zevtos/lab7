package ru.itmo.general.commands.core;

import ru.itmo.general.commands.Command;
import ru.itmo.general.commands.CommandName;
import ru.itmo.general.exceptions.InvalidFormException;
import ru.itmo.general.exceptions.InvalidNumberOfElementsException;
import ru.itmo.general.network.Request;
import ru.itmo.general.network.Response;
import ru.itmo.general.utility.base.Registered;
import ru.itmo.general.utility.console.Console;

import javax.management.InstanceAlreadyExistsException;

/**
 * Command 'register'. Registers a new user in the system.
 *
 * @author [Your Name]
 */
public class Register extends Command {
    private static final int MAX_USERNAME_LENGTH = 50;
    public static final int MIN_PASSWORD_LENGTH = 8;
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
            if (request.getLogin().length() >= MAX_USERNAME_LENGTH)
                throw new InvalidFormException("Длина имени пользователя должна быть < " + MAX_USERNAME_LENGTH);

            if (request.getPassword().length() < MIN_PASSWORD_LENGTH)
                throw new InvalidFormException("Длина пароля меньше " + MIN_PASSWORD_LENGTH);

            if (request.getUserId() != null) throw new InstanceAlreadyExistsException("User already exists");

            var user = userDAO.insertUser(request.getLogin(), request.getPassword());

            if (user == null) throw new InstanceAlreadyExistsException("User already exists");

            if (!user.validate())
                throw new InvalidFormException("Пользователь не зарегистрирован, поля пользователя не валидны!");

            return new Response(true, "Пользователь успешно зарегистрирован", null);
        } catch (InstanceAlreadyExistsException ex) {
            return new Response(false, ex.getMessage(), null);
        } catch (InvalidFormException invalid) {
            return new Response(false, invalid.getMessage());
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
            if (username.length() > MAX_USERNAME_LENGTH)
                throw new InvalidFormException("Длина имени пользователя должна быть < " + MAX_USERNAME_LENGTH);

            // Use Console to read the password without echoing characters
            char[] passwordChars = console.readPassword("Введите пароль: ", MIN_PASSWORD_LENGTH);

            if (passwordChars == null)
                throw new InvalidFormException("Невозможно прочитать пароль.");
            if (passwordChars.length < MIN_PASSWORD_LENGTH)
                throw new InvalidFormException("Длина пароля должна быть >= " + MIN_PASSWORD_LENGTH);
            String password = new String(passwordChars);
            Request request = new Request(true, getName(), null);
            request.setLogin(username);
            request.setPassword(password);
            return request;

        } catch (InvalidFormException invalid) {
            return new Request(false, getName(), invalid.getMessage());
        } catch (InvalidNumberOfElementsException exception) {
            return new Request(false, getName(), getUsingError());
        }
    }
}
