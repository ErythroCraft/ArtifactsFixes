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
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;

import java.util.Optional;

public record FluidCollision(Value<Boolean> enabled, Optional<TagKey<Fluid>> tag, AbilityCondition condition)
        implements TickingAbility {

    public static Codec<FluidCollision> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ValueTypes.enabledField().forGetter(FluidCollision::enabled),
            TagKey.codec(Registries.FLUID).optionalFieldOf("tag").forGetter(FluidCollision::tag),
            AbilityCondition.CODEC.optionalFieldOf("condition", AbilityCondition.ALWAYS).forGetter(FluidCollision::condition)
    ).apply(instance, FluidCollision::new));

    public static StreamCodec<ByteBuf, FluidCollision> STREAM_CODEC = StreamCodec.composite(
            ValueTypes.BOOLEAN.streamCodec(),
            FluidCollision::enabled,
            ByteBufCodecs.optional(ModCodecs.tagKeyStreamCodec(Registries.FLUID)),
            FluidCollision::tag,
            AbilityCondition.STREAM_CODEC,
            FluidCollision::condition,
            FluidCollision::new
    );

    @Override
    public void wornTick(LivingEntity entity, boolean isOnCooldown, boolean isDisabled) {
        FluidState fluidState = entity.getBlockStateOn().getFluidState();
        if (fluidState.is(FluidTags.LAVA) && !entity.fireImmune() && condition.test(entity)) {
            entity.hurt(entity.damageSources().hotFloor(), 1);
        }
    }

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
        } else if (condition == AbilityCondition.SPRINTING && tag().isEmpty()){
            writer.add("sprinting");
        }
    }
}
