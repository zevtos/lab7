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

            if (!userDAO.verifyUserPassword(username, password)) {
                return new Response(false, "Неверное имя пользователя или пароль", null);
            }

            User user = userDAO.getUserByUsername(username);

            if (user == null) {
                return new Response(false, "User not found", null);
            }

            if (user.getId() == null) {
                return new Response(false, "User ID is null", null);
            }

            return new Response(true, "Вы успешно вошли в систему", user.getId());
        } catch (Exception e) {
            return new Response(false, e.toString(), null);
        }
    }
}
