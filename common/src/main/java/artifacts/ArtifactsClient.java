package artifacts;

import artifacts.client.CloudInABottleInputHandler;
import artifacts.client.HeliumFlamingoOverlay;
import artifacts.client.ToggleKeyHandlers;
import artifacts.client.item.ArtifactLayers;
import artifacts.client.mimic.MimicModel;
import artifacts.event.SwimInAirInputHooks;
import artifacts.integration.ModCompat;
import artifacts.integration.accessories.AccessoriesCompatClient;
import artifacts.integration.trinkets.TrinketsCompatClient;
import artifacts.registry.ModItems;
import net.minecraft.client.Minecraft;

public class ArtifactsClient {

    private static final HeliumFlamingoOverlay HELIUM_FLAMINGO_OVERLAY = new HeliumFlamingoOverlay();

    public static void setup() {
        if (ModCompat.TRINKETS.isLoaded()) {
            TrinketsCompatClient.setup();
        }
        if (ModCompat.ACCESSORIES.isLoaded()) {
            AccessoriesCompatClient.setup();
        }
    }

    public static HeliumFlamingoOverlay getHeliumFlamingoOverlay() {
        return HELIUM_FLAMINGO_OVERLAY;
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

    public static void registerLayerDefinitions(ArtifactLayers.LayerRegistration registration) {
        ArtifactLayers.register(registration);
        registration.register(MimicModel.LAYER_LOCATION, MimicModel::createLayer);
        registration.register(MimicModel.CHEST_LAYER_LOCATION, MimicModel::createChestLayer);
    }
}
