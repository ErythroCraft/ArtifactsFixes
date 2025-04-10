package artifacts.ability.mobeffect;

import artifacts.config.value.Value;
import artifacts.config.value.ValueTypes;
import artifacts.registry.ModDataComponents;
import artifacts.util.AbilityHelper;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;

import java.util.List;

public record ApplyMobEffectAfterEatingAbility(Holder<MobEffect> mobEffect, Value<Integer> level, Value<Integer> duration) implements MobEffectAbility {

    public static final Codec<ApplyMobEffectAfterEatingAbility> CODEC = RecordCodecBuilder.create(
            instance -> MobEffectAbility.codecStartWithDuration(instance)
                    .apply(instance, ApplyMobEffectAfterEatingAbility::new)
    );

    public static final StreamCodec<RegistryFriendlyByteBuf, ApplyMobEffectAfterEatingAbility> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.holderRegistry(Registries.MOB_EFFECT),
            ApplyMobEffectAfterEatingAbility::mobEffect,
            ValueTypes.DURATION.streamCodec(),
            ApplyMobEffectAfterEatingAbility::duration,
            ValueTypes.MOB_EFFECT_LEVEL.streamCodec(),
            ApplyMobEffectAfterEatingAbility::level,
            ApplyMobEffectAfterEatingAbility::new
    );

    public static void applyEffects(LivingEntity entity, FoodProperties properties) {
        int foodPointsMissing = entity instanceof Player player ? 20 - player.getFoodData().getFoodLevel() : 20;
        int foodPointsRestored = Math.min(properties.nutrition(), foodPointsMissing);
        applyEffects(entity, foodPointsRestored);
    }

    public static void applyEffects(LivingEntity entity, int foodPointsRestored) {
        if (foodPointsRestored > 0) {
            AbilityHelper.forEach(ModDataComponents.APPLY_MOB_EFFECT_AFTER_EATING.get(), entity,
                    (ability, stack) -> entity.addEffect(ability.createEffect(ability.duration().get() * 20 * foodPointsRestored)),
                    true, true
            );
        }
    }

    @Override
    public boolean isVisible() {
        return true;
    }

    @Override
    public void addAbilityTooltip(List<MutableComponent> tooltip) {
        if (mobEffect.equals(MobEffects.DIG_SPEED)) {
            tooltip.add(tooltipLine("haste"));
        }
    }
}
