package ru.itmo.general.models.forms;

import ru.itmo.general.utility.Interrogator;
import ru.itmo.general.exceptions.InvalidScriptInputException;
import ru.itmo.general.models.TicketType;
import ru.itmo.general.utility.console.Console;

import java.util.NoSuchElementException;

/**
 * Form for inputting the ticket type.
 * Handles user input to determine the type of ticket.
 *
 * @author zevtos
 */
public class TicketTypeForm extends Form<TicketType> {
    private final Console console;

    /**
     * Constructs a new form object for inputting the ticket type.
     *
     * @param console The console for interacting with the user.
     */
    public TicketTypeForm(Console console) {
        this.console = console;
    }

    /**
     * Builds a ticket type object based on the entered data.
     *
     * @return The entered ticket type, or null if no input was provided.
     * @throws InvalidScriptInputException If an error occurs while executing the script.
     */
    @Override
    public TicketType build() throws InvalidScriptInputException {
        var fileMode = Interrogator.fileMode();

        String strTicketType;
        TicketType ticketType;
        while (true) {
            try {
                console.println("List of ticket types - " + TicketType.names());
                console.println("Enter the ticket type (or 'null' to cancel):");
                console.prompt();

                strTicketType = Interrogator.getUserScanner().nextLine().trim();
                if (fileMode) console.println(strTicketType);

                if (strTicketType.isEmpty() || strTicketType.equals("null")) return null;
                ticketType = TicketType.valueOf(strTicketType.toUpperCase());
                break;
            } catch (NoSuchElementException exception) {
                console.printError(getClass(), "Ticket type not recognized!");
                if (fileMode) throw new InvalidScriptInputException();
            } catch (IllegalArgumentException exception) {
                console.printError(getClass(), "Ticket type is not in the list!");
                if (fileMode) throw new InvalidScriptInputException();
            } catch (IllegalStateException exception) {
                console.printError(getClass(), "An unexpected error occurred!");
                System.exit(0);
            }
        }
        return ticketType;
    }
}
