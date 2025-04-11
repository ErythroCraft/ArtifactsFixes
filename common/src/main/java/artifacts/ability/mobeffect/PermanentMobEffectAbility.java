package artifacts.ability.mobeffect;

import artifacts.ability.AbilityWithTooltip;
import artifacts.config.value.Value;
import artifacts.config.value.ValueTypes;
import artifacts.registry.ModMobEffects;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffects;

import java.util.Objects;
import java.util.Set;

public class PermanentMobEffectAbility extends ConstantMobEffectAbility implements AbilityWithTooltip {

    private static final Set<Holder<MobEffect>> CUSTOM_TOOLTIP_MOB_EFFECTS = Set.of(
            MobEffects.INVISIBILITY,
            ModMobEffects.MAGNETISM // TODO add different magnetism strengths
    );

    public static final Codec<PermanentMobEffectAbility> CODEC = RecordCodecBuilder.create(instance -> MobEffectAbility.codecStart(instance)
            .and(ValueTypes.enabledField().forGetter(ability -> ability.enabled))
            .apply(instance, PermanentMobEffectAbility::new)
    );

    public static final StreamCodec<RegistryFriendlyByteBuf, PermanentMobEffectAbility> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.holderRegistry(Registries.MOB_EFFECT),
            PermanentMobEffectAbility::mobEffect,
            ValueTypes.MOB_EFFECT_LEVEL.streamCodec(),
            PermanentMobEffectAbility::level,
            ValueTypes.BOOLEAN.streamCodec(),
            ability -> ability.enabled,
            PermanentMobEffectAbility::new
    );

    private final Value<Boolean> enabled;

    public PermanentMobEffectAbility(Holder<MobEffect> mobEffect, Value<Integer> level, Value<Boolean> enabled) {
        super(mobEffect, level);
        this.enabled = enabled;
    }

    @Override
    public Value<Integer> level() {
        return enabled.get() ? super.level() : Value.of(0);
    }

    @Override
    public void addToTooltip(TooltipWriter writer) {
        for (Holder<MobEffect> mobEffect : CUSTOM_TOOLTIP_MOB_EFFECTS) {
            if (mobEffect.isBound() && mobEffect.value() == mobEffect().value()) {
                writer.add(Objects.requireNonNull(BuiltInRegistries.MOB_EFFECT.getKey(mobEffect.value())).getPath());
                return;
            }
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        PermanentMobEffectAbility that = (PermanentMobEffectAbility) o;
        return enabled.equals(that.enabled);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), enabled);
    }
}
