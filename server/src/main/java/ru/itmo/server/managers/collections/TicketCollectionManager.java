package ru.itmo.server.managers.collections;

import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.itmo.general.managers.CollectionManager;
import ru.itmo.general.models.Ticket;
import ru.itmo.server.dao.TicketDAO;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Управляет коллекцией билетов.
 *
 * @author zevtos
 */
public class TicketCollectionManager implements CollectionManager<Ticket> {
    private final Logger logger = LoggerFactory.getLogger("TicketCollectionManager");
    @Getter
    private final LinkedList<Ticket> collection = new LinkedList<>();
    private final ReentrantLock lock = new ReentrantLock(true); // Замок для синхронизации доступа
    private final TicketDAO dao;
    @Getter
    private LocalDateTime lastSaveTime;

    /**
     * Создает менеджер коллекции билетов.
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
    public Integer add(Ticket ticket, int userID) {
        try {
            lock.lock();
            int newID = dao.addTicket(ticket, userID);
            if (newID < 0) return -1;
            ticket.setId(newID);
            collection.add(ticket);
            update();
            return newID;
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
            if (!dao.removeTicketById(ticket.getId())) return false;
            collection.remove(ticket);
            if (!dao.updateTicket(ticket)) return false;
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
    public boolean remove(Integer id) {
        try {
            lock.lock();
            Ticket ticket = byId(id);
            if (ticket == null) {
                return false;
            }
            if (!dao.removeTicketById(ticket.getId())) return false;
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

    public String collectionType() {
        return collection.getClass().getName();
    }

    @Override
    public Ticket getLast() {
        return collection.getLast();
    }


    @Override
    public boolean remove(Ticket ticket) {
        try {
            lock.lock();
            if (!dao.removeTicketById(ticket.getId())) return false;
            collection.remove(ticket);
            return true;
        } finally {
            lock.unlock();
        }
    }

    @Override
    public boolean clear(int userId) {
        try {
            lock.lock();
            boolean result = dao.removeTicketsByUserId(userId);
            if (result) {
                collection.removeIf(ticket -> ticket.getUserId() == userId);
            }
            return result;
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
        return collection.getFirst();
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