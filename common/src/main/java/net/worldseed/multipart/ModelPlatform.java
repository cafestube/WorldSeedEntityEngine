package net.worldseed.multipart;

import net.worldseed.multipart.math.Pos;
import net.worldseed.multipart.entity.entity.*;
import net.worldseed.multipart.scheduling.Scheduler;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public interface ModelPlatform<TViewer> {

    ItemDisplayBoneEntity<TViewer> createItemDisplayBoneEntity(GenericModel<TViewer> model, String name);

    RootBoneEntity<TViewer> createRootEntity(GenericModel<TViewer> model);

    TextDisplayBoneEntity<TViewer> createTextDisplayBoneEntity(GenericModel<TViewer> model, String name);

    HitboxEntity<TViewer> createHitboxEntity(GenericModel<TViewer> model, String name);

    void spawn(GenericModel<TViewer> model, BoneEntity<TViewer> entity, Pos position);

    Scheduler getScheduler(GenericModel<TViewer> model);
}
