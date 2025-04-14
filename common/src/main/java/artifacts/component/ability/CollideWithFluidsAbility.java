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
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;

import java.util.Optional;

public record CollideWithFluidsAbility(Value<Boolean> enabled, Optional<TagKey<Fluid>> tag, AbilityCondition condition)
        implements EquipmentAbility {

    public static Codec<CollideWithFluidsAbility> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ValueTypes.enabledField().forGetter(CollideWithFluidsAbility::enabled),
            TagKey.codec(Registries.FLUID).optionalFieldOf("tag").forGetter(CollideWithFluidsAbility::tag),
            AbilityCondition.CODEC.optionalFieldOf("condition", AbilityCondition.ALWAYS).forGetter(CollideWithFluidsAbility::condition)
    ).apply(instance, CollideWithFluidsAbility::new));

    public static StreamCodec<ByteBuf, CollideWithFluidsAbility> STREAM_CODEC = StreamCodec.composite(
            ValueTypes.BOOLEAN.streamCodec(),
            CollideWithFluidsAbility::enabled,
            ByteBufCodecs.optional(ModCodecs.tagKeyStreamCodec(Registries.FLUID)),
            CollideWithFluidsAbility::tag,
            AbilityCondition.STREAM_CODEC,
            CollideWithFluidsAbility::condition,
            CollideWithFluidsAbility::new
    );

    @Override
    public boolean isNonCosmetic() {
        return enabled().get();
    }

    public boolean matchesFluid(FluidState fluidState) {
        return tag().isEmpty() || fluidState.is(tag().get());
    }

    @Override
    public void addToTooltip(TooltipWriter writer) {
        if (condition == AbilityCondition.SNEAKING && tag().isPresent() && tag().get().equals(FluidTags.LAVA)) {
            writer.add("sneaking.lava");
        } else {
            writer.add("sprinting");
        }
    }
}
