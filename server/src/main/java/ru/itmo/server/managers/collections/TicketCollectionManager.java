package ru.itmo.server.managers.collections;

import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.itmo.general.exceptions.DuplicateException;
import ru.itmo.general.models.Person;
import ru.itmo.general.models.Ticket;
import ru.itmo.general.managers.CollectionManager;
import ru.itmo.server.dao.TicketDAO;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Управляет коллекцией билетов.
 *
 * @author zevtos
 */
public class TicketCollectionManager implements CollectionManager<Ticket> {
    private final Logger logger = LoggerFactory.getLogger("TicketCollectionManager");
    private int currentId = 1;
    @Getter
    private final LinkedList<Ticket> collection = new LinkedList<>();
    @Getter
    private LocalDateTime lastSaveTime;
    private final ReentrantLock lock = new ReentrantLock(); // Замок для синхронизации доступа
    private final TicketDAO dao;

    /**
     * Создает менеджер коллекции билетов.
     *
     */
    public TicketCollectionManager() {
        this.lastSaveTime = null;
        this.dao = new TicketDAO();
        this.loadCollection();
        update();
    }

    public TicketCollectionManager(TicketDAO ticketDAO) {
        this.lastSaveTime = null;
        this.dao = ticketDAO;
        this.loadCollection();
        update();
    }

    /**
     * Создает менеджер коллекции билетов.
     *
     * @param args аргументы командной строки
     */
    public TicketCollectionManager(String[] args) {
        this.lastSaveTime = null;
        this.dao = new TicketDAO();
        this.loadCollection();
        update();
    }

    /**
     * Получить Ticket по ID
     */
    @Override
    public Ticket byId(int id) {
        try {
            lock.lock();
            if (collection.isEmpty()) return null;
            return collection.stream()
                    .filter(ticket -> ticket.getId() == id)
                    .findFirst()
                    .orElse(null);
        } finally {
            lock.unlock();
        }
    }

    /**
     * Добавляет Ticket
     */
    @Override
    public boolean add(Ticket ticket) {
        try {
            lock.lock();
            if (contains(ticket)) {
                return false;
            }
            TicketDAO ticketDAO = new TicketDAO();
            if (!ticketDAO.addTicket(ticket, 1)) return false;
            collection.add(ticket);
            update();
            return true;
        } finally {
            lock.unlock();
        }
    }

    /**
     * Обновляет Ticket
     */
    @Override
    public boolean update(Ticket ticket) {
        try {
            lock.lock();
            if (!contains(ticket)) {
                return false;
            }
            collection.remove(ticket);
            collection.add(ticket);
            update();
            return true;
        } finally {
            lock.unlock();
        }
    }

    /**
     * Удаляет Ticket по ID
     */
    @Override
    public boolean remove(int id) {
        try {
            lock.lock();
            Ticket ticket = byId(id);
            if (ticket == null) {
                return false;
            }
            collection.remove(ticket);
            update();
            return true;
        } finally {
            lock.unlock();
        }
    }

    /**
     * Фиксирует изменения коллекции
     */
    public void update() {
        Collections.sort(collection);
    }

    /**
     * Содержит ли коллекции Ticket
     */
    public boolean contains(Ticket ticket) {
        try {
            lock.lock();
            for (Ticket t : collection) {
                if (t.getId() == ticket.getId()) {
                    return true;
                }
            }
            return false;
        } finally {
            lock.unlock();
        }
    }

    /**
     * Получить свободный ID
     */
    @Override
    public synchronized int getFreeId() {
        while (byId(currentId) != null) {
            currentId++;
        }
        return currentId;
    }

    public String collectionType() {
        return collection.getClass().getName();
    }


    @Override
    public boolean remove(Ticket ticket) {
        try {
            lock.lock();
            return collection.remove(ticket);
        } finally {
            lock.unlock();
        }
    }


    public void clearCollection() {
        try {
            lock.lock();
            collection.clear();
        } finally {
            lock.unlock();
        }
    }

    public int collectionSize() {
        return collection.size();
    }

    @Override
    public String toString() {
        if (collection.isEmpty()) return "Коллекция пуста!";

        StringBuilder info = new StringBuilder();
        for (var Ticket : collection) {
            info.append(Ticket).append("\n\n");
        }
        return info.toString().trim();
    }

    @Override
    public boolean loadCollection() {
        try {
            lock.lock();
            Collection<Ticket> loadedTickets = dao.getAllTickets();
            if (loadedTickets.isEmpty()) {
                collection.clear();
            } else {
                boolean success = collection.addAll(loadedTickets);
                if (success) {
                    logger.info("Tickets added successfully.");
                }
            }
            validateAll();
            return true;
        } finally {
            lock.unlock();
        }
    }


    public Ticket getFirst() {
        if (collection.isEmpty()) return null;
        return collection.peek();
    }

    public Collection<Person> getAllPersons() {
        // Получаем коллекцию всех билетов
        Collection<Ticket> tickets = getCollection();
        // Создаем новую коллекцию для хранения всех персон
        List<Person> allPersons = new ArrayList<>();

        // Проходим по каждому билету и добавляем его персону в список всех персон
        for (Ticket ticket : tickets) {
            if (ticket == null) {
                continue;
            }
            Person person = ticket.getPerson();
            if (person != null) {
                allPersons.add(person);
            }
        }

        return allPersons;
    }

    public void validateAll() {
        AtomicBoolean flag = new AtomicBoolean(true);
        collection.forEach(ticket -> {
            if (!ticket.validate()) {
                logger.error("Билет с id={} имеет недопустимые поля.", ticket.getId());
                flag.set(false);
            }
        });
        if (flag.get()) {
            logger.info("! Загруженные билеты валидны.");
        }
    }
}