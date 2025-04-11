package artifacts.ability;

import artifacts.config.value.Value;
import artifacts.config.value.ValueTypes;
import artifacts.util.ModCodecs;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.material.Fluid;

import java.util.List;
import java.util.Optional;

public record CollideWithFluidsAbility(Value<Boolean> enabled, Optional<TagKey<Fluid>> tag, Optional<Component> tooltip) implements EquipmentAbility {

    // TODO tooltip
    public static Codec<CollideWithFluidsAbility> codec(Component tooltip) {
        return RecordCodecBuilder.create(instance -> instance.group(
                ValueTypes.enabledField().forGetter(CollideWithFluidsAbility::enabled),
                TagKey.codec(Registries.FLUID).optionalFieldOf("tag").forGetter(CollideWithFluidsAbility::tag)
        ).apply(instance, (enabled, tag) -> new CollideWithFluidsAbility(enabled, tag, Optional.ofNullable(tooltip))));
    }

    public static StreamCodec<ByteBuf, CollideWithFluidsAbility> streamCodec(Component tooltip) {
        return StreamCodec.composite(
                ValueTypes.BOOLEAN.streamCodec(),
                CollideWithFluidsAbility::enabled,
                ByteBufCodecs.optional(ModCodecs.tagKeyStreamCodec(Registries.FLUID)),
                CollideWithFluidsAbility::tag,
                (enabled, tag) -> new CollideWithFluidsAbility(enabled, tag, Optional.ofNullable(tooltip))
        );
    }

    @Override
    public boolean isNonCosmetic() {
        return enabled().get();
    }

    @Override
    public void addAbilityTooltip(List<MutableComponent> tooltip) {
        /* TODO
        if (getType() == ModAbilities.SNEAK_ON_FLUIDS.value() && tag().isPresent() && FluidTags.LAVA.equals(tag().get())) {
            tooltip.add(tooltipLine("lava"));
        } else if (getType() == ModAbilities.SPRINT_ON_FLUIDS.value() && tag.isEmpty()) {
            ArtifactAbility.super.addAbilityTooltip(tooltip);
        }
         */
    }
}
