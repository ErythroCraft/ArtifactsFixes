package artifacts.network;

import artifacts.Artifacts;
import artifacts.config.ConfigEntryKey;
import artifacts.config.value.ConfigValue;
import artifacts.config.value.type.ValueType;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public final class UpdateConfigValuePacket implements CustomPacketPayload {

    public static final Type<UpdateConfigValuePacket> TYPE = new Type<>(Artifacts.id("update_item_configs"));

    public static final StreamCodec<ByteBuf, UpdateConfigValuePacket> CODEC = ConfigEntryKey.STREAM_CODEC.dispatch(
            packet -> packet.key,
            id -> {
                ValueType<?, ?> type = Artifacts.CONFIG.configs.get(id.configManager()).getValues().get(id).type();
                return codecFor(type, id);
            }
    );

    private final ValueType<Object, ?> type;
    private final ConfigEntryKey key;
    private final Object value;

    @SuppressWarnings("unchecked")
    private UpdateConfigValuePacket(ValueType<?, ?> type, ConfigEntryKey key, Object value) {
        this.type = (ValueType<Object, ?>) type;
        this.key = key;
        this.value = value;
    }

    public static <T> UpdateConfigValuePacket of(ConfigValue<T> configValue) {
        return new UpdateConfigValuePacket(configValue.type(), configValue.getKey(), configValue.get());
    }

    private static <T> StreamCodec<ByteBuf, UpdateConfigValuePacket> codecFor(ValueType<T, ?> type, ConfigEntryKey key) {
        return type.valueStreamCodec().map(
                value -> new UpdateConfigValuePacket(type, key, value),
                packet -> type.cast(packet.value)
        );
    }

    void apply(NetworkHandler.PayloadContext ignored) {
        Artifacts.LOGGER.debug("Received updated config value for {} from server", key);
        Artifacts.CONFIG.configs.get(key.configManager()).getValues(type).get(key).set(type.cast(value));
    }

    @Override
    public Type<UpdateConfigValuePacket> type() {
        return TYPE;
    }
}
