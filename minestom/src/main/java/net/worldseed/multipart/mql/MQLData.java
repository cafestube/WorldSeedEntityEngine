package net.worldseed.multipart.mql;

import net.hollowcube.molang.eval.MolangValue;
import org.jetbrains.annotations.NotNull;

public class MQLData implements MolangValue.Holder {
    private double time;

    public void setTime(double time) {
        this.time = time;
    }

    @Override
    public @NotNull MolangValue get(@NotNull String field) {
        if(field.equals("anim_time")) {
            return new MolangValue.Num(this.time);
        }

        return MolangValue.NIL;
    }
}
