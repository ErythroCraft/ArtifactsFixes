package artifacts.config;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public record ConfigEntryKey(String configManager, String path) implements Comparable<ConfigEntryKey> {

    public static final StreamCodec<ByteBuf, ConfigEntryKey> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8, ConfigEntryKey::configManager,
            ByteBufCodecs.STRING_UTF8, ConfigEntryKey::path,
            ConfigEntryKey::new
    );

    @Override
    public String toString() {
        return configManager + '.' + path;
    }

    @Override
    public int compareTo(ConfigEntryKey other) {
        return toString().compareTo(other.toString());
    }
}
