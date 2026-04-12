package net.worldseed.multipart.animations.script;

import net.hollowcube.molang.eval.MolangEvaluator;
import net.hollowcube.molang.eval.MolangValue;
import net.worldseed.multipart.animations.ModelAnimationInstance;

import java.util.HashMap;
import java.util.Map;

public class RuntimeScriptExecutor implements ScriptExecutor {


    private ModelAnimationInstance instance;
    private MolangEvaluator molangEvaluator;
    private ThreadLocal<Map<String, MolangValue>> customQueryValues = ThreadLocal.withInitial(HashMap::new);

    public RuntimeScriptExecutor(ModelAnimationInstance instance) {
        this.instance = instance;

        MolangValue query = (MolangValue.Holder) field -> {
            if(customQueryValues.get().containsKey(field)) {
                return customQueryValues.get().get(field);
            }

            if(field.equals("anim_time")) {
                return new MolangValue.Num(instance.getTick() * 50.0 / 1000);
            }
            return MolangValue.NIL;
        };


        this.molangEvaluator = new MolangEvaluator(Map.of(
                "q", query,
                "query", query
        ));
    }


    public void setLocalCustomQueryValue(String name, MolangValue value) {
        this.customQueryValues.get().put(name, value);
    }

    public MolangEvaluator getMolangEvaluator() {
        return molangEvaluator;
    }

    public void resetLocalCustomValues() {
        this.customQueryValues.remove();
    }

}
