package ru.itmo.general.utility.console;

/**
 * Интерфейс для консоли, обеспечивающий ввод команд и вывод результатов.
 *
 * @author zevtos
 */
public interface Console {
    /**
     * Выводит объект в консоль.
     *
     * @param obj Объект для печати.
     */
    void print(Object obj);

    /**
     * Выводит объект в консоль с переводом строки.
     *
     * @param obj Объект для печати.
     */
    void println(Object obj);

    /**
     * Выводит ошибку в консоль.
     *
     * @param obj Ошибка для печати.
     */
    void printError(Class<?> callingClass, Object obj);

    /**
     * Выводит два элемента в формате таблицы.
     *
     * @param obj1 Первый элемент.
     * @param obj2 Второй элемент.
     */
    void printTable(Object obj1, Object obj2);

    /**
     * Выводит приглашение для ввода команды.
     */
    void prompt();

    /**
     * Возвращает приглашение для ввода команды.
     *
     * @return Приглашение для ввода команды.
     */
    String getPrompt();


    void println();

    char[] readPassword(String s);
}
