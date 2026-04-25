package artifacts.network;

import artifacts.Artifacts;
import artifacts.config.value.ConfigValue;
import artifacts.config.value.type.ValueType;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public final class UpdateItemConfigPacket implements CustomPacketPayload {

    public static final Type<UpdateItemConfigPacket> TYPE = new Type<>(Artifacts.id("update_item_configs"));

    public static final StreamCodec<ByteBuf, UpdateItemConfigPacket> CODEC = ByteBufCodecs.STRING_UTF8.dispatch(
            packet -> packet.id,
            id -> {
                ValueType<?, ?> type = Artifacts.CONFIG.items.getValues().get(id).type();
                return codecFor(type, id);
            }
    );

    private final ValueType<Object, ?> type;
    private final String id;
    private final Object value;

    @SuppressWarnings("unchecked")
    private UpdateItemConfigPacket(ValueType<?, ?> type, String id, Object value) {
        this.type = (ValueType<Object, ?>) type;
        this.id = id;
        this.value = value;
    }

    public static <T> UpdateItemConfigPacket of(ConfigValue<T> configValue) {
        return new UpdateItemConfigPacket(configValue.type(), configValue.getId(), configValue.get());
    }

    private static <T> StreamCodec<ByteBuf, UpdateItemConfigPacket> codecFor(ValueType<T, ?> type, String id) {
        return type.valueStreamCodec().map(
                value -> new UpdateItemConfigPacket(type, id, value),
                packet -> type.cast(packet.value)
        );
    }

    void apply(NetworkHandler.PayloadContext ignored) {
        Artifacts.LOGGER.debug("Received updated config value for {} from server", id);
        Artifacts.CONFIG.items.getValues(type).get(id).set(type.cast(value));
    }

    @Override
    public Type<UpdateItemConfigPacket> type() {
        return TYPE;
    }
}
