package artifacts.component.ability;

import artifacts.config.value.Value;
import artifacts.config.value.ValueTypes;
import artifacts.equipment.EquipmentHelper;
import artifacts.registry.ModDataComponents;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.DeathProtection;
import net.minecraft.world.item.consume_effects.ConsumeEffect;
import org.jetbrains.annotations.Nullable;

public record EquipableTotem(Value<Boolean> enabled) implements EquipmentAbility {

    public static final Codec<EquipableTotem> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ValueTypes.enabledField().forGetter(EquipableTotem::enabled)
    ).apply(instance, EquipableTotem::new));

    public static final StreamCodec<ByteBuf, EquipableTotem> STREAM_CODEC = ValueTypes.BOOLEAN.streamCodec()
            .map(EquipableTotem::new, EquipableTotem::enabled);

    @Nullable
    public static ItemStack findTotem(LivingEntity entity) {
        ItemStack totem = EquipmentHelper.reduceAbilities(
                ModDataComponents.EQUIPABLE_TOTEM.get(), entity, true, true, ItemStack.EMPTY,
                (ability, stack, result) -> result.isEmpty() && stack.has(DataComponents.DEATH_PROTECTION) ? stack : result
        );
        return totem.isEmpty() ? null : totem;
    }

    @Override
    public boolean isNonCosmetic() {
        return enabled().get();
    }

    @Override
    public void addToTooltip(TooltipWriter writer) {
        DeathProtection deathProtection = writer.components().get(DataComponents.DEATH_PROTECTION);
        if (deathProtection != null) {
            boolean causesTeleport = deathProtection.deathEffects()
                    .stream()
                    .anyMatch(consumeEffect -> consumeEffect.getType() == ConsumeEffect.Type.TELEPORT_RANDOMLY);
            if (causesTeleport) {
                writer.add("teleport");
            }
        }
    }
}
