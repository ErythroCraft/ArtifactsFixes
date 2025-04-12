package artifacts;

import artifacts.client.CloudInABottleInputHandler;
import artifacts.client.ToggleKeyHandlers;
import artifacts.client.item.ArtifactLayers;
import artifacts.client.mimic.model.MimicChestLayerModel;
import artifacts.client.mimic.model.MimicModel;
import artifacts.integration.ModCompat;
import artifacts.integration.accessories.AccessoriesCompatClient;
import artifacts.integration.trinkets.TrinketsCompatClient;
import artifacts.platform.PlatformServices;
import artifacts.registry.ModItems;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.renderer.item.ClampedItemPropertyFunction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import org.apache.logging.log4j.util.TriConsumer;

import java.util.function.BiConsumer;
import java.util.function.Supplier;

public class ArtifactsClient {

    public static void setup() {
        if (PlatformServices.platformHelper.isModLoaded(ModCompat.TRINKETS)) {
            TrinketsCompatClient.setup();
        }
        if (PlatformServices.platformHelper.isModLoaded(ModCompat.ACCESSORIES)) {
            AccessoriesCompatClient.setup();
        }
    }

    public static void onClientTick(Minecraft instance) {
        HeliumFlamingoInputEventHandler.onClientTick(instance);
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

    public static void registerItemPropertyFunctions(TriConsumer<Item, ResourceLocation, ClampedItemPropertyFunction> registration) {
        registration.accept(
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
}
