package artifacts.ability.mobeffect;

import artifacts.config.value.Value;
import artifacts.config.value.ValueTypes;
import artifacts.registry.ModMobEffects;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;

import java.util.Objects;

public final class AttractItemsAbility extends ConstantMobEffectAbility {

    public static final Codec<AttractItemsAbility> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ValueTypes.enabledField().forGetter(AttractItemsAbility::enabled)
    ).apply(instance, AttractItemsAbility::new));

    public static final StreamCodec<ByteBuf, AttractItemsAbility> STREAM_CODEC = StreamCodec.composite(
            ValueTypes.BOOLEAN.streamCodec(),
            AttractItemsAbility::enabled,
            AttractItemsAbility::new
    );

    private final Value<Boolean> enabled;

    public AttractItemsAbility(Value<Boolean> enabled) {
        super(ModMobEffects.MAGNETISM);
        this.enabled = enabled;
    }

    @Override
    public boolean isNonCosmetic() {
        return enabled().get();
    }

    public Value<Boolean> enabled() {
        return enabled;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (AttractItemsAbility) obj;
        return Objects.equals(this.enabled, that.enabled);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), enabled);
    }
}
