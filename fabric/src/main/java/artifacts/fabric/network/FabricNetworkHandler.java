package artifacts.fabric.network;

import artifacts.network.ConfigurationNetworkHandler;
import artifacts.network.NetworkHandler;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.player.Player;

public class FabricNetworkHandler {

    public static void register() {
        // clientbound payloads
        for (NetworkHandler.PayloadHandler<?> payloadHandler : NetworkHandler.CLIENTBOUND_HANDLERS) {
            registerClientboundPayload(payloadHandler);
        }
        // clientbound configuration payloads
        for (ConfigurationNetworkHandler.PayloadHandler<?> payloadHandler : ConfigurationNetworkHandler.CLIENTBOUND_HANDLERS) {
            registerClientboundPayload(payloadHandler);
        }
        // serverbound payloads
        for (NetworkHandler.PayloadHandler<?> payloadHandler : NetworkHandler.SERVERBOUND_HANDLERS) {
            registerServerboundPayload(payloadHandler);
        }
        // serverbound receivers
        for (NetworkHandler.PayloadHandler<?> payloadHandler : NetworkHandler.SERVERBOUND_HANDLERS) {
            registerServerboundReceiver(payloadHandler);
        }
    }

    private static <T extends CustomPacketPayload> void registerClientboundPayload(NetworkHandler.PayloadHandler<T> payloadHandler) {
        PayloadTypeRegistry.clientboundPlay().register(payloadHandler.type(), payloadHandler.codec());
    }

    private static <T extends CustomPacketPayload> void registerClientboundPayload(ConfigurationNetworkHandler.PayloadHandler<T> payloadHandler) {
        PayloadTypeRegistry.clientboundConfiguration().register(payloadHandler.type(), payloadHandler.codec());
    }

    private static <T extends CustomPacketPayload> void registerServerboundPayload(NetworkHandler.PayloadHandler<T> payloadHandler) {
        PayloadTypeRegistry.serverboundPlay().register(payloadHandler.type(), payloadHandler.codec());
    }

    private static <T extends CustomPacketPayload> void registerServerboundReceiver(NetworkHandler.PayloadHandler<T> payloadHandler) {
        ServerPlayNetworking.registerGlobalReceiver(payloadHandler.type(), (payload, context) ->
                payloadHandler.receiver().receive(payload, new PayloadContext(context.player(), context))
        );
    }

    private record PayloadContext(Player player, ServerPlayNetworking.Context context) implements NetworkHandler.PayloadContext {

        @Override
        public void queue(Runnable runnable) {
            context.server().execute(runnable);
        }
    }
}
