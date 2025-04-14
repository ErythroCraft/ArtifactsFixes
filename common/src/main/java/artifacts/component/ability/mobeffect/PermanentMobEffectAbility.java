package artifacts.component.ability.mobeffect;

import artifacts.component.ability.AbilityCondition;
import artifacts.component.ability.TickingAbility;
import artifacts.config.value.Value;
import artifacts.registry.ModDataComponents;
import artifacts.registry.ModMobEffects;
import com.mojang.serialization.Codec;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;

import java.util.List;
import java.util.Objects;
import java.util.Set;

public record PermanentMobEffectAbility(List<MobEffectProvider> effects) implements TickingAbility {

    private static final Set<Holder<MobEffect>> CUSTOM_TOOLTIP_MOB_EFFECTS = Set.of(
            MobEffects.INVISIBILITY,
            ModMobEffects.MAGNETISM
    );

    public static final Codec<PermanentMobEffectAbility> CODEC = MobEffectProvider.codec(false).listOf(0, 16).xmap(
            PermanentMobEffectAbility::new, PermanentMobEffectAbility::effects
    );

    public static final StreamCodec<RegistryFriendlyByteBuf, PermanentMobEffectAbility> STREAM_CODEC = ByteBufCodecs
            .<RegistryFriendlyByteBuf, MobEffectProvider>list()
            .apply(MobEffectProvider.STREAM_CODEC).map(
                    PermanentMobEffectAbility::new, PermanentMobEffectAbility::effects
            );

    @Override
    public void wornTick(LivingEntity entity, boolean isOnCooldown, boolean isDisabled) {
        if (!isDisabled && !isOnCooldown) {
            for (MobEffectProvider provider : effects()) {
                if (provider.canApply(entity)) {
                    entity.addEffect(provider.createEffect());
                }
            }
        }
    }

    @Override
    public void onUnequip(LivingEntity entity) {
        for (MobEffectProvider provider : effects()) {
            MobEffectInstance instance = entity.getEffect(provider.mobEffect());
            if (instance != null
                    && instance.getAmplifier() == provider.getAmplifier()
                    && instance.isVisible() == provider.spawnParticles().get()
                    && instance.showIcon() == provider.showIcon().get()
                    && instance.endsWithin(provider.getDuration() * 20 + 19)
            ) {
                entity.removeEffect(provider.mobEffect());
            }
        }
    }

    @Override
    public void addToTooltip(TooltipWriter writer) {
        for (MobEffectProvider provider : effects) {
            if (provider.condition() == AbilityCondition.NEVER || !provider.isNonCosmetic()) {
                continue;
            }
            if (CUSTOM_TOOLTIP_MOB_EFFECTS.contains(provider.mobEffect())) {
                ResourceLocation id = BuiltInRegistries.MOB_EFFECT.getKey(provider.mobEffect().value());
                writer.add(Objects.requireNonNull(id).getPath());
            }
            if (provider.mobEffect().value() == MobEffects.NIGHT_VISION.value()) {
                Value<Double> nightVisionStrength = writer.stack().get(ModDataComponents.REDUCES_NIGHT_VISION_STRENGTH.get());
                if (nightVisionStrength != null && nightVisionStrength.get() < 0.5) {
                    writer.add("night_vision.partial");
                } else {
                    writer.add("night_vision.full");
                }
            }
            if (provider.mobEffect().value() == MobEffects.WATER_BREATHING.value()) {
                if (provider.condition() == AbilityCondition.ALWAYS) {
                    writer.add("water_breathing.infinite");
                } else {
                    writer.add("water_breathing.limited");
                }
            }
        }
    }

    @Override
    public boolean isNonCosmetic() {
        for (MobEffectProvider provider : effects) {
            if (provider.isNonCosmetic()) {
                return true;
            }
        }
        return false;
    }
}
