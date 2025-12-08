package net.worldseed.multipart.math;

public record Vec(double x, double y, double z) implements Point {

    public static final Vec ZERO = new Vec(0,0,0);
    public static final Vec ONE = new Vec(1, 1, 1);

    public static Vec fromPoint(Point point) {
        if(point instanceof Vec vec) {
            return vec;
        }

        return new Vec(point.x(), point.y(), point.z());
    }

    @Override
    public Vec mul(double x, double y, double z) {
        return new Vec(this.x * x, this.y * y, this.z * z);
    }

    @Override
    public Vec add(double x, double y, double z) {
        return new Vec(this.x + x, this.y + y, this.z + z);
    }

    @Override
    public Vec add(Point value) {
        return new Vec(this.x + value.x(), this.y + value.y(), this.z + value.z());
    }

    @Override
    public Vec sub(Point value) {
        return new Vec(this.x - value.x(), this.y - value.y(), this.z - value.z());
    }

    @Override
    public Vec mul(Point value) {
        return new Vec(this.x * value.x(), this.y * value.y(), this.z * value.z());
    }

    @Override
    public Vec div(double i, double i1, double i2) {
        return new Vec(x / i, y / i1, z / i2);
    }

    @Override
    public Vec sub(double x, double y, double z) {
        return new Vec(this.x - x, this.y - y, this.z - z);
    }

    @Override
    public Vec lerp(Point vec, double percent) {
        return new Vec(x + (percent * (vec.x() - x)),
                y + (percent * (vec.y() - y)),
                z + (percent * (vec.z() - z)));
    }

    @Override
    public Vec withY(double y) {
        return new Vec(x, y, z);
    }

    @Override
    public Vec withX(double x) {
        return new Vec(x, y, z);
    }

    @Override
    public Vec withZ(double z) {
        return new Vec(x, y, z);
    }
}
