package ru.itmo.server.utility.adapters;

import com.google.gson.*;

import java.lang.reflect.Type;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Адаптер для сериализации и десериализации объектов LocalDateTime в формате JSON.
 * @author zevtos
 */
public class LocalDateTimeAdapter implements JsonSerializer<LocalDateTime>, JsonDeserializer<LocalDateTime> {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    /**
     * Сериализует объект LocalDateTime в формат JSON.
     * @param date Объект LocalDateTime для сериализации.
     * @param typeOfSrc Тип объекта для сериализации.
     * @param context Контекст сериализации JSON.
     * @return JsonElement, представляющий сериализованный объект LocalDateTime.
     */
    @Override
    public JsonElement serialize(LocalDateTime date, Type typeOfSrc, JsonSerializationContext context) {
        return new JsonPrimitive(date.format(FORMATTER));
    }

    /**
     * Десериализует объект LocalDateTime из формата JSON.
     * @param json Элемент JSON для десериализации.
     * @param type Тип объекта для десериализации.
     * @param context Контекст десериализации JSON.
     * @return Десериализованный объект LocalDateTime.
     * @throws JsonParseException Если не удалось десериализовать объект.
     */
    @Override
    public LocalDateTime deserialize(JsonElement json, Type type, JsonDeserializationContext context) throws JsonParseException {
        LocalDate localDate = LocalDate.parse(json.getAsString(), FORMATTER);
        return localDate.atStartOfDay();
    }
}
