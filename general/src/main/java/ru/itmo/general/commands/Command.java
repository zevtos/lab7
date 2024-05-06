package ru.itmo.general.commands;

import lombok.Getter;

import java.util.Objects;

/**
 * Abstract command with a name and description.
 */
@Getter
public abstract class Command implements Describable, Executable {
    private final String name;
    private final String description;

    /**
     * Constructor for creating a command with a name and description.
     *
     * @param name        The name of the command.
     * @param description The description of the command.
     */
    public Command(CommandName name, String description) {
        this.name = name.toString().toLowerCase();
        this.description = description;
    }

    /**
     * Get the error message for using the command with the wrong number of arguments.
     *
     * @return The error message for using the command with the wrong number of arguments.
     */
    public String getUsingError() {
        return "Incorrect number of arguments!\nUsage: '" + getName() + getDescription() + "'";
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

