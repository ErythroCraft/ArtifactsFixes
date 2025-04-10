package artifacts.ability;

import artifacts.config.value.Value;
import artifacts.config.value.ValueTypes;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.codec.StreamCodec;

import java.util.List;

public record EnderPearlsCostHungerAbility(Value<Boolean> enabled, Value<Integer> cost, Value<Integer> cooldown) implements ArtifactAbility {

    public static final Codec<EnderPearlsCostHungerAbility> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ValueTypes.enabledField().forGetter(EnderPearlsCostHungerAbility::enabled),
            ValueTypes.NON_NEGATIVE_INT.codec().fieldOf("cost").forGetter(EnderPearlsCostHungerAbility::cost),
            ValueTypes.cooldownField().forGetter(EnderPearlsCostHungerAbility::cooldown)
    ).apply(instance, EnderPearlsCostHungerAbility::new));

    public static final StreamCodec<ByteBuf, EnderPearlsCostHungerAbility> STREAM_CODEC = StreamCodec.composite(
            ValueTypes.BOOLEAN.streamCodec(),
            EnderPearlsCostHungerAbility::enabled,
            ValueTypes.NON_NEGATIVE_INT.streamCodec(),
            EnderPearlsCostHungerAbility::cost,
            ValueTypes.DURATION.streamCodec(),
            EnderPearlsCostHungerAbility::cooldown,
            EnderPearlsCostHungerAbility::new
    );

    @Override
    public boolean isNonCosmetic() {
        return enabled.get();
    }

    @Override
    public void addAbilityTooltip(List<MutableComponent> tooltip) {
        if (cost.get() == 0) {
            tooltip.add(tooltipLine("free"));
        } else {
            tooltip.add(tooltipLine("cost"));
        }
    }
}
