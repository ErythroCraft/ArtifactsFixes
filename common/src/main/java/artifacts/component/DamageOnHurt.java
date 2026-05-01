package artifacts.component;

import artifacts.config.value.Value;
import artifacts.config.value.ValueTypes;
import artifacts.equipment.EquipmentHelper;
import artifacts.registry.ModDataComponents;
import artifacts.util.ModCodecs;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

import java.util.Optional;

// This doesn't implement EquipmentAbility since it shouldn't prevent items from being marked as 'cosmetic',
// but this also means the item will still be damaged when all abilities are disabled.
// It's not a major issue since this won't occur with sensible config settings.
public record DamageOnHurt(Value<Integer> itemDamage, Optional<TagKey<DamageType>> tag) {

    public static final Codec<DamageOnHurt> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ValueTypes.NON_NEGATIVE_INT.codec().fieldOf("item_damage").forGetter(DamageOnHurt::itemDamage),
            TagKey.codec(Registries.DAMAGE_TYPE).optionalFieldOf("tag").forGetter(DamageOnHurt::tag)
    ).apply(instance, DamageOnHurt::new));

    public static final StreamCodec<ByteBuf, DamageOnHurt> STREAM_CODEC = StreamCodec.composite(
            ValueTypes.DURATION.streamCodec(),
            DamageOnHurt::itemDamage,
            ByteBufCodecs.optional(ModCodecs.tagKeyStreamCodec(Registries.DAMAGE_TYPE)),
            DamageOnHurt::tag,
            DamageOnHurt::new
    );

    public static void onLivingDamaged(LivingEntity entity, DamageSource damageSource) {
        if (entity instanceof Player player && !player.level().isClientSide()) {
            EquipmentHelper.iterateComponents(ModDataComponents.DAMAGE_ON_HURT.get(), entity, (ability, slotAccess) -> {
                if (ability.tag().isEmpty() || damageSource.is(ability.tag().get())) {
                    if (!slotAccess.isDisabledOrBroken() && !slotAccess.isOnCooldown(entity)) {
                        slotAccess.hurtAndBreak(entity, ability.itemDamage.get());
                    }
                }
            });
        }
    }
}
