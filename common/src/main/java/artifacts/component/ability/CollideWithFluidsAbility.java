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
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;

import java.util.Locale;
import java.util.Optional;
import java.util.function.Predicate;

public record CollideWithFluidsAbility(Value<Boolean> enabled, Optional<TagKey<Fluid>> tag, CollisionCondition condition)
        implements EquipmentAbility {

    public static Codec<CollideWithFluidsAbility> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ValueTypes.enabledField().forGetter(CollideWithFluidsAbility::enabled),
            TagKey.codec(Registries.FLUID).optionalFieldOf("tag").forGetter(CollideWithFluidsAbility::tag),
            CollisionCondition.CODEC.optionalFieldOf("condition", CollisionCondition.ALWAYS).forGetter(CollideWithFluidsAbility::condition)
    ).apply(instance, CollideWithFluidsAbility::new));

    public static StreamCodec<ByteBuf, CollideWithFluidsAbility> STREAM_CODEC = StreamCodec.composite(
            ValueTypes.BOOLEAN.streamCodec(),
            CollideWithFluidsAbility::enabled,
            ByteBufCodecs.optional(ModCodecs.tagKeyStreamCodec(Registries.FLUID)),
            CollideWithFluidsAbility::tag,
            CollisionCondition.STREAM_CODEC,
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
        if (condition == CollisionCondition.WHILE_SNEAKING && tag().isPresent() && tag().get().equals(FluidTags.LAVA)) {
            writer.add("sneaking.lava");
        } else {
            writer.add("sprinting");
        }
    }

    public enum CollisionCondition implements StringRepresentable {
        ALWAYS(entity -> true),
        WHILE_SNEAKING(Entity::isCrouching),
        WHILE_SPRINTING(entity -> entity.isSprinting() && !entity.isUsingItem() && !entity.isCrouching());

        public static final Codec<CollisionCondition> CODEC = StringRepresentable.fromValues(CollisionCondition::values);
        public static final StreamCodec<ByteBuf, CollisionCondition> STREAM_CODEC = ByteBufCodecs.idMapper(i -> CollisionCondition.values()[i], CollisionCondition::ordinal);

        private final Predicate<LivingEntity> predicate;

        CollisionCondition(Predicate<LivingEntity> predicate) {
            this.predicate = predicate;
        }

        public boolean test(LivingEntity entity) {
            return predicate.test(entity);
        }

        @Override
        public String getSerializedName() {
            return toString();
        }

        @Override
        public String toString() {
            return name().toLowerCase(Locale.ROOT);
        }
    }
}
