package net.worldseed.multipart.math;

public interface Point {

    double x();
    double y();
    double z();

    Point mul(double x, double y, double z);

    default Point mul(double value) {
        return mul(value, value, value);
    }

    default Point div(double value) {
        return div(value, value, value);
    }

    Point add(Point value);

    Point sub(Point value);

    Point mul(Point value);

    Point add(double x, double y, double z);

    Point div(double i, double i1, double i2);

    Point sub(double x, double y, double z);

    Point withX(double x);

    Point withY(double y);

    Point withZ(double z);

    Point lerp(Point target, double delta);
}
