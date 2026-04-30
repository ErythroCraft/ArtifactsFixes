package artifacts.component.ability;

import artifacts.equipment.EquipmentSlotAccess;
import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.LivingEntity;

import java.util.List;

public record CompositeAbility<ENTRY extends EquipmentAbility>(List<ENTRY> entries) implements EquipmentAbility {

    public static <ENTRY extends EquipmentAbility> Codec<CompositeAbility<ENTRY>> codec(Codec<ENTRY> entryCodec) {
        return entryCodec.listOf().xmap(CompositeAbility::new, CompositeAbility::entries);
    }

    public static <ENTRY extends EquipmentAbility, B extends ByteBuf> StreamCodec<B, CompositeAbility<ENTRY>> streamCodec(StreamCodec<B, ENTRY> entryCodec) {
        return ByteBufCodecs.<B, ENTRY>list().apply(entryCodec).map(CompositeAbility::new, CompositeAbility::entries);
    }

    @SafeVarargs
    public static <ENTRY extends EquipmentAbility> CompositeAbility<ENTRY> of(ENTRY... entries) {
        return new CompositeAbility<>(List.of(entries));
    }

    @Override
    public boolean isNonCosmetic() {
        for (ENTRY entry : entries()) {
            if (entry.isNonCosmetic()) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void addToTooltip(TooltipWriter writer) {
        for (ENTRY entry : entries()) {
            if (entry.isNonCosmetic()) {
                entry.addToTooltip(writer);
            }
        }
    }

    public record Ticker<ENTRY extends EquipmentAbility>(AbilityTicker<ENTRY> entryTicker)
            implements AbilityTicker<CompositeAbility<ENTRY>> {

        @Override
        public void wornTick(CompositeAbility<ENTRY> ability, EquipmentSlotAccess slotAccess, LivingEntity entity, boolean isOnCooldown, boolean isDisabled) {
            for (ENTRY entry : ability.entries) {
                entryTicker.wornTick(entry, slotAccess, entity, isOnCooldown, isDisabled);
            }
        }

        @Override
        public void onUnequip(CompositeAbility<ENTRY> ability, LivingEntity entity) {
            for (ENTRY entry : ability.entries) {
                entryTicker.onUnequip(entry, entity);
            }
        }
    }
}
