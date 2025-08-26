package net.worldseed.multipart;

public class PositionConversion {

    public static net.minestom.server.coordinate.Vec asMinestom(net.worldseed.multipart.math.Vec p) {
        return new net.minestom.server.coordinate.Vec(p.x(), p.y(), p.z());
    }

    public static net.minestom.server.coordinate.Pos asMinestom(net.worldseed.multipart.math.Pos p) {
        return new net.minestom.server.coordinate.Pos(p.x(), p.y(), p.z(), p.yaw(), p.pitch());
    }

    public static net.minestom.server.coordinate.Point asMinestom(net.worldseed.multipart.math.Point p) {
        if(p instanceof net.worldseed.multipart.math.Pos pos) {
            return asMinestom(pos);
        } else {
            return asMinestom((net.worldseed.multipart.math.Vec) p);
        }
    }


    public static net.worldseed.multipart.math.Vec fromMinestom(net.minestom.server.coordinate.Vec p) {
        return new net.worldseed.multipart.math.Vec(p.x(), p.y(), p.z());
    }

    public static net.worldseed.multipart.math.Pos fromMinestom(net.minestom.server.coordinate.Pos p) {
        return new net.worldseed.multipart.math.Pos(p.x(), p.y(), p.z(), p.yaw(), p.pitch());
    }
}
