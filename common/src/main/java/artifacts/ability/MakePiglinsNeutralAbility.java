package artifacts.ability;

import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;

public record MakePiglinsNeutralAbility() implements TooltiplessAbility {

    public static final MakePiglinsNeutralAbility INSTANCE = new MakePiglinsNeutralAbility();

    public static final Codec<MakePiglinsNeutralAbility> CODEC = Codec.unit(INSTANCE);
    public static final StreamCodec<ByteBuf, MakePiglinsNeutralAbility> STREAM_CODEC = StreamCodec.unit(INSTANCE);

    @Override
    public boolean isNonCosmetic() {
        return true;
    }
}
