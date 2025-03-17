package artifacts.integration.impl.trinkets;

import artifacts.Artifacts;
import artifacts.client.item.ArtifactRenderers;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;

public class TrinketRenderersReloadHook implements ResourceManagerReloadListener {

    public static final TrinketRenderersReloadHook INSTANCE = new TrinketRenderersReloadHook();

    public static final ResourceLocation ID = Artifacts.id("trinket_renderers");

    private TrinketRenderersReloadHook(){}

    @Override
    public void onResourceManagerReload(ResourceManager resourceManager) {
        ArtifactRenderers.register();
    }
}
