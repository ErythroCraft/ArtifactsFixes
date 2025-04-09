package artifacts.neoforge.network;

import artifacts.network.NetworkHandler;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

public class NeoForgeNetworkHandler {

    public static void registerPayloadHandlers(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar("1");
        for (NetworkHandler.PayloadHandler<?> payloadHandler : NetworkHandler.SERVERBOUND_HANDLERS) {
            registerServerbound(registrar, payloadHandler);
        }
        for (NetworkHandler.PayloadHandler<?> payloadHandler : NetworkHandler.CLIENTBOUND_HANDLERS) {
            registerClientbound(registrar, payloadHandler);
        }
    }

    private static <T extends CustomPacketPayload> void registerServerbound(PayloadRegistrar registrar, NetworkHandler.PayloadHandler<T> payloadHandler) {
        @SuppressWarnings("unchecked")
        StreamCodec<? super FriendlyByteBuf, T> codec = ((StreamCodec<? super FriendlyByteBuf, T>) payloadHandler.codec());
        registrar.playToServer(
                payloadHandler.type(), codec,
                (arg, context) -> payloadHandler.receiver().receive(arg, new NeoForgePayloadContext(context.player(), context))
        );
    }

    private static <T extends CustomPacketPayload> void registerClientbound(PayloadRegistrar registrar, NetworkHandler.PayloadHandler<T> payloadHandler) {
        @SuppressWarnings("unchecked")
        StreamCodec<? super FriendlyByteBuf, T> codec = ((StreamCodec<? super FriendlyByteBuf, T>) payloadHandler.codec());
        registrar.playToClient(
                payloadHandler.type(), codec,
                (arg, context) -> payloadHandler.receiver().receive(arg, new NeoForgePayloadContext(context.player(), context))
        );
    }

    private record NeoForgePayloadContext(Player player, IPayloadContext context) implements NetworkHandler.PayloadContext {

        @Override
        public void queue(Runnable runnable) {
            context.enqueueWork(runnable);
        }
    }
}
