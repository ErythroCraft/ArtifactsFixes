package artifacts.component;

import artifacts.component.ability.EntityCondition;
import artifacts.config.value.Value;
import artifacts.config.value.ValueTypes;
import artifacts.equipment.EquipmentHelper;
import artifacts.registry.ModDataComponents;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.LivingEntity;

public record DamageOverTime(Value<Integer> damagePerSecond, EntityCondition condition) {

    public static final Codec<DamageOverTime> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ValueTypes.NON_NEGATIVE_INT.codec().fieldOf("damage_per_second").forGetter(DamageOverTime::damagePerSecond),
            EntityCondition.CODEC.optionalFieldOf("condition", EntityCondition.ALWAYS).forGetter(DamageOverTime::condition)
    ).apply(instance, DamageOverTime::new));

    public static final StreamCodec<ByteBuf, DamageOverTime> STREAM_CODEC = StreamCodec.composite(
            ValueTypes.NON_NEGATIVE_INT.streamCodec(),
            DamageOverTime::damagePerSecond,
            EntityCondition.STREAM_CODEC,
            DamageOverTime::condition,
            DamageOverTime::new
    );

    public static void onLivingUpdate(LivingEntity entity) {
        if (entity.tickCount % 20 == 0) {
            EquipmentHelper.iterateComponents(
                    ModDataComponents.DAMAGE_OVER_TIME.get(),
                    entity,
                    true, true,
                    (component, slotAccess) -> {
                        if (component.condition.test(entity)) {
                            slotAccess.hurtAndBreak(entity, component.damagePerSecond.get());
                        }
                    }
            );
        }
    }
}
