package artifacts.component.ability;

import artifacts.config.value.Value;
import artifacts.config.value.ValueTypes;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;

public record EnderPearlsCostHungerAbility(Value<Boolean> enabled, Value<Integer> cost, Value<Integer> cooldown)
        implements EquipmentAbility {

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
    public void addToTooltip(TooltipWriter writer) {
        if (cost.get() == 0) {
            writer.add("free");
        } else {
            writer.add("cost");
        }
    }
}
