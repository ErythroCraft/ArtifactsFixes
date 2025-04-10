package artifacts.ability.mobeffect;

import artifacts.config.value.Value;
import artifacts.config.value.ValueTypes;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffects;

import java.util.List;
import java.util.Objects;

public class NightVisionAbility extends ConstantMobEffectAbility {

    public static final Codec<NightVisionAbility> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ValueTypes.FRACTION.codec().optionalFieldOf("strength", Value.of(1D)).forGetter(NightVisionAbility::strength)
    ).apply(instance, NightVisionAbility::new));

    public static final StreamCodec<ByteBuf, NightVisionAbility> STREAM_CODEC = StreamCodec.composite(
            ValueTypes.FRACTION.streamCodec(),
            NightVisionAbility::strength,
            NightVisionAbility::new
    );

    private final Value<Double> strength;

    public NightVisionAbility(Value<Double> strength) {
        super(MobEffects.NIGHT_VISION);
        this.strength = strength;
    }

    public Value<Double> strength() {
        return strength;
    }

    @Override
    public boolean isNonCosmetic() {
        return !Mth.equal(strength().get(), 0);
    }

    @Override
    public void addAbilityTooltip(List<MutableComponent> tooltip) {
        if (strength.get() > 0.5) {
            tooltip.add(tooltipLine("full"));
        } else {
            tooltip.add(tooltipLine("partial"));
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        NightVisionAbility that = (NightVisionAbility) o;
        return strength.equals(that.strength);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), strength);
    }
}
