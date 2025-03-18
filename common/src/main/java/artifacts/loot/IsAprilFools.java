package artifacts.loot;

import artifacts.registry.ModLootConditions;
import com.mojang.serialization.MapCodec;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;

import java.time.LocalDate;
import java.time.Month;

public record IsAprilFools() implements LootItemCondition {

    public static final MapCodec<IsAprilFools> CODEC = MapCodec.unit(new IsAprilFools());

    private static final boolean IS_APRIL_FOOLS = LocalDate.now().getMonth() == Month.APRIL && LocalDate.now().getDayOfMonth() == 1;

    @Override
    public LootItemConditionType getType() {
        return ModLootConditions.IS_APRIL_FOOLS.value();
    }

    @Override
    public boolean test(LootContext lootContext) {
        return IS_APRIL_FOOLS;
    }

    public static Builder builder() {
        return IsAprilFools::new;
    }
}
