package artifacts.neoforge.registry;

import artifacts.Artifacts;
import artifacts.neoforge.condition.ConfigValueCondition;
import com.mojang.serialization.MapCodec;
import net.neoforged.neoforge.common.conditions.ICondition;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

public class ModConditions {

    public static final DeferredRegister<MapCodec<? extends ICondition>> CONDITIONS = DeferredRegister.create(NeoForgeRegistries.Keys.CONDITION_CODECS, Artifacts.MOD_ID);

    static {
        CONDITIONS.register("config_value", () -> ConfigValueCondition.CODEC);
    }
}
