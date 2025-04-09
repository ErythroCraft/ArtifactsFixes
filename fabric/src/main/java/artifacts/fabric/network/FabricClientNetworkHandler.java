package artifacts.fabric.network;

import artifacts.network.NetworkHandler;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.player.Player;

public class FabricClientNetworkHandler {

    public static void registerClientboundReceivers() {
        for (NetworkHandler.PayloadHandler<?> payloadHandler : NetworkHandler.CLIENTBOUND_HANDLERS) {
            registerClientboundReceiver(payloadHandler);
        }
    }

    private static <T extends CustomPacketPayload> void registerClientboundReceiver(NetworkHandler.PayloadHandler<T> payloadHandler) {
        ClientPlayNetworking.registerGlobalReceiver(payloadHandler.type(), (payload, context) ->
                payloadHandler.receiver().receive(payload, new FabricClientboundPayloadContext(context.player(), context))
        );
    }

    private record FabricClientboundPayloadContext(Player player, ClientPlayNetworking.Context context) implements NetworkHandler.PayloadContext {

        @Override
        public void queue(Runnable runnable) {
            context.client().execute(runnable);
        }
    }
}
