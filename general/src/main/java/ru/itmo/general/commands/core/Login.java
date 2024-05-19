package ru.itmo.general.commands.core;

import ru.itmo.general.commands.Command;
import ru.itmo.general.commands.CommandName;
import ru.itmo.general.exceptions.InvalidNumberOfElementsException;
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

            if (!userDAO.verifyUserPassword(username, password)) {
                return new Response(false, "Неверное имя пользователя или пароль", null);
            }
            return new Response(true, "Вы успешно вошли в систему", null);
        } catch (Exception e) {
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
            if (arguments.length < 3 || arguments[2].isEmpty()) throw new InvalidNumberOfElementsException();
            request.setPassword(arguments[2]);
            return request;
        } catch (InvalidNumberOfElementsException exception) {
            return new Request(false, getName(), getUsingError());
        }
    }
}
