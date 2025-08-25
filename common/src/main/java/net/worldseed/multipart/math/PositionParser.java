package net.worldseed.multipart.math;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import net.worldseed.multipart.math.Point;
import net.worldseed.multipart.math.Vec;
import net.worldseed.multipart.mql.MQLPoint;

import java.lang.reflect.InvocationTargetException;
import java.util.Optional;

public class PositionParser {

    public static Optional<Point> getPos(JsonElement pivot) {
        if (pivot == null) return Optional.empty();
        else {
            JsonArray arr = pivot.getAsJsonArray();
            return Optional.of(new Vec(arr.get(0).getAsDouble(), arr.get(1).getAsDouble(), arr.get(2).getAsDouble()));
        }
    }

    public static Optional<MQLPoint> getMQLPos(JsonElement pivot) throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        if (pivot == null) return Optional.empty();
        else if (pivot instanceof JsonObject obj) {
            return Optional.of(new MQLPoint(obj));
        } else if (pivot instanceof JsonPrimitive num && num.isNumber()) {
            return Optional.of(new MQLPoint(num.getAsDouble(), num.getAsDouble(), num.getAsDouble()));
        } else {
            return Optional.empty();
        }
    }

    public static Optional<MQLPoint> getMQLPos(JsonArray arr) throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        if (arr == null) return Optional.empty();
        else {
            return Optional.of(new MQLPoint(arr));
        }
    }

}
