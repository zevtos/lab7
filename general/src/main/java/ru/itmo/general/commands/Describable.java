package ru.itmo.general.commands;

/**
 * Интерфейс для объектов, которые можно назвать и описать.
 *
 * @author zevtos
 */
public interface Describable {
    /**
     * Получить имя.
     *
     * @return Имя объекта.
     */
    String getName();

    /**
     * Получить описание.
     *
     * @return Описание объекта.
     */
    String getDescription();
}
