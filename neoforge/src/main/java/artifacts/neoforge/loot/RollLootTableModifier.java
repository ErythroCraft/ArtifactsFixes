package artifacts.neoforge.loot;

import com.google.common.base.Suppliers;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.neoforged.neoforge.common.loot.IGlobalLootModifier;
import net.neoforged.neoforge.common.loot.LootModifier;

import java.util.function.Supplier;

public class RollLootTableModifier extends LootModifier {

    public static final Supplier<MapCodec<RollLootTableModifier>> CODEC = Suppliers.memoize(
            () -> RecordCodecBuilder.mapCodec(instance -> codecStart(instance)
                    .and(ResourceKey.codec(Registries.LOOT_TABLE).fieldOf("lootTable").forGetter(m -> m.lootTable))
                    .and(Codec.BOOL.optionalFieldOf("replace", false).forGetter(m -> m.replace))
                    .apply(instance, RollLootTableModifier::new)
            )
    );

    private final ResourceKey<LootTable> lootTable;
    private final boolean replace;

    public RollLootTableModifier(LootItemCondition[] conditions, ResourceKey<LootTable> lootTable, boolean replace) {
        super(conditions);
        this.lootTable = lootTable;
        this.replace = replace;
    }

    @Override
    @SuppressWarnings("deprecation")
    protected ObjectArrayList<ItemStack> doApply(ObjectArrayList<ItemStack> generatedLoot, LootContext context) {
        if (replace) {
            ObjectArrayList<ItemStack> stacks = new ObjectArrayList<>(1);
            // TODO context.getResolver().get(Registries.LOOT_TABLE, lootTable).ifPresent(table -> table.value().getRandomItemsRaw(context, stacks::add));
            if (!stacks.isEmpty()) {
                return stacks;
            } else {
                return generatedLoot;
            }
        }
        // TODO context.getResolver().get(Registries.LOOT_TABLE, lootTable).ifPresent(table -> table.value().getRandomItemsRaw(context, generatedLoot::add));
        return generatedLoot;
    }

    @Override
    public MapCodec<? extends IGlobalLootModifier> codec() {
        return CODEC.get();
    }
}
