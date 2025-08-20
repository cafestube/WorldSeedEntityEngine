package net.worldseed.resourcepack.math;

public record Vec(double x, double y, double z) {

    public static Vec ZERO = new Vec(0, 0, 0);

    public Vec withX(double v) {
        return new Vec(v, this.y, this.z);
    }

    public Vec mul(int x, int y, int z) {
        return new Vec(this.x * x, this.y * y, this.z * z);
    }

    public Vec sub(int x, int y, int z) {
        return new Vec(this.x - x, this.y - y, this.z - z);
    }

    public Vec sub(Vec vec) {
        return new Vec(this.x - vec.x, this.y - vec.y, this.z - vec.z);
    }
}
