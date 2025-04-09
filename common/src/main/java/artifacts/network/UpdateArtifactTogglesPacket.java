package artifacts.network;

import artifacts.Artifacts;
import artifacts.ability.ArtifactAbility;
import artifacts.component.AbilityToggles;
import artifacts.platform.PlatformServices;
import artifacts.registry.ModAbilities;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

import java.util.List;

public record UpdateArtifactTogglesPacket(List<ArtifactAbility.Type<?>> toggles) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<UpdateArtifactTogglesPacket> TYPE = new CustomPacketPayload.Type<>(Artifacts.id("update_artifact_toggles"));

    public static final StreamCodec<FriendlyByteBuf, UpdateArtifactTogglesPacket> CODEC = StreamCodec.composite(
            ByteBufCodecs.<ByteBuf, ArtifactAbility.Type<?>>list().apply(
                    ResourceLocation.STREAM_CODEC.map(id -> ModAbilities.getRegistry().get(id), type -> ModAbilities.getRegistry().getKey(type))
            ),
            UpdateArtifactTogglesPacket::toggles,
            UpdateArtifactTogglesPacket::new
    );

    void apply(NetworkHandler.PayloadContext context) {
        Player player = context.player();
        if (player != null) {
            AbilityToggles abilityToggles = PlatformServices.platformHelper.getAbilityToggles(player);
            if (abilityToggles != null) {
                abilityToggles.applyToggles(toggles, context.player());
            }
        }
    }

    @Override
    public CustomPacketPayload.Type<UpdateArtifactTogglesPacket> type() {
        return TYPE;
    }
}
