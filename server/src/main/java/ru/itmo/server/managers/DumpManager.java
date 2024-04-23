package ru.itmo.server.managers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;
import ru.itmo.server.utility.adapters.LocalDateTimeAdapter;
import ru.itmo.server.utility.adapters.ZonedDateAdapter;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.Collection;
import java.util.LinkedList;
import java.util.NoSuchElementException;

/**
 * Менеджер для работы с файлом, в который происходит сохранение и извлечение коллекции.
 *
 * @param <T> Тип элементов коллекции.
 * @author zevtos
 */
public class DumpManager<T> {
    private final java.lang.reflect.Type collectionType;
    private final String collectionName;
    private final Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .serializeNulls()
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .registerTypeAdapter(ZonedDateTime.class, new ZonedDateAdapter())
            .create();

    private final String fileName;

    /**
     * Конструктор для создания экземпляра менеджера.
     *
     * @param fileName Имя файла.
     * @param clazz    Класс элементов коллекции.
     */
    public DumpManager(String fileName, Class<T> clazz) {
        this.fileName = fileName;
        collectionType = TypeToken.getParameterized(LinkedList.class, clazz).getType();
        String[] parts = clazz.getName().split("\\.");
        collectionName = parts[parts.length - 1];
    }


    /**
     * Записывает коллекцию в файл.
     *
     * @param collection Коллекция.
     */
    public void writeCollection(Collection<T> collection) {
        try (OutputStreamWriter collectionPrintWriter = new OutputStreamWriter(new FileOutputStream(fileName), StandardCharsets.UTF_8)) {
            collectionPrintWriter.write(gson.toJson(collection));
            //console.println("Коллекция " + collectionName + " сохранена в файл!");
        } catch (IOException exception) {
            //console.printError("Загрузочный файл не может быть открыт!");
        }
        //TODO: сделать передачу messages и удалить console из Dump
    }

    /**
     * Считывает коллекцию из файла.
     *
     * @return Считанная коллекция.
     */
    public Collection<T> readCollection() {
        if (fileName != null && !fileName.isEmpty()) {
            try (InputStreamReader fileReader = new InputStreamReader(new FileInputStream(fileName), StandardCharsets.UTF_8);
                 BufferedReader reader = new BufferedReader(fileReader)) {

                var jsonString = new StringBuilder();

                String line;
                while ((line = reader.readLine()) != null) {
                    line = line.trim();
                    if (!line.isEmpty()) {
                        jsonString.append(line);
                    }
                }

                if (jsonString.isEmpty()) {
                    jsonString = new StringBuilder("[]");
                }

                LinkedList<T> collection = gson.fromJson(jsonString.toString(),
                        collectionType);

                //console.println("Коллекция " + collectionName + " успешно загружена!");
                return collection;

            } catch (FileNotFoundException exception) {
                //console.printError("Загрузочный файл не найден!");
            } catch (NoSuchElementException exception) {
                //console.printError("Загрузочный файл пуст!");
            } catch (JsonParseException exception) {
                //console.printError("В загрузочном файле не обнаружена необходимая коллекция!");
            } catch (IllegalStateException | IOException exception) {
                //console.printError("Непредвиденная ошибка!");
                System.exit(0);
            }
        } else {
            //console.printError("Аргумент командной строки с загрузочным файлом не найден!");
        }
        return new LinkedList<>();
    }

    /**
     * Очищает содержимое файла.
     */
    public void clearFile() {
        try {
            FileWriter writer = new FileWriter(fileName);
            writer.write("");
            writer.close();
            //console.println("Файл " + fileName + " успешно очищен!");
        } catch (IOException exception) {
            //console.printError("Ошибка при очистке файла " + fileName + ": " + exception.getMessage());
        }
    }
}
