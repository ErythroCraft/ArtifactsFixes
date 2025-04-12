package artifacts.integration.trinkets;

import artifacts.client.item.ArtifactRenderers;
import artifacts.integration.ModCompat;
import artifacts.platform.PlatformServices;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;

public class ArtifactRendererReloadListener implements ResourceManagerReloadListener {

    @Override
    public void onResourceManagerReload(ResourceManager resourceManager) {
        // Curios & Accessories reload the renderers when client resources are reloaded, Trinkets does not
        if (PlatformServices.platformHelper.isModLoaded(ModCompat.TRINKETS)) {
            ArtifactRenderers.register();
        }
    }
}
