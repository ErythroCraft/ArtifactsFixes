package artifacts.neoforge;

import artifacts.Artifacts;
import artifacts.ArtifactsClient;
import artifacts.client.item.ArtifactRenderers;
import artifacts.integration.EquipmentIntegrationConstants;
import artifacts.integration.client.ClientEquipmentIntegrationUtils;
import artifacts.integration.impl.trinkets.TrinketRenderersReloadHook;
import artifacts.neoforge.client.ArmRenderHandler;
import artifacts.neoforge.client.ArtifactCooldownOverlayRenderer;
import artifacts.neoforge.client.HeliumFlamingoOverlayRenderer;
import artifacts.neoforge.client.UmbrellaArmPoseHandler;
import artifacts.neoforge.integration.curios.CuriosClientIntegration;
import artifacts.registry.ModItems;
import net.minecraft.client.renderer.item.ItemProperties;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModList;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.AddReloadListenerEvent;

public class ArtifactsNeoForgeClient {

    public ArtifactsNeoForgeClient(IEventBus modBus) {
        ArtifactsClient.init();

        modBus.addListener(this::onClientSetup);
        modBus.addListener(this::onRegisterGuiOverlays);

        boolean curiosLoaded = ClientEquipmentIntegrationUtils.hasIntegration(EquipmentIntegrationConstants.CURIOS);

        if (ClientEquipmentIntegrationUtils.hasIntegration(EquipmentIntegrationConstants.TRINKETS) || curiosLoaded) {
            ArmRenderHandler.setup();
        }

        if (curiosLoaded) {
            modBus.addListener(CuriosClientIntegration::onAddLayers);
        }

        NeoForge.EVENT_BUS.addListener((AddReloadListenerEvent event) -> {
            if (ModList.get().isLoaded("trinkets")) {
                event.addListener(TrinketRenderersReloadHook.INSTANCE);
            }
        });
    }

    public void onClientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(
                () -> ItemProperties.register(
                        ModItems.UMBRELLA.value(),
                        Artifacts.id("blocking"),
                        (stack, level, entity, seed) -> entity != null && entity.isUsingItem() && entity.getUseItem() == stack ? 1 : 0
                )
        );
        ArtifactRenderers.register();
        UmbrellaArmPoseHandler.setup();
    }

    public void onRegisterGuiOverlays(RegisterGuiLayersEvent event) {
        event.registerAbove(VanillaGuiLayers.AIR_LEVEL, Artifacts.id("helium_flamingo_charge"), HeliumFlamingoOverlayRenderer::render);
        event.registerAbove(VanillaGuiLayers.HOTBAR, Artifacts.id("artifact_cooldowns"), ArtifactCooldownOverlayRenderer::render);
    }
}
