package ru.itmo.general.managers;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Интерфейс для управления коллекцией объектов.
 *
 * @param <T> тип объектов в коллекции
 * @author zevtos
 */
public interface CollectionManager<T> {
    /**
     * Проверяет валидность всех объектов в коллекции.
     */
    void validateAll();

    /**
     * Получает коллекцию объектов.
     *
     * @return коллекция объектов
     */
    List<T> getCollection();

    /**
     * Получает объект по его идентификатору.
     *
     * @param id идентификатор объекта
     * @return объект с указанным идентификатором или null, если такой объект не найден
     */
    T byId(int id);

    /**
     * Проверяет, содержит ли коллекция указанный объект.
     *
     * @param item объект для проверки
     * @return true, если коллекция содержит указанный объект, иначе false
     */
    boolean contains(T item);

    /**
     * Получает свободный идентификатор для нового объекта.
     *
     * @return свободный идентификатор
     */
    int getFreeId();

    /**
     * Добавляет объект в коллекцию.
     *
     * @param item объект для добавления
     * @return true, если объект успешно добавлен, иначе false
     */
    boolean add(T item, int userID);

    /**
     * Обновляет информацию об объекте в коллекции.
     *
     * @param item объект для обновления
     * @return true, если объект успешно обновлен, иначе false
     */
    boolean update(T item);

    /**
     * Удаляет объект из коллекции по его идентификатору.
     *
     * @param integer идентификатор объекта для удаления
     * @return true, если объект успешно удален, иначе false
     */
    boolean remove(Integer integer);

    /**
     * Удаляет указанный объект из коллекции.
     *
     * @param item объект для удаления
     * @return true, если объект успешно удален, иначе false
     */
    boolean remove(T item);

    /**
     * Обновляет состояние коллекции.
     */
    void update();

    /**
     * Загружает коллекцию из файла.
     *
     * @return true, если коллекция успешно загружена, иначе false
     */
    boolean loadCollection();


    /**
     * Очищает коллекцию.
     */
    void clearCollection();

    /**
     * Возвращает размер коллекции.
     *
     * @return размер коллекции
     */
    int collectionSize();

    /**
     * Получает первый объект в коллекции.
     *
     * @return первый объект в коллекции или null, если коллекция пуста
     */
    T getFirst();

    LocalDateTime getLastSaveTime();

    String collectionType();

    T getLast();
}
