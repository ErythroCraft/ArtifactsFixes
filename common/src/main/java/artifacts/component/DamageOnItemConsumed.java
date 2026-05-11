package artifacts.component;

import artifacts.config.value.Value;
import artifacts.config.value.ValueTypes;
import artifacts.equipment.EquipmentHelper;
import artifacts.registry.ModDataComponents;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemUseAnimation;
import net.minecraft.world.item.component.Consumable;

public record DamageOnItemConsumed(Value<Integer> damageOnItemEaten, Value<Integer> damageOnItemDrunk) {

    public static final Codec<DamageOnItemConsumed> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ValueTypes.NON_NEGATIVE_INT.codec().fieldOf("damage_on_item_eaten").forGetter(DamageOnItemConsumed::damageOnItemEaten),
            ValueTypes.NON_NEGATIVE_INT.codec().fieldOf("damage_on_item_drunk").forGetter(DamageOnItemConsumed::damageOnItemDrunk)
    ).apply(instance, DamageOnItemConsumed::new));

    public static final StreamCodec<ByteBuf, DamageOnItemConsumed> STREAM_CODEC = StreamCodec.composite(
            ValueTypes.NON_NEGATIVE_INT.streamCodec(),
            DamageOnItemConsumed::damageOnItemEaten,
            ValueTypes.NON_NEGATIVE_INT.streamCodec(),
            DamageOnItemConsumed::damageOnItemDrunk,
            DamageOnItemConsumed::new
    );

    public static void onItemConsumed(LivingEntity entity, Consumable consumable) {
        EquipmentHelper.iterateComponents(
                ModDataComponents.DAMAGE_ON_ITEM_CONSUMED,
                entity,
                true, true,
                (component, slotAccess) -> {
                    if (consumable.animation() == ItemUseAnimation.EAT) {
                        slotAccess.hurtAndBreak(entity, component.damageOnItemEaten.get());
                    }
                    if (consumable.animation() == ItemUseAnimation.DRINK) {
                        slotAccess.hurtAndBreak(entity, component.damageOnItemDrunk.get());
                    }
                }
        );
    }
}
