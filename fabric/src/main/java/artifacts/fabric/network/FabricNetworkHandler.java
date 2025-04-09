package artifacts.fabric.network;

import artifacts.network.NetworkHandler;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.player.Player;

public class FabricNetworkHandler {
    public static void registerClientboundPayloads() {
        for (NetworkHandler.PayloadHandler<?> payloadHandler : NetworkHandler.CLIENTBOUND_HANDLERS) {
            registerClientboundPayload(payloadHandler);
        }
    }

    public static void registerServerboundPayloads() {
        for (NetworkHandler.PayloadHandler<?> payloadHandler : NetworkHandler.SERVERBOUND_HANDLERS) {
            registerServerboundPayload(payloadHandler);
        }
    }

    public static void registerServerboundReceivers() {
        for (NetworkHandler.PayloadHandler<?> payloadHandler : NetworkHandler.SERVERBOUND_HANDLERS) {
            registerServerboundReceiver(payloadHandler);
        }
    }

    private static <T extends CustomPacketPayload> void registerClientboundPayload(NetworkHandler.PayloadHandler<T> payloadHandler) {
        PayloadTypeRegistry.playS2C().register(payloadHandler.type(), payloadHandler.codec());
    }

    private static <T extends CustomPacketPayload> void registerServerboundPayload(NetworkHandler.PayloadHandler<T> payloadHandler) {
        PayloadTypeRegistry.playC2S().register(payloadHandler.type(), payloadHandler.codec());
    }

    private static <T extends CustomPacketPayload> void registerServerboundReceiver(NetworkHandler.PayloadHandler<T> payloadHandler) {
        ServerPlayNetworking.registerGlobalReceiver(payloadHandler.type(), (payload, context) ->
                payloadHandler.receiver().receive(payload, new FabricServerboundPayloadContext(context.player(), context))
        );
    }

    private record FabricServerboundPayloadContext(Player player, ServerPlayNetworking.Context context) implements NetworkHandler.PayloadContext {

        @Override
        public void queue(Runnable runnable) {
            context.server().execute(runnable);
        }
    }
}
