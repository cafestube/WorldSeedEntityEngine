package net.worldseed.multipart.animations;

import net.worldseed.multipart.animations.script.ScriptExecutor;
import net.worldseed.multipart.math.Point;
import net.worldseed.multipart.math.Vec;

public interface FrameProvider {
    Point RotationMul = new Vec(-1, -1, 1);
    Point TranslationMul = new Vec(-1, 1, 1);

    Point getFrame(ScriptExecutor executor, int tick);
}
