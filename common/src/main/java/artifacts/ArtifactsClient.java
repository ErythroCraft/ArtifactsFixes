package artifacts;

import artifacts.client.CloudInABottleInputHandler;
import artifacts.client.ToggleKeyHandler;
import artifacts.client.item.ArtifactLayers;
import artifacts.client.mimic.model.MimicChestLayerModel;
import artifacts.client.mimic.model.MimicModel;
import artifacts.platform.PlatformServices;
import artifacts.registry.ModItems;
import artifacts.registry.ModKeyMappings;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;

import java.util.function.BiConsumer;
import java.util.function.Supplier;

public class ArtifactsClient {

    public static void init() {
        ModKeyMappings.register();

        PlatformServices.platformHelper.setupClientIntegrations();
    }

    public static void onClientStarted() {
        if (!ModItems.NIGHT_VISION_GOGGLES.isBound()) {
            Artifacts.LOGGER.error("Detected broken mod state, skipping input registration");
            return;
        }
        ToggleKeyHandler.register();
        CloudInABottleInputHandler.register();
        registerItemPropertyFunctions();
    }

    public static void registerItemPropertyFunctions() {
        ItemProperties.register(
                ModItems.UMBRELLA.value(),
                Artifacts.id("blocking"),
                (stack, level, entity, i) -> entity != null && entity.isUsingItem() && entity.getUseItem() == stack ? 1 : 0
        );
    }

    public static void registerLayerDefinitions(BiConsumer<ModelLayerLocation, Supplier<LayerDefinition>> registration) {
        ArtifactLayers.register(registration);
        registration.accept(MimicModel.LAYER_LOCATION, MimicModel::createLayer);
        registration.accept(MimicChestLayerModel.LAYER_LOCATION, MimicChestLayerModel::createLayer);
    }

    @Nullable
    public static Player getLocalPlayer() {
        return Minecraft.getInstance().player;
    }
}
