package artifacts.fabric;

import artifacts.ArtifactsClient;
import artifacts.fabric.client.UmbrellaModelLoadingPlugin;
import artifacts.integration.impl.trinkets.TrinketRenderersReloadHook;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.model.loading.v1.ModelLoadingPlugin;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.ResourceManager;

public class ArtifactsFabricClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        ArtifactsClient.init();

        if (FabricLoader.getInstance().isModLoaded("trinkets")) {
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
        ModelLoadingPlugin.register(new UmbrellaModelLoadingPlugin());
    }
}
