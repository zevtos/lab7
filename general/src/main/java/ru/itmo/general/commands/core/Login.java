package ru.itmo.general.commands.core;

import ru.itmo.general.commands.Command;
import ru.itmo.general.commands.CommandName;
import ru.itmo.general.exceptions.InvalidNumberOfElementsException;
import ru.itmo.general.models.User;
import ru.itmo.general.network.Request;
import ru.itmo.general.network.Response;
import ru.itmo.general.utility.base.Registered;

/**
 * Command 'login'. Logs in a user to the system.
 *
 * @author zevtos
 */
public class Login extends Command {
    private Registered userDAO;

    public Login() {
        super(CommandName.LOGIN, "{username} вход в систему");
    }

    public Login(Registered userDAO) {
        this();
        this.userDAO = userDAO;
    }

    @Override
    public Response execute(Request request) {
        try {
            String username = request.getLogin();
            String password = request.getPassword();

            System.out.println("Received login request for username: " + username); // Debug message

            if (!userDAO.verifyUserPassword(username, password)) {
                System.out.println("Invalid username or password for username: " + username); // Debug message
                return new Response(false, "Неверное имя пользователя или пароль", null);
            }

            User user = userDAO.getUserByUsername(username);

            if (user == null) {
                System.out.println("User not found for username: " + username); // Debug message
                return new Response(false, "User not found", null);
            }

            if (user.getId() == null) {
                System.out.println("User ID is null for username: " + username); // Debug message
                return new Response(false, "User ID is null", null);
            }

            System.out.println("User logged in successfully: " + user); // Debug message
            return new Response(true, "Вы успешно вошли в систему", user.getId());
        } catch (Exception e) {
            System.out.println("Exception during login: " + e); // Debug message
            return new Response(false, e.toString(), null);
        }
    }


    @Override
    public Request execute(String[] arguments) {
        try {
            System.out.println(arguments);
            if (arguments.length <= 2 || arguments[1].isEmpty() || arguments[2].isEmpty())
                throw new InvalidNumberOfElementsException();
            Request request = new Request(true, getName(), null);
            request.setLogin(arguments[1]);
            if (arguments[2].isEmpty()) throw new InvalidNumberOfElementsException();
            request.setPassword(arguments[2]);
            return request;
        } catch (InvalidNumberOfElementsException exception) {
            return new Request(false, getName(), getUsingError());
        }
    }
}
