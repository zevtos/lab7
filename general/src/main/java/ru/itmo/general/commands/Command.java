package ru.itmo.general.commands;

import lombok.Getter;
import lombok.Setter;

import java.util.Objects;

/**
 * Абстрактная команда с именем и описанием.
 *
 * @author zevtos
 */
public abstract class Command implements Describable, Executable {
    /**
     * -- GETTER --
     *  Получить название команды.
     *
     */
    @Getter
    private final String name;
    /**
     * -- GETTER --
     *  Получить описание команды.
     *
     */
    @Getter
    private final String description;

    /**
     * Конструктор для создания команды с именем и описанием.
     *
     * @param name        Название команды.
     * @param description Описание команды.
     */
    public Command(CommandName name, String description) {
        this.name = name.toString().toLowerCase();
        this.description = description;
    }

    public String getUsingError(){
        return "Неправильное количество аргументов!" + '\n' + "Использование: '" + getName() + getDescription() + "'";
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Command command = (Command) o;
        return Objects.equals(name, command.name) && Objects.equals(description, command.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, description);
    }

    @Override
    public String toString() {
        return "Command{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}
