package ru.itmo.server.managers.collections;

import ru.itmo.general.exceptions.DuplicateException;
import ru.itmo.general.models.Person;
import ru.itmo.general.models.Ticket;
import ru.itmo.server.managers.DumpManager;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Управляет коллекцией билетов.
 *
 * @author zevtos
 */
public class TicketCollectionManager implements CollectionManager<Ticket> {
    private int currentId = 1;
    private final LinkedList<Ticket> collection = new LinkedList<>();
    private LocalDateTime lastSaveTime;
    private final DumpManager<Ticket> dumpManager;
    private final PersonCollectionManager personCollectionManager;

    /**
     * Создает менеджер коллекции билетов.
     *
     * @param dumpManager             менеджер для записи и чтения данных из файла
     * @param personCollectionManager менеджер коллекции персон
     */
    public TicketCollectionManager(DumpManager<Ticket> dumpManager, PersonCollectionManager personCollectionManager) {
        this.lastSaveTime = null;
        this.dumpManager = dumpManager;

        this.loadCollection();
        if (personCollectionManager == null) {
            var personDumpManager = new DumpManager<Person>("data/persons.json", Person.class);
            personCollectionManager = new PersonCollectionManager(personDumpManager);
            personCollectionManager.loadCollection();
            personCollectionManager.addAll(this.getAllPersons());
        }
        this.personCollectionManager = personCollectionManager;
    }

    /**
     * Создает менеджер коллекции билетов.
     *
     * @param args    аргументы командной строки
     */
    public TicketCollectionManager(String[] args) {
        this.lastSaveTime = null;
        this.dumpManager = new DumpManager<Ticket>(args[0], Ticket.class);
        this.loadCollection();
        if (args.length == 2) {
            this.personCollectionManager = new PersonCollectionManager(args[1]);
        } else {
            this.personCollectionManager = new PersonCollectionManager();
        }
        this.personCollectionManager.addAll(this.getAllPersons());
    }

    public void validateAll() {
        AtomicBoolean flag = new AtomicBoolean(true);
        collection.forEach(ticket -> {
            if (!ticket.validate()) {
                //console.printError("Билет с id=" + ticket.getId() + " имеет недопустимые поля.");
                flag.set(false);
            }
        });
        if (flag.get()) {
            //console.println("! Загруженные билеты валидны.");
        }
    }

    /**
     * @return Последнее время сохранения.
     */
    public LocalDateTime getLastSaveTime() {
        return lastSaveTime;
    }

    /**
     * @return коллекция.
     */
    public LinkedList<Ticket> getCollection() {
        return collection;
    }

    /**
     * Получить Ticket по ID
     */
    @Override
    public Ticket byId(int id) {
        if (collection.isEmpty()) return null;
        return collection.stream()
                .filter(ticket -> ticket.getId() == id)
                .findFirst()
                .orElse(null);
    }

    /**
     * Содержит ли коллекции Ticket
     */
    public boolean contains(Ticket ticket) {
        for (Ticket t : collection) {
            if (t.getId() == ticket.getId()) {
                return true;
            }
        }
        return false;
    }

    /**
     * Получить свободный ID
     */
    @Override
    public int getFreeId() {
        while (byId(currentId) != null) {
            currentId++;
        }
        return currentId;
    }

    public String collectionType() {
        return collection.getClass().getName();
    }

    /**
     * Добавляет Ticket
     */
    @Override
    public boolean add(Ticket ticket) {
        if (contains(ticket)) {
            return false;
        }
        collection.add(ticket);
        update();
        return true;
    }

    public void addAll(Collection<Ticket> tickets) {
        for (Ticket ticket : tickets) {
            if (!contains(ticket)) {
                collection.add(ticket);
            }
        }
    }


    /**
     * Обновляет Ticket
     */
    @Override
    public boolean update(Ticket ticket) {
        if (!contains(ticket)) {
            return false;
        }
        collection.remove(ticket);
        collection.add(ticket);
        update();
        return true;
    }

    /**
     * Удаляет Ticket по ID
     */
    @Override
    public boolean remove(int id) {
        Ticket ticket = byId(id);
        if (ticket == null) {
            return false;
        }
        collection.remove(ticket);
        update();
        return true;
    }

    @Override
    public boolean remove(Ticket ticket) {
        return collection.remove(ticket);
    }


    /**
     * Фиксирует изменения коллекции
     */
    public void update() {
        Collections.sort(collection);
    }

    /**
     * Сохраняет коллекцию в файл
     */
    public void saveCollection() {
        dumpManager.writeCollection(collection);
        lastSaveTime = LocalDateTime.now();
    }

    public void clearCollection() {
        collection.clear();
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
        Collection<Ticket> loadedTickets = dumpManager.readCollection();
        try {
            for (Ticket ticket : loadedTickets) {
                if (ticket != null) {
                    int id = ticket.getId();
                    Ticket existingTicket = byId(id);
                    if (existingTicket != null) {
                        throw new DuplicateException();
                    }
                }
                collection.add(ticket);
            }
            validateAll();
            return true;
        } catch (DuplicateException e) {
            //dumpManager.getConsole().printError("Ошибка загрузки коллекции: обнаружены дубликаты Ticket по полю id, загружены только первые значения.");
        }
        return false;
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

    public PersonCollectionManager getPersonManager() {
        return personCollectionManager;
    }
}