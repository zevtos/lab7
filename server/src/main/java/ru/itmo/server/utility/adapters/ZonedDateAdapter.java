package ru.itmo.server.utility.adapters;

import com.google.gson.*;

import java.lang.reflect.Type;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Адаптер для сериализации и десериализации объектов ZonedDateTime в формате JSON.
 * @author zevtos
 */
public class ZonedDateAdapter implements JsonSerializer<ZonedDateTime>, JsonDeserializer<ZonedDateTime> {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ISO_ZONED_DATE_TIME;

    /**
     * Сериализует объект ZonedDateTime в формат JSON.
     * @param date Объект ZonedDateTime для сериализации.
     * @param typeOfSrc Тип объекта для сериализации.
     * @param context Контекст сериализации JSON.
     * @return JsonElement, представляющий сериализованный объект ZonedDateTime.
     */
    @Override
    public JsonElement serialize(ZonedDateTime date, Type typeOfSrc, JsonSerializationContext context) {
        return new JsonPrimitive(date.format(FORMATTER));
    }

    /**
     * Десериализует объект ZonedDateTime из формата JSON.
     * @param json Элемент JSON для десериализации.
     * @param type Тип объекта для десериализации.
     * @param context Контекст десериализации JSON.
     * @return Десериализованный объект ZonedDateTime.
     * @throws JsonParseException Если не удалось десериализовать объект.
     */
    @Override
    public ZonedDateTime deserialize(JsonElement json, Type type, JsonDeserializationContext context) throws JsonParseException {
        return ZonedDateTime.parse(json.getAsString(), FORMATTER);
    }
}
