package ru.itmo.general.models.forms;

import ru.itmo.general.utility.Interrogator;
import ru.itmo.general.exceptions.InvalidScriptInputException;
import ru.itmo.general.models.Color;
import ru.itmo.general.utility.console.Console;

import java.util.NoSuchElementException;

/**
 * Форма для ввода цвета волос.
 *
 * @author zevtos
 */
public class ColorForm extends Form<Color> {
    private final Console console;

    /**
     * Конструктор формы для ввода цвета волос.
     *
     * @param console консоль для взаимодействия с пользователем
     */
    public ColorForm(Console console) {
        this.console = console;
    }

    @Override
    public Color build() throws InvalidScriptInputException {
        var fileMode = Interrogator.fileMode();

        String strColor;
        Color color;
        while (true) {
            try {
                console.println("Список цветов волос - " + Color.names());
                console.println("Введите цвет волос:");
                console.prompt();

                strColor = Interrogator.getUserScanner().nextLine().trim();
                if (fileMode) console.println(strColor);

                color = Color.valueOf(strColor.toUpperCase());
                break;
            } catch (NoSuchElementException exception) {
                console.printError(getClass(), "Цвет волос не распознан!");
                if (fileMode) throw new InvalidScriptInputException();
            } catch (IllegalArgumentException exception) {
                console.printError(getClass(), "Такого цвета волос нет в списке!");
                if (fileMode) throw new InvalidScriptInputException();
            } catch (IllegalStateException exception) {
                console.printError(getClass(), "Произошла непредвиденная ошибка!");
                System.exit(0);
            }
        }
        return color;
    }
}
