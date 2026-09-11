package util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializer;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * GsonUtil - provides a single, shared Gson instance configured with
 * custom adapters for java.time.LocalDate and java.time.LocalTime.
 *
 * WHY THIS IS NEEDED:
 * Gson's default reflective serialization tries to reach into private
 * fields of LocalDate/LocalTime (e.g. LocalDate#year). Since Java 9+,
 * the module system blocks reflective access into java.time's internals
 * unless the module is explicitly opened, which causes:
 *   InaccessibleObjectException: Unable to make field private final
 *   int java.time.LocalDate.year accessible...
 *
 * The fix is to tell Gson exactly how to convert these types to/from
 * JSON ourselves (as plain ISO-8601 strings), bypassing reflection
 * entirely for these two types.
 *
 * All servlets should use GsonUtil.getGson() instead of `new Gson()`
 * directly, so this configuration is applied consistently everywhere.
 */
public class GsonUtil {

    private static final Gson gson = buildGson();

    private static Gson buildGson() {
        JsonSerializer<LocalDate> dateSerializer =
                (date, type, ctx) -> new JsonPrimitive(date.toString()); // e.g. "2026-09-15"

        JsonDeserializer<LocalDate> dateDeserializer =
                (json, type, ctx) -> LocalDate.parse(json.getAsString());

        JsonSerializer<LocalTime> timeSerializer =
                (time, type, ctx) -> new JsonPrimitive(time.toString()); // e.g. "14:30"

        JsonDeserializer<LocalTime> timeDeserializer =
                (json, type, ctx) -> LocalTime.parse(json.getAsString());

        return new GsonBuilder()
                .registerTypeAdapter(LocalDate.class, dateSerializer)
                .registerTypeAdapter(LocalDate.class, dateDeserializer)
                .registerTypeAdapter(LocalTime.class, timeSerializer)
                .registerTypeAdapter(LocalTime.class, timeDeserializer)
                .create();
    }

    public static Gson getGson() {
        return gson;
    }
}