package artifacts.component.ability;

import artifacts.config.value.Value;
import artifacts.config.value.ValueTypes;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;

public record EnderPearlHungerCost(Value<Boolean> enabled, Value<Integer> cost, Value<Integer> cooldown)
        implements EquipmentAbility {

    public static final Codec<EnderPearlHungerCost> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ValueTypes.enabledField().forGetter(EnderPearlHungerCost::enabled),
            ValueTypes.NON_NEGATIVE_INT.codec().fieldOf("cost").forGetter(EnderPearlHungerCost::cost),
            ValueTypes.cooldownField().forGetter(EnderPearlHungerCost::cooldown)
    ).apply(instance, EnderPearlHungerCost::new));

    public static final StreamCodec<ByteBuf, EnderPearlHungerCost> STREAM_CODEC = StreamCodec.composite(
            ValueTypes.BOOLEAN.streamCodec(),
            EnderPearlHungerCost::enabled,
            ValueTypes.NON_NEGATIVE_INT.streamCodec(),
            EnderPearlHungerCost::cost,
            ValueTypes.DURATION.streamCodec(),
            EnderPearlHungerCost::cooldown,
            EnderPearlHungerCost::new
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
