package artifacts.component.ability;

import artifacts.config.value.Value;
import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.material.Fluid;

import java.util.Optional;

public class SneakOnFluidsAbility extends CollideWithFluidsAbility implements EquipmentAbility {

    public static final Codec<SneakOnFluidsAbility> CODEC = codec(SneakOnFluidsAbility::new);
    public static final StreamCodec<ByteBuf, SneakOnFluidsAbility> STREAM_CODEC = streamCodec(SneakOnFluidsAbility::new);

    public SneakOnFluidsAbility(Value<Boolean> enabled, Optional<TagKey<Fluid>> tag) {
        super(enabled, tag);
    }

    @Override
    public void addToTooltip(TooltipWriter writer) {
        if (tag().isPresent() && FluidTags.LAVA.equals(tag().get())) {
            writer.add("lava");
        }
    }
}
