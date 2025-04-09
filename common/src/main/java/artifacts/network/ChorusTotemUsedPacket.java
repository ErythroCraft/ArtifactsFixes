package artifacts.network;

import artifacts.Artifacts;
import artifacts.ability.TeleportOnDeathAbility;
import artifacts.registry.ModItems;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public record ChorusTotemUsedPacket() implements CustomPacketPayload {

    public static final Type<ChorusTotemUsedPacket> TYPE = new Type<>(Artifacts.id("chorus_totem_used"));

    public static final StreamCodec<FriendlyByteBuf, ChorusTotemUsedPacket> CODEC = StreamCodec.unit(new ChorusTotemUsedPacket());

    void apply(NetworkHandler.PayloadContext context) {
        Player player = context.player();
        ItemStack totem = TeleportOnDeathAbility.findTotem(player);
        if (totem.isEmpty()) {
            totem = new ItemStack(ModItems.CHORUS_TOTEM.value());
        }
        Minecraft.getInstance().gameRenderer.displayItemActivation(totem);
        player.level().playSound(context.player(), context.player(), SoundEvents.TOTEM_USE, SoundSource.PLAYERS, 1, 1);
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
