package net.worldseed.multipart;

import net.worldseed.multipart.blueprint.ModelBoneInfo;
import net.worldseed.multipart.entity.ModelBone;

public interface BoneFactory<TViewer> {

    public ModelBone<TViewer> create(ModelBoneInfo info, float scale);

}
