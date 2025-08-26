package net.worldseed.multipart.math;

public record Pos(double x, double y, double z, float yaw, float pitch) implements Point {
    public static final Pos ZERO = new Pos(0, 0, 0, 0, 0);

    public static Pos fromPoint(Point p) {
        if(p instanceof Pos pos) return pos;

        return new Pos(p.x(), p.y(), p.z(), 0, 0);
    }

    public Pos(Point point, float yaw, float pitch) {
        this(point.x(), point.y(), point.z(), yaw, pitch);
    }

    public Pos(Point point) {
        this(point, 0, 0);
    }

    @Override
    public Pos mul(double x, double y, double z) {
        return new Pos(this.x * x, this.y * y, this.z * z, yaw, pitch);
    }

    @Override
    public Pos div(double value) {
        return new Pos(x / value, y / value, z / value, yaw, pitch);
    }

    @Override
    public Pos add(Point value) {
        return new Pos(this.x + value.x(), this.y + value.y(), this.z + value.z(), yaw, pitch);
    }

    @Override
    public Pos add(double x, double y, double z) {
        return new Pos(this.x + x, this.y + y, this.z + z, yaw, pitch);
    }

    @Override
    public Pos mul(double value) {
        return mul(value, value, value);
    }

    @Override
    public Pos sub(Point value) {
        return new Pos(this.x - value.x(), this.y - value.y(), this.z - value.z(), yaw, pitch);
    }

    @Override
    public Pos mul(Point value) {
        return new Pos(this.x * value.x(), this.y * value.y(), this.z * value.z(), yaw, pitch);
    }

    @Override
    public Pos sub(double x, double y, double z) {
        return new Pos(this.x - x, this.y - y, this.z - z, yaw, pitch);
    }

    @Override
    public Pos div(double i, double i1, double i2) {
        return new Pos(x / i, y / i1, z / i2, yaw, pitch);
    }

    public Pos withYaw(float yaw) {
        return new Pos(x, y, z, yaw, pitch);
    }

    public Pos withView(float yaw, float pitch) {
        return new Pos(x, y, z, yaw, pitch);
    }

    @Override
    public Pos withX(double x) {
        return new Pos(x, y, z, yaw, pitch);
    }

    @Override
    public Pos withY(double y) {
        return new Pos(x, y, z, yaw, pitch);
    }

    @Override
    public Pos withZ(double z) {
        return new Pos(x, y, z, yaw, pitch);
    }
}
