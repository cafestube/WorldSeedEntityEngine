package net.worldseed.multipart.animations.script;

import net.hollowcube.molang.eval.MolangEvaluator;
import net.hollowcube.molang.eval.MolangValue;

public interface ScriptExecutor {

    void setLocalCustomQueryValue(String name, MolangValue value);

    MolangEvaluator getMolangEvaluator();

    void resetLocalCustomValues();
}
