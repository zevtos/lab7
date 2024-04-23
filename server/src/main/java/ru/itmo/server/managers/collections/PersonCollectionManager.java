package ru.itmo.server.managers.collections;


import ru.itmo.general.exceptions.DuplicateException;
import ru.itmo.general.models.Person;
import ru.itmo.server.managers.DumpManager;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Менеджер коллекции объектов типа Person.
 * @author zevtos
 */
public class PersonCollectionManager implements CollectionManager<Person> {
    private final List<Person> collection = new ArrayList<>();
    private LocalDateTime lastSaveTime;
    private final DumpManager<Person> dumpManager;

    public PersonCollectionManager() {
        this.lastSaveTime = null;
        this.dumpManager = new DumpManager<Person>("data/persons.json", Person.class);
        this.loadCollection();
    }
    /**
     * Конструктор для создания экземпляра менеджера коллекции объектов типа Person.
     *
     * @param dumpManager менеджер для записи и чтения коллекции из файла
     */
    public PersonCollectionManager(DumpManager<Person> dumpManager) {
        this.lastSaveTime = null;
        this.dumpManager = dumpManager;
        this.loadCollection();
    }

    public PersonCollectionManager( String arg) {
        this.lastSaveTime = null;
        this.dumpManager = new DumpManager<Person>(arg, Person.class);
    }

    @Override
    public void validateAll() {
        AtomicBoolean flag = new AtomicBoolean(true);
        collection.forEach(person -> {
            if (!person.validate()) {
                //console.printError("Человек с паспортом " + person.getPassportID() + " имеет недопустимые поля.");
                flag.set(false);
            }
        });
        if (flag.get()) {
            //console.println("! Загруженные объекты Person валидны.");
        }
    }

    @Override
    public List<Person> getCollection() {
        return collection;
    }

    @Override
    public Person byId(int id) {
        return collection.stream()
                .filter(person -> Objects.equals(person.getPassportID(), Integer.toString(id)))
                .findFirst()
                .orElse(null);
    }

    /**
     * Получает объект типа Person по его паспортному идентификатору.
     *
     * @param id паспортный идентификатор объекта
     * @return объект типа Person с указанным паспортным идентификатором или null, если объект не найден
     */
    public Person byId(String id) {
        return collection.stream()
                .filter(person -> Objects.equals(person.getPassportID(), id))
                .findFirst()
                .orElse(null);
    }

    @Override
    public boolean contains(Person person) {
        return collection.stream().anyMatch(p -> p.getPassportID().equals(person.getPassportID()));
    }

    /**
     * Проверяет, содержит ли коллекция объект типа Person с указанным паспортным идентификатором.
     *
     * @param id паспортный идентификатор для проверки
     * @return true, если коллекция содержит объект с указанным паспортным идентификатором, иначе false
     */
    public boolean contains(String id) {
        return collection.stream().anyMatch(p -> p.getPassportID().equals(id));
    }

    @Override
    public int getFreeId() {
        return collection.size() + 1;
    }

    @Override
    public boolean add(Person person) {
        if (contains(person)) {
            return false;
        }
        collection.add(person);
        return true;
    }

    @Override
    public boolean update(Person person) {
        if (!contains(person)) {
            return false;
        }
        collection.remove(person);
        collection.add(person);
        return true;
    }

    @Override
    public boolean remove(int id) {
        Person person = byId(id);
        if (person == null) {
            return false;
        }
        collection.remove(person);
        return true;
    }

    @Override
    public boolean remove(Person person) {
        return collection.remove(person);
    }

    /**
     * Фиксирует изменения коллекции
     */
    @Override
    public void update() {
        Collections.sort(collection);
    }

    @Override
    public boolean loadCollection() {
        Collection<Person> loadedPersons = dumpManager.readCollection();
        try {
            for (Person person : loadedPersons) {
                if (person != null) {
                    String passportID = person.getPassportID();
                    if (contains(passportID)) {
                        throw new DuplicateException(passportID);
                    }
                }
                collection.add(person);
            }
            validateAll();
            return true;
        } catch (DuplicateException e) {
            //dumpManager.getConsole().printError("Ошибка загрузки коллекции: обнаружены дубликаты Person по полю passportID: " + e.getDuplicateObject() + '\n' + "Коллекция Person будет инициализирована с помощью Ticket");
            collection.clear();
        }
        return false;
    }

    @Override
    public void saveCollection() {
        dumpManager.writeCollection(collection);
        lastSaveTime = LocalDateTime.now();
    }

    @Override
    public void clearCollection() {
        collection.clear();
    }

    @Override
    public int collectionSize() {
        return collection.size();
    }

    @Override
    public Person getFirst() {
        return collection.isEmpty() ? null : collection.get(0);
    }

    /**
     * Возвращает последнее время сохранения коллекции.
     *
     * @return последнее время сохранения
     */
    public LocalDateTime getLastSaveTime() {
        return lastSaveTime;
    }

    /**
     * Добавляет все объекты из указанной коллекции в текущую коллекцию, исключая дубликаты по паспортным идентификаторам.
     *
     * @param persons коллекция объектов типа Person для добавления
     */
    public void addAll(Collection<Person> persons) {
        for (Person person : persons) {
            if (!contains(person.getPassportID())) {
                collection.add(person);
            }
        }
    }
}