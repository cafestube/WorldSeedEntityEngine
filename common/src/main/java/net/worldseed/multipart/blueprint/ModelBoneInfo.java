package net.worldseed.multipart.blueprint;

import com.google.gson.JsonArray;
import net.worldseed.multipart.math.Point;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public record ModelBoneInfo(
        String name,
        @Nullable String parent,
        Point pivot,
        Point rotation,
        @Nullable Point diff,
        @Nullable Point offset,
        @Nullable Map<String, ModelRenderInformation> renderInfo,
        JsonArray cubes
) { }