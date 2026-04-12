package net.worldseed.multipart.animations.script;

import net.hollowcube.molang.eval.MolangEvaluator;
import net.hollowcube.molang.eval.MolangValue;
import net.worldseed.multipart.animations.ModelAnimationInstance;

import java.util.HashMap;
import java.util.Map;

public class PrecomputeScriptExecutor implements ScriptExecutor {

    private MolangEvaluator molangEvaluator;
    private Map<String, MolangValue> customQueryValues = new HashMap<>();

    public PrecomputeScriptExecutor() {
        MolangValue query = (MolangValue.Holder) field -> {
            if(customQueryValues.containsKey(field)) {
                return customQueryValues.get(field);
            }

            return MolangValue.NIL;
        };

        this.molangEvaluator = new MolangEvaluator(Map.of(
                "q", query,
                "query", query
        ));
    }


    public void setLocalCustomQueryValue(String name, MolangValue value) {
        this.customQueryValues.put(name, value);
    }

    public MolangEvaluator getMolangEvaluator() {
        return molangEvaluator;
    }

    public void resetLocalCustomValues() {
        this.customQueryValues.clear();
    }

}
