package artifacts.component.ability;

import artifacts.config.value.Value;
import artifacts.config.value.ValueTypes;
import artifacts.util.ModCodecs;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.material.Fluid;

import java.util.Objects;
import java.util.Optional;
import java.util.function.BiFunction;

public class CollideWithFluidsAbility implements EquipmentAbility {

    private final Value<Boolean> enabled;
    private final Optional<TagKey<Fluid>> tag;

    public CollideWithFluidsAbility(Value<Boolean> enabled, Optional<TagKey<Fluid>> tag) {
        this.enabled = enabled;
        this.tag = tag;
    }

    protected static <T extends CollideWithFluidsAbility> Codec<T> codec(BiFunction<Value<Boolean>, Optional<TagKey<Fluid>>, T> constructor) {
        return RecordCodecBuilder.create(instance -> instance.group(
                ValueTypes.enabledField().forGetter(CollideWithFluidsAbility::enabled),
                TagKey.codec(Registries.FLUID).optionalFieldOf("tag").forGetter(CollideWithFluidsAbility::tag)
        ).apply(instance, constructor));
    }

    protected static <T extends CollideWithFluidsAbility> StreamCodec<ByteBuf, T> streamCodec(BiFunction<Value<Boolean>, Optional<TagKey<Fluid>>, T> constructor) {
        return StreamCodec.composite(
                ValueTypes.BOOLEAN.streamCodec(),
                CollideWithFluidsAbility::enabled,
                ByteBufCodecs.optional(ModCodecs.tagKeyStreamCodec(Registries.FLUID)),
                CollideWithFluidsAbility::tag,
                constructor
        );
    }

    @Override
    public boolean isNonCosmetic() {
        return enabled().get();
    }

    public Value<Boolean> enabled() {
        return enabled;
    }

    public Optional<TagKey<Fluid>> tag() {
        return tag;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (CollideWithFluidsAbility) obj;
        return Objects.equals(this.enabled, that.enabled) &&
                Objects.equals(this.tag, that.tag);
    }

    @Override
    public int hashCode() {
        return Objects.hash(enabled, tag);
    }
}
