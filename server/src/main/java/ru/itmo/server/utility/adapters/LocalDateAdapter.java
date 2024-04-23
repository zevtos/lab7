package ru.itmo.server.utility.adapters;

import com.google.gson.*;

import java.lang.reflect.Type;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Адаптер для сериализации и десериализации объектов LocalDate в формате JSON.
 * @author zevtos
 */
public class LocalDateAdapter implements JsonSerializer<LocalDate>, JsonDeserializer<LocalDate> {
    /**
     * Сериализует объект LocalDate в формат JSON.
     * @param date Объект LocalDate для сериализации.
     * @param typeOfSrc Тип объекта для сериализации.
     * @param context Контекст сериализации JSON.
     * @return JsonElement, представляющий сериализованный объект LocalDate.
     */
    @Override
    public JsonElement serialize(LocalDate date, Type typeOfSrc, JsonSerializationContext context) {
        return new JsonPrimitive(date.format(DateTimeFormatter.ISO_LOCAL_DATE));
    }

    /**
     * Десериализует объект LocalDate из формата JSON.
     * @param json Элемент JSON для десериализации.
     * @param type Тип объекта для десериализации.
     * @param context Контекст десериализации JSON.
     * @return Десериализованный объект LocalDate.
     * @throws JsonParseException Если не удалось десериализовать объект.
     */
    @Override
    public LocalDate deserialize(JsonElement json, Type type, JsonDeserializationContext context) throws JsonParseException {
        return LocalDate.parse(json.getAsJsonPrimitive().getAsString());
    }
}
