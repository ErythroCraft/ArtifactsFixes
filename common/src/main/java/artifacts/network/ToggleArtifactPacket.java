package artifacts.network;

import artifacts.Artifacts;
import artifacts.ability.ArtifactAbility;
import artifacts.component.AbilityToggles;
import artifacts.platform.PlatformServices;
import artifacts.registry.ModAbilities;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

public record ToggleArtifactPacket(ArtifactAbility.Type<?> toggle) implements CustomPacketPayload {

    public static final Type<ToggleArtifactPacket> TYPE = new Type<>(Artifacts.id("toggle_artifacts"));

    public static final StreamCodec<FriendlyByteBuf, ToggleArtifactPacket> CODEC = StreamCodec.composite(
            ResourceLocation.STREAM_CODEC.map(id -> ModAbilities.getRegistry().get(id), type -> ModAbilities.getRegistry().getKey(type)),
            ToggleArtifactPacket::toggle,
            ToggleArtifactPacket::new
    );

    void apply(NetworkHandler.PayloadContext context) {
        Player player = context.player();
        if (player != null) {
            AbilityToggles abilityToggles = PlatformServices.platformHelper.getAbilityToggles(player);
            if (abilityToggles != null) {
                abilityToggles.toggle(toggle, context.player());
            }
        }
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
