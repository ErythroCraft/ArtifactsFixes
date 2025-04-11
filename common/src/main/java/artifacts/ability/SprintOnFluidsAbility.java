package artifacts.ability;

import artifacts.config.value.Value;
import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.material.Fluid;

import java.util.Optional;

public class SprintOnFluidsAbility extends CollideWithFluidsAbility implements AbilityWithTooltip {

    public static final Codec<SprintOnFluidsAbility> CODEC = codec(SprintOnFluidsAbility::new);
    public static final StreamCodec<ByteBuf, SprintOnFluidsAbility> STREAM_CODEC = streamCodec(SprintOnFluidsAbility::new);

    public SprintOnFluidsAbility(Value<Boolean> enabled, Optional<TagKey<Fluid>> tag) {
        super(enabled, tag);
    }

    @Override
    public void addToTooltip(TooltipWriter writer) {
        if (tag().isEmpty()) {
            AbilityWithTooltip.super.addToTooltip(writer);
        }
    }
}
