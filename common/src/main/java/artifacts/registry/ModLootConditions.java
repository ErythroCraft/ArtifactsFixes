package artifacts.registry;

import artifacts.loot.ArtifactRarityAdjustedChance;
import artifacts.loot.ConfigValueChance;
import artifacts.loot.ConfigValueCondition;
import artifacts.loot.IsAprilFools;
import artifacts.platform.PlatformServices;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;

public class ModLootConditions {

    public static final Register<LootItemConditionType> LOOT_CONDITION_TYPES = PlatformServices.platformHelper.createRegister(Registries.LOOT_CONDITION_TYPE);

    public static final Holder<LootItemConditionType> ARTIFACT_RARITY_ADJUSTED_CHANCE = register("artifact_rarity_adjusted_chance", ArtifactRarityAdjustedChance.CODEC);
    public static final Holder<LootItemConditionType> CONFIG_VALUE_CHANCE = register("config_value_chance", ConfigValueChance.CODEC);
    public static final Holder<LootItemConditionType> IS_APRIL_FOOLS = register("is_april_fools", IsAprilFools.CODEC);
    public static final Holder<LootItemConditionType> CONFIG_VALUE = register("config_value", ConfigValueCondition.CODEC);

    private static Holder<LootItemConditionType> register(String name, MapCodec<? extends LootItemCondition> codec) {
        return LOOT_CONDITION_TYPES.register(name, () -> new LootItemConditionType(codec));
    }
}
