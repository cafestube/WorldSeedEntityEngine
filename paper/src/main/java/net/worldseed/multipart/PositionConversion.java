package net.worldseed.multipart;

import net.worldseed.multipart.math.Pos;
import net.worldseed.multipart.math.Vec;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.util.Vector;

public class PositionConversion {

    public static Location asPaper(World world, Pos p) {
        return new Location(world, p.x(), p.y(), p.z(), p.yaw(), p.pitch());
    }

    public static Location asPaper(World world, Vec p) {
        return new Location(world, p.x(), p.y(), p.z(), 0, 0);
    }

    public static Pos fromPaper(Location p) {
        return new Pos(p.x(), p.y(), p.z(), p.getYaw(), p.getPitch());
    }
}
