package artifacts.registry;

import artifacts.loot.ReplaceWithLootTableFunction;
import artifacts.platform.PlatformServices;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;

public class ModLootFunctions {

    public static final Register<LootItemFunctionType<?>> LOOT_FUNCTION_TYPES = PlatformServices.platformHelper.createRegister(Registries.LOOT_FUNCTION_TYPE);

    public static final RegistryHolder<LootItemFunctionType<?>, LootItemFunctionType<ReplaceWithLootTableFunction>> REPLACE_WITH_LOOT_TABLE = LOOT_FUNCTION_TYPES.register("replace_with_loot_table", () -> new LootItemFunctionType<>(ReplaceWithLootTableFunction.CODEC));

}
