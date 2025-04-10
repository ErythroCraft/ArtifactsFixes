package artifacts.component;

import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ByIdMap;
import net.minecraft.util.StringRepresentable;

import java.util.function.IntFunction;

public enum ToggleIdentifier implements StringRepresentable {
    NIGHT_VISION_GOGGLES(0, "night_vision_goggles"),
    UNIVERSAL_ATTRACTOR(1, "universal_attractor");

    private final int id;
    private final String name;

    ToggleIdentifier(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public static final Codec<ToggleIdentifier> CODEC = StringRepresentable.fromValues(ToggleIdentifier::values);
    public static final IntFunction<ToggleIdentifier> BY_ID = ByIdMap.continuous(toggleIdentifier -> toggleIdentifier.id, values(), ByIdMap.OutOfBoundsStrategy.ZERO);
    public static final StreamCodec<ByteBuf, ToggleIdentifier> STREAM_CODEC = ByteBufCodecs.idMapper(BY_ID, rarity -> rarity.id);

    @Override
    public String getSerializedName() {
        return toString();
    }

    @Override
    public String toString() {
        return name;
    }
}
