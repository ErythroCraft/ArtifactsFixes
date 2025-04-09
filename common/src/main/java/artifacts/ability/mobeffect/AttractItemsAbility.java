package artifacts.ability.mobeffect;

import artifacts.config.value.Value;
import artifacts.config.value.ValueTypes;
import artifacts.registry.ModAbilities;
import artifacts.registry.ModMobEffects;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;

import java.util.Objects;

public final class AttractItemsAbility extends ConstantMobEffectAbility {

    public static final MapCodec<AttractItemsAbility> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
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
    public Type<?> getType() {
        return ModAbilities.ATTRACT_ITEMS.value();
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
