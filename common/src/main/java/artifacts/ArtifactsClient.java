package artifacts;

import artifacts.client.CloudInABottleInputHandler;
import artifacts.client.ToggleKeyHandler;
import artifacts.client.item.ArtifactLayers;
import artifacts.client.mimic.MimicRenderer;
import artifacts.client.mimic.model.MimicChestLayerModel;
import artifacts.client.mimic.model.MimicModel;
import artifacts.platform.PlatformHelper;
import artifacts.platform.PlatformServices;
import artifacts.registry.ModEntityTypes;
import artifacts.registry.ModItems;
import artifacts.registry.ModKeyMappings;
import dev.architectury.event.events.client.ClientLifecycleEvent;
import dev.architectury.registry.client.level.entity.EntityModelLayerRegistry;
import dev.architectury.registry.client.level.entity.EntityRendererRegistry;
import dev.architectury.registry.item.ItemPropertiesRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;

public class ArtifactsClient {

    public static void init() {
        ModKeyMappings.register();
        registerLayerDefinitions();
        registerRenderers();
        ClientLifecycleEvent.CLIENT_STARTED.register(clientState -> onClientStarted());

        PlatformServices.platformHelper.setupClientIntegratons();
    }

    public static void onClientStarted() {
        if (!ModItems.NIGHT_VISION_GOGGLES.isBound()) {
            Artifacts.LOGGER.error("Detected broken mod state, skipping input registration");
            return;
        }
        ToggleKeyHandler.register();
        CloudInABottleInputHandler.register();
        HeliumFlamingoInputEventHandler.register();
        ItemPropertiesRegistry.register(
                ModItems.UMBRELLA.value(),
                Artifacts.id("blocking"),
                (stack, level, entity, i) -> entity != null && entity.isUsingItem() && entity.getUseItem() == stack ? 1 : 0
        );
    }

    public static void registerLayerDefinitions() {
        ArtifactLayers.register();
        EntityModelLayerRegistry.register(MimicModel.LAYER_LOCATION, MimicModel::createLayer);
        EntityModelLayerRegistry.register(MimicChestLayerModel.LAYER_LOCATION, MimicChestLayerModel::createLayer);
    }

    public static void registerRenderers() {
        EntityRendererRegistry.register(ModEntityTypes.MIMIC::value, MimicRenderer::new);
    }

    @Nullable
    public static Player getLocalPlayer() {
        return Minecraft.getInstance().player;
    }
}
