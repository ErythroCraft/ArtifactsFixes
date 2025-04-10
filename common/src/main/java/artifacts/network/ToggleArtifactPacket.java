package artifacts.network;

import artifacts.Artifacts;
import artifacts.ability.ArtifactAbility;
import artifacts.component.AbilityToggles;
import artifacts.platform.PlatformServices;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

public record ToggleArtifactPacket(DataComponentType<? extends ArtifactAbility> toggle) implements CustomPacketPayload {

    public static final Type<ToggleArtifactPacket> TYPE = new Type<>(Artifacts.id("toggle_artifacts"));

    @SuppressWarnings("unchecked")
    public static final StreamCodec<FriendlyByteBuf, ToggleArtifactPacket> CODEC = StreamCodec.composite(
            ResourceLocation.STREAM_CODEC.map(
                    id -> (DataComponentType<? extends ArtifactAbility>) BuiltInRegistries.DATA_COMPONENT_TYPE.get(id),
                    BuiltInRegistries.DATA_COMPONENT_TYPE::getKey
            ),
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
