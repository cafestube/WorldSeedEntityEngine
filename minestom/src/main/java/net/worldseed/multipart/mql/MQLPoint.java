package net.worldseed.multipart.mql;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.hollowcube.molang.MolangExpr;
import net.hollowcube.molang.MolangOptimizer;
import net.hollowcube.molang.eval.MolangEvaluator;
import net.minestom.server.coordinate.Point;
import net.minestom.server.coordinate.Vec;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;

public class MQLPoint {
    public static final MQLPoint ZERO = new MQLPoint();
    MolangExpr molangX = null;
    MolangExpr molangY = null;
    MolangExpr molangZ = null;
    double x = 0;
    double y = 0;
    double z = 0;

    private final MQLData data = new MQLData();
    private final MolangEvaluator evaluator = new MolangEvaluator(Map.of(
            "q", this.data,
            "query", this.data
    ));

    public MQLPoint() {
        this(0, 0, 0);
    }

    public MQLPoint(double x_, double y_, double z_) {
        x = x_;
        y = y_;
        z = z_;
    }

    public MQLPoint(JsonObject json) throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        JsonElement fx = json.get("x");
        if (fx != null) {
            try {
                x = fx.getAsDouble();
            } catch (Exception ignored) {
                molangX = fromString(fx.getAsString());
            }
        }

        JsonElement fy = json.get("y");
        if (fy != null) {
            try {
                y = fy.getAsDouble();
            } catch (Exception ignored) {
                molangY = fromString(fy.getAsString());
            }
        }

        JsonElement fz = json.get("z");
        if (fz != null) {
            try {
                z = fz.getAsDouble();
            } catch (Exception ignored) {
                molangZ = fromString(fz.getAsString());
            }
        }
    }

    public MQLPoint(JsonArray arr) throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        JsonElement fx = arr.get(0);
        if (fx != null) {
            try {
                x = fx.getAsDouble();
            } catch (Exception ignored) {
                molangX = fromString(fx.getAsString());
            }
        }

        JsonElement fy = arr.get(1);
        if (fy != null) {
            try {
                y = fy.getAsDouble();
            } catch (Exception ignored) {
                molangY = fromString(fy.getAsString());
            }
        }

        JsonElement fz = arr.get(2);
        if (fz != null) {
            try {
                z = fz.getAsDouble();
            } catch (Exception ignored) {
                molangZ = fromString(fz.getAsString());
            }
        }
    }

    static MolangExpr fromDouble(double value) throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        return fromString(Double.toString(value));
    }

    static MolangExpr fromString(String s) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        if (s == null || s.isBlank()) return fromDouble(0);
        MolangExpr molangExpr = MolangExpr.parseOrThrow(s.trim().replace("Math", "math"));

        return MolangOptimizer.optimizeAst(molangExpr);
    }

    public Point evaluate(double time) {
        data.setTime(time);

        double evaluatedX = x;
        if (molangX != null) {
            try {
                evaluatedX = this.evaluator.eval(this.molangX);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        double evaluatedY = y;
        if (molangY != null) {
            try {
                evaluatedY = this.evaluator.eval(this.molangY);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        double evaluatedZ = z;
        if (molangZ != null) {
            try {
                evaluatedZ = this.evaluator.eval(this.molangZ);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return new Vec(evaluatedX, evaluatedY, evaluatedZ);
    }

    @Override
    public String toString() {
        if (molangX != null || molangY != null || molangZ != null) {
            return "MQLPoint{" +
                    "x=" + molangX +
                    ", y=" + molangY +
                    ", z=" + molangZ +
                    '}';
        }

        return "MQLPoint{" +
                "x=" + x +
                ", y=" + y +
                ", z=" + z +
                '}';
    }
}
