package artifacts.network;

import artifacts.Artifacts;
import artifacts.component.SwimData;
import artifacts.platform.PlatformServices;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.player.Player;

public record SwimPacket(boolean shouldSwim) implements CustomPacketPayload {

    public static final Type<SwimPacket> TYPE = new Type<>(Artifacts.id("update_swimming"));

    public static final StreamCodec<FriendlyByteBuf, SwimPacket> CODEC = StreamCodec.composite(
            ByteBufCodecs.BOOL,
            SwimPacket::shouldSwim,
            SwimPacket::new
    );

    void apply(NetworkHandler.PayloadContext context) {
        Player player = context.player();
        if (player != null) {
            context.queue(() -> {
                SwimData swimData = PlatformServices.platformHelper.getSwimData(player);
                if (swimData != null) {
                    swimData.setSwimming(player, shouldSwim);
                }
            });
        }
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
