package ru.itmo.client.forms;

import ru.itmo.client.utility.Interrogator;
import ru.itmo.client.utility.console.Console;
import ru.itmo.general.exceptions.InvalidFormException;
import ru.itmo.general.exceptions.InvalidRangeException;
import ru.itmo.general.exceptions.InvalidScriptInputException;
import ru.itmo.general.models.Coordinates;

import java.util.NoSuchElementException;

/**
 * Форма для ввода координат.
 * @author zevtos
 */
public class CoordinatesForm extends Form<Coordinates> {
    private final Console console;

    /**
     * Конструктор формы координат.
     *
     * @param console консоль для взаимодействия с пользователем
     */
    public CoordinatesForm(Console console) {
        this.console = console;
    }

    @Override
    public Coordinates build() throws InvalidScriptInputException, InvalidFormException {
        var coordinates = new Coordinates(askX(), askY());
        if (!coordinates.validate()) throw new InvalidFormException();
        return coordinates;
    }

    /**
     * Запрашивает у пользователя координату X.
     *
     * @return Координата X
     * @throws InvalidScriptInputException если возникает ошибка ввода при выполнении скрипта
     */
    public double askX() throws InvalidScriptInputException {
        var fileMode = Interrogator.fileMode();
        double x;
        while (true) {
            try {
                console.println("Введите координату X:");
                console.prompt();
                var strX = Interrogator.getUserScanner().nextLine().trim();
                if (fileMode) console.println(strX);

                x = Double.parseDouble(strX);
                break;
            } catch (NoSuchElementException exception) {
                console.printError("Координата X не распознана!");
                if (fileMode) throw new InvalidScriptInputException();
            } catch (NumberFormatException exception) {
                console.printError("Координата X должна быть числом!");
                if (fileMode) throw new InvalidScriptInputException();
            } catch (NullPointerException | IllegalStateException exception) {
                console.printError("Непредвиденная ошибка!");
                System.exit(0);
            }
        }
        return x;
    }

    /**
     * Запрашивает у пользователя координату Y.
     *
     * @return Координата Y
     * @throws InvalidScriptInputException если возникает ошибка ввода при выполнении скрипта
     */
    public Float askY() throws InvalidScriptInputException {
        var fileMode = Interrogator.fileMode();
        float y;
        while (true) {
            try {
                console.println("Введите координату Y:");
                console.prompt();
                var strY = Interrogator.getUserScanner().nextLine().trim();
                if (fileMode) console.println(strY);

                y = Float.parseFloat(strY);
                if (y <= -420) throw new InvalidRangeException("Значение Y должно быть больше -420");
                break;
            } catch (NoSuchElementException exception) {
                console.printError("Координата Y не распознана!");
                if (fileMode) throw new InvalidScriptInputException();
            } catch (NumberFormatException exception) {
                console.printError("Координата Y должна быть числом!");
                if (fileMode) throw new InvalidScriptInputException();
            } catch (InvalidRangeException exception) {
                console.printError(exception.getMessage());
            } catch (NullPointerException | IllegalStateException exception) {
                console.printError("Непредвиденная ошибка!");
                System.exit(0);
            }
        }
        return y;
    }
}
