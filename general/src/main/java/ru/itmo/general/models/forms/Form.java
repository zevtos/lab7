package ru.itmo.general.models.forms;

import ru.itmo.general.exceptions.InvalidFormException;
import ru.itmo.general.exceptions.InvalidScriptInputException;

/**
 * Абстрактный класс формы для ввода пользовательских данных.
 *
 * @param <T> тип создаваемого объекта
 * @author zevtos
 */
public abstract class Form<T> {
    /**
     * Метод для построения объекта на основе введенных пользовательских данных.
     *
     * @return созданный объект
     * @throws InvalidScriptInputException если введены некорректные данные в скрипте
     * @throws InvalidFormException        если введены некорректные данные вручную
     */
    public abstract T build() throws InvalidScriptInputException, InvalidFormException;
}
