package artifacts.ability.mobeffect;

import artifacts.config.value.Value;
import artifacts.config.value.ValueTypes;
import artifacts.registry.ModDataComponents;
import artifacts.util.AbilityHelper;
import artifacts.util.DamageSourceHelper;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

import java.util.List;
import java.util.Set;

public record AttacksInflictMobEffectAbility(Holder<MobEffect> mobEffect, Value<Integer> level, Value<Integer> duration, Value<Integer> cooldown, Value<Double> chance) implements MobEffectAbility {

    private static final Set<Holder<MobEffect>> CUSTOM_TOOLTIP_MOB_EFFECTS = Set.of(
            MobEffects.WITHER
    );

    public static final Codec<AttacksInflictMobEffectAbility> CODEC = RecordCodecBuilder.create(
            instance -> MobEffectAbility.codecStartWithDuration(instance)
                    .and(ValueTypes.cooldownField().forGetter(AttacksInflictMobEffectAbility::cooldown))
                    .and(ValueTypes.FRACTION.codec().optionalFieldOf("chance", Value.of(1D)).forGetter(AttacksInflictMobEffectAbility::chance))
                    .apply(instance, AttacksInflictMobEffectAbility::new)
    );

    public static final StreamCodec<RegistryFriendlyByteBuf, AttacksInflictMobEffectAbility> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.holderRegistry(Registries.MOB_EFFECT),
            AttacksInflictMobEffectAbility::mobEffect,
            ValueTypes.MOB_EFFECT_LEVEL.streamCodec(),
            AttacksInflictMobEffectAbility::level,
            ValueTypes.DURATION.streamCodec(),
            AttacksInflictMobEffectAbility::duration,
            ValueTypes.DURATION.streamCodec(),
            AttacksInflictMobEffectAbility::cooldown,
            ValueTypes.FRACTION.streamCodec(),
            AttacksInflictMobEffectAbility::chance,
            AttacksInflictMobEffectAbility::new
    );

    public static void onLivingHurt(LivingEntity entity, DamageSource damageSource) {
        LivingEntity attacker = DamageSourceHelper.getAttacker(damageSource);
        if (attacker != null && DamageSourceHelper.isMeleeAttack(damageSource) && !entity.level().isClientSide()) {
            AbilityHelper.forEach(ModDataComponents.ATTACKS_INFLICT_MOB_EFFECT.get(), attacker, (ability, stack) -> {
                if (entity.getRandom().nextDouble() < ability.chance().get()) {
                    entity.addEffect(ability.createEffect(attacker), attacker);
                    if (attacker instanceof Player player) {
                        player.getCooldowns().addCooldown(stack.getItem(), ability.cooldown().get() * 20);
                    }
                }
            }, true, true);
        }
    }

    @Override
    public boolean isVisible() {
        return true;
    }

    @Override
    public boolean isNonCosmetic() {
        return duration().get() > 0 && level().get() > 0 && chance().get() > 0;
    }

    @Override
    public void addAbilityTooltip(List<MutableComponent> tooltip) {
        for (Holder<MobEffect> mobEffect : CUSTOM_TOOLTIP_MOB_EFFECTS) {
            if (mobEffect.isBound() && mobEffect.value() == mobEffect().value()) {
                //noinspection ConstantConditions
                String name = BuiltInRegistries.MOB_EFFECT.getKey(mobEffect.value()).getPath();
                if (Mth.equal(chance().get(), 1)) {
                    tooltip.add(tooltipLine(name + ".constant"));
                } else {
                    tooltip.add(tooltipLine(name + ".chance"));
                }
                return;
            }
        }
    }
}
