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
 * @autor zevtos
 */
public class Login extends Command {
    private Registered userDAO;

    public Login() {
        super(CommandName.LOGIN, "{username} log in to the system");
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

            if (!userDAO.verifyUserPassword(username, password)) {
                return new Response(false, "Invalid username or password", null);
            }

            User user = userDAO.getUserByUsername(username);

            if (user == null) {
                return new Response(false, "User not found", null);
            }

            if (user.getId() == null) {
                return new Response(false, "User ID is null", null);
            }

            return new Response(true, "You have successfully logged in", user.getId());
        } catch (Exception e) {
            System.out.println("Exception during login: " + e); // Debug message
            return new Response(false, e.toString(), null);
        }
    }

    @Override
    public Request execute(String[] arguments) {
        try {
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
