package artifacts.fabric;

import artifacts.ArtifactsClient;
import artifacts.client.mimic.MimicRenderer;
import artifacts.event.ArtifactHooks;
import artifacts.fabric.client.UmbrellaModelLoadingPlugin;
import artifacts.fabric.network.FabricClientNetworkHandler;
import artifacts.integration.ModCompat;
import artifacts.integration.impl.trinkets.TrinketRenderersReloadHook;
import artifacts.registry.ModEntityTypes;
import artifacts.registry.ModKeyMappings;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientEntityEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.model.loading.v1.ModelLoadingPlugin;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.ResourceManager;

public class ArtifactsFabricClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        ArtifactsClient.init();
        ArtifactsClient.onClientStarted();
        FabricClientNetworkHandler.registerClientboundReceivers();
        ModelLoadingPlugin.register(new UmbrellaModelLoadingPlugin());
        ArtifactsClient.registerLayerDefinitions((location, layerDefinition) -> EntityModelLayerRegistry.registerModelLayer(location, layerDefinition::get));
        ArtifactsClient.registerItemPropertyFunctions(ItemProperties::register);
        ClientTickEvents.END_CLIENT_TICK.register(ArtifactsClient::onClientTick);
        EntityRendererRegistry.register(ModEntityTypes.MIMIC.get(), MimicRenderer::new);
        ModKeyMappings.register(KeyBindingHelper::registerKeyBinding);

        ClientEntityEvents.ENTITY_LOAD.register((entity, level) -> ArtifactHooks.onEntityAdded(entity));

        if (FabricLoader.getInstance().isModLoaded(ModCompat.TRINKETS)) {
            ResourceManagerHelper.get(PackType.CLIENT_RESOURCES).registerReloadListener(new SimpleSynchronousResourceReloadListener() {
                @Override
                public ResourceLocation getFabricId() {
                    return TrinketRenderersReloadHook.ID;
                }

                @Override
                public void onResourceManagerReload(ResourceManager resourceManager) {
                    TrinketRenderersReloadHook.INSTANCE.onResourceManagerReload(resourceManager);
                }
            });
        }
    }
}
