package repositories;

import com.google.gson.*;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Optional;
/**
 * Source - https://stackoverflow.com/a/77000193
 * Posted by nricciardi, License - CC BY-SA 4.0
 *
 * A custom Gson type adapter for serializing and deserializing Java's {@link Optional} type.
 *
 * <p>This adapter allows Gson to handle fields of type {@code Optional<E>} by serializing the contained value if present,
 * or as JSON null if the optional is empty. During deserialization, it correctly reconstructs the {@code Optional} instance
 * based on the presence of a value in the JSON.</p>
 *
 * @param <E> the type of the value contained in the Optional
 */
public class OptionalTypeAdapter<E> implements JsonSerializer<Optional<E>>, JsonDeserializer<Optional<E>> {

    /**
     *  Serializes an {@code Optional<E>} to JSON.
     *  If the optional contains a value, it is serialized using the provided context.
     *  If the optional is empty, it is serialized as JSON null.
     *
     * @param src the optional object to convert to JSON
     * @param typeOfSrc the actual type of the source object
     * @param context the serialization context used to serialize the contained value
     * @return the JSON representation of the optional value, or {@link JsonNull} if it is empty
     */
    @Override
    public JsonElement serialize(Optional<E> src, Type typeOfSrc, JsonSerializationContext context) {
        if (src.isPresent()) {
            return context.serialize(src.get());
        } else {
            return JsonNull.INSTANCE;
        }
    }

    /**
     * Deserializes JSON into an {@code Optional<E>}.
     *
     * <p>If the JSON element is null, this method returns {@code Optional.empty()}.
     * Otherwise, the contained value is deserialized using the provided context.</p>
     *
     * @param json the JSON data being deserialized
     * @param typeOfT the target optional type
     * @param context the deserialization context used to deserialize the contained value
     * @return an optional containing the deserialized value, or {@code Optional.empty()} if the JSON is null
     * @throws JsonParseException if the JSON value cannot be deserialized
     */
    @Override
    public Optional<E> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
            throws JsonParseException {
        if (json.isJsonNull()) {
            return Optional.empty();
        } else {
            E value = context.deserialize(json, ((ParameterizedType) typeOfT).getActualTypeArguments()[0]);
            return Optional.ofNullable(value);
        }
    }
}

