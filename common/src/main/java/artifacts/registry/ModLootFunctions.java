package artifacts.registry;

import artifacts.loot.ReplaceWithLootTableFunction;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;

public class ModLootFunctions {

    public static final Register<LootItemFunctionType<?>> LOOT_FUNCTION_TYPES = Register.create(Registries.LOOT_FUNCTION_TYPE);

    public static final RegistryHolder<LootItemFunctionType<?>, LootItemFunctionType<ReplaceWithLootTableFunction>> REPLACE_WITH_LOOT_TABLE = LOOT_FUNCTION_TYPES.register("replace_with_loot_table", () -> new LootItemFunctionType<>(ReplaceWithLootTableFunction.CODEC));

}
