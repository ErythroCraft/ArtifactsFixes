package artifacts.ability;

import artifacts.config.value.Value;
import artifacts.config.value.ValueTypes;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.Mth;

import java.util.List;

public record AttacksAbsorbDamageAbility(Value<Double> absorptionRatio, Value<Double> absorptionChance, Value<Integer> maxDamageAbsorbed) implements ArtifactAbility {

    public static final Codec<AttacksAbsorbDamageAbility> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ValueTypes.NON_NEGATIVE_DOUBLE.codec().fieldOf("absorption_ratio").forGetter(AttacksAbsorbDamageAbility::absorptionRatio),
            ValueTypes.FRACTION.codec().fieldOf("absorption_chance").forGetter(AttacksAbsorbDamageAbility::absorptionChance),
            ValueTypes.NON_NEGATIVE_INT.codec().fieldOf("max_damage_absorbed").forGetter(AttacksAbsorbDamageAbility::maxDamageAbsorbed)
    ).apply(instance, AttacksAbsorbDamageAbility::new));

    public static final StreamCodec<ByteBuf, AttacksAbsorbDamageAbility> STREAM_CODEC = StreamCodec.composite(
            ValueTypes.NON_NEGATIVE_DOUBLE.streamCodec(),
            AttacksAbsorbDamageAbility::absorptionRatio,
            ValueTypes.FRACTION.streamCodec(),
            AttacksAbsorbDamageAbility::absorptionChance,
            ValueTypes.NON_NEGATIVE_INT.streamCodec(),
            AttacksAbsorbDamageAbility::maxDamageAbsorbed,
            AttacksAbsorbDamageAbility::new
    );

    @Override
    public boolean isNonCosmetic() {
        return absorptionRatio.get() > 0 && absorptionChance.get() > 0 && maxDamageAbsorbed.get() > 0;
    }

    @Override
    public void addAbilityTooltip(List<MutableComponent> tooltip) {
        if (Mth.equal(absorptionChance.get(), 1)) {
            tooltip.add(tooltipLine("constant"));
        } else {
            tooltip.add(tooltipLine("chance"));
        }
    }
}
