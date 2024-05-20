package ru.itmo.general.models;

import lombok.Getter;
import lombok.Setter;
import ru.itmo.general.utility.base.Element;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

/**
 * The {@code Ticket} class represents a ticket object.
 * It encapsulates information about the ticket, including its name, coordinates, creation date, price, discount,
 * comment, ticket type, and associated person.
 *
 * @author zevtos
 */
@Getter
@Setter
public class Ticket extends Element {
    /**
     * The unique identifier of the ticket.
     */
    private Integer id;

    /**
     * The name of the ticket.
     */
    private String name;

    /**
     * The coordinates of the ticket.
     */
    private Coordinates coordinates;

    /**
     * The creation date of the ticket.
     */
    private ZonedDateTime creationDate;

    /**
     * The price of the ticket.
     */
    private double price;

    /**
     * The discount percentage applied to the ticket, if any.
     */
    private Long discount;

    /**
     * Any additional comment associated with the ticket.
     */
    private String comment;

    /**
     * The type of the ticket.
     */
    private TicketType type;

    /**
     * The person associated with the ticket.
     */
    private Person person;

    public Ticket() {
        this.id = -1; // или другой способ генерации ID
        this.name = "";
        this.coordinates = new Coordinates(0, 0f);
        this.creationDate = ZonedDateTime.now();
        this.price = 1.0; // Минимальная валидная цена
        this.discount = 1L; // Минимальная валидная скидка
        this.comment = "";
        this.type = TicketType.USUAL; // или другой тип по умолчанию
        this.person = new Person(LocalDateTime.now(), 1.0f, "", Color.BLACK); // или другие значения по умолчанию
    }


    /**
     * Constructs a ticket object with the specified parameters.
     *
     * @param nextId       the next available ID for the ticket
     * @param name         the name of the ticket
     * @param coordinates  the coordinates of the ticket
     * @param creationDate the creation date of the ticket
     * @param price        the price of the ticket
     * @param discount     the discount percentage applied to the ticket
     * @param comment      any additional comment associated with the ticket
     * @param type         the type of the ticket
     * @param person       the person associated with the ticket
     */
    public Ticket(Integer nextId, String name, Coordinates coordinates, ZonedDateTime creationDate, double price, Long discount, String comment, TicketType type, Person person) {
        this.id = nextId;
        this.name = name;
        this.coordinates = coordinates;
        this.creationDate = creationDate;
        this.price = price;
        this.discount = discount;
        this.comment = comment;
        this.type = type;
        this.person = person;
    }


    /**
     * Constructs a ticket object with the specified parameters, using the current date and time as the creation date.
     *
     * @param nextId      the next available ID for the ticket
     * @param name        the name of the ticket
     * @param coordinates the coordinates of the ticket
     * @param price       the price of the ticket
     * @param discount    the discount percentage applied to the ticket
     * @param comment     any additional comment associated with the ticket
     * @param type        the type of the ticket
     * @param person      the person associated with the ticket
     */
    public Ticket(Integer nextId, String name, Coordinates coordinates, double price, Long discount, String comment, TicketType type, Person person) {
        this(nextId, name, coordinates, ZonedDateTime.now(), price, discount, comment, type, person);
    }

    /**
     * Validates whether the ticket is valid.
     *
     * @return {@code true} if the ticket is valid, {@code false} otherwise
     */
    public boolean validate() {
        if (name == null || name.isEmpty()) return false;
        if (coordinates == null || !coordinates.validate()) return false;
        if (creationDate == null) return false;
        if (price <= 0) return false;
        if (discount != null && (discount <= 0 || discount > 100)) return false;
        return person != null && person.validate();
    }

    @Override
    public int compareTo(Element element) {
        return CharSequence.compare(this.getName(), element.getName());
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Ticket that = (Ticket) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, creationDate, coordinates, price, discount, comment, type, person);
    }

    /**
     * Updates the ticket with the information from the specified ticket object.
     *
     * @param ticket the ticket object containing updated information
     */
    public void update(Ticket ticket) {
        this.name = ticket.name;
        this.coordinates = ticket.coordinates;
        this.creationDate = ticket.creationDate;
        this.price = ticket.price;
        this.discount = ticket.discount;
        this.comment = ticket.comment;
        this.type = ticket.type;
        this.person = ticket.person;
    }

    @Override
    public String toString() {
        return "Ticket{" +
                "\n\tid=" + id +
                "\n\tname='" + name + '\'' +
                "\n\tcoordinates=" + coordinates +
                "\n\tcreationDate='" + creationDate.format(DateTimeFormatter.ISO_DATE_TIME) + '\'' +
                "\n\tprice=" + price +
                "\n\tdiscount=" + (discount == null ? "null" : discount) +
                "\n\tcomment='" + (comment == null ? "null" : comment) + '\'' +
                "\n\tticketType='" + (type == null ? "null" : type) + '\'' +
                "\n\t" + (person == null ? "null" : person.toString()) +
                "\n}";
    }
}
