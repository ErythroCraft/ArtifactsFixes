package artifacts;

import artifacts.client.CloudInABottleInputHandler;
import artifacts.client.ToggleKeyHandlers;
import artifacts.client.item.ArtifactLayers;
import artifacts.client.mimic.model.MimicChestLayerModel;
import artifacts.client.mimic.model.MimicModel;
import artifacts.event.SwimInAirInputHooks;
import artifacts.integration.ModCompat;
import artifacts.integration.accessories.AccessoriesCompatClient;
import artifacts.integration.trinkets.TrinketsCompatClient;
import artifacts.platform.PlatformServices;
import artifacts.registry.ModItems;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.builders.LayerDefinition;

import java.util.function.BiConsumer;
import java.util.function.Supplier;

// TODO fix umbrella model using vanilla `is_using_item` & `display_context` model properties
public class ArtifactsClient {

    public static void setup() {
        if (PlatformServices.getModList().isModLoaded(ModCompat.TRINKETS)) {
            TrinketsCompatClient.setup();
        }
        if (PlatformServices.getModList().isModLoaded(ModCompat.ACCESSORIES)) {
            AccessoriesCompatClient.setup();
        }
    }

    public static void onClientTick(Minecraft instance) {
        SwimInAirInputHooks.onClientTick(instance);
        CloudInABottleInputHandler.onClientTick(instance);
        ToggleKeyHandlers.onClientTick();
    }

    public static void onClientStarted() {
        if (!ModItems.NIGHT_VISION_GOGGLES.isBound()) {
            Artifacts.LOGGER.error("Detected broken mod state, skipping input registration");
            return;
        }
        ToggleKeyHandlers.init();
    }

    public static void registerLayerDefinitions(BiConsumer<ModelLayerLocation, Supplier<LayerDefinition>> registration) {
        ArtifactLayers.register(registration);
        registration.accept(MimicModel.LAYER_LOCATION, MimicModel::createLayer);
        registration.accept(MimicChestLayerModel.LAYER_LOCATION, MimicChestLayerModel::createLayer);
    }
}
