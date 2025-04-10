package artifacts.ability.mobeffect;

import artifacts.config.value.Value;
import artifacts.config.value.ValueTypes;
import artifacts.registry.ModDataComponents;
import artifacts.util.AbilityHelper;
import artifacts.util.ModCodecs;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;

import java.util.List;
import java.util.Optional;

public record ApplyMobEffectAfterDamageAbility(Holder<MobEffect> mobEffect, Value<Integer> level, Value<Integer> duration, Optional<TagKey<DamageType>> tag) implements MobEffectAbility {

    public static final Codec<ApplyMobEffectAfterDamageAbility> CODEC = RecordCodecBuilder.create(
            instance -> MobEffectAbility.codecStartWithDuration(instance)
                    .and(TagKey.codec(Registries.DAMAGE_TYPE).optionalFieldOf("tag").forGetter(ApplyMobEffectAfterDamageAbility::tag))
                    .apply(instance, ApplyMobEffectAfterDamageAbility::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, ApplyMobEffectAfterDamageAbility> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.holderRegistry(Registries.MOB_EFFECT),
            ApplyMobEffectAfterDamageAbility::mobEffect,
            ValueTypes.MOB_EFFECT_LEVEL.streamCodec(),
            ApplyMobEffectAfterDamageAbility::level,
            ValueTypes.DURATION.streamCodec(),
            ApplyMobEffectAfterDamageAbility::duration,
            ByteBufCodecs.optional(ModCodecs.tagKeyStreamCodec(Registries.DAMAGE_TYPE)),
            ApplyMobEffectAfterDamageAbility::tag,
            ApplyMobEffectAfterDamageAbility::new
    );

    public static void onLivingDamaged(LivingEntity entity, DamageSource damageSource, float amount) {
        if (!entity.level().isClientSide() && amount >= 0.1) { // TODO remove amount check
            AbilityHelper.forEach(ModDataComponents.APPLY_MOB_EFFECT_AFTER_DAMAGE.get(), entity, (ability, stack) -> {
                if (ability.tag().isEmpty() || damageSource.is(ability.tag().get())) {
                    entity.addEffect(ability.createEffect(entity));
                }
            }, true, true);
        }
    }

    @Override
    public boolean isVisible() {
        return true;
    }

    @Override
    public void addAbilityTooltip(List<MutableComponent> tooltip) {
        if (mobEffect().equals(MobEffects.FIRE_RESISTANCE) && tag.isPresent() && tag.get().equals(DamageTypeTags.IS_FIRE)) {
            tooltip.add(tooltipLine("fire_resistance"));
        } else if (mobEffect().equals(MobEffects.MOVEMENT_SPEED)) {
            tooltip.add(tooltipLine("speed"));
        }
    }
}
