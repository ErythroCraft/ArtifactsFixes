package artifacts.neoforge;

import artifacts.Artifacts;
import artifacts.ArtifactsClient;
import artifacts.client.item.ArtifactRenderers;
import artifacts.client.mimic.MimicRenderer;
import artifacts.integration.ModCompat;
import artifacts.integration.equipment.client.ClientEquipmentIntegrationUtils;
import artifacts.integration.impl.trinkets.TrinketRenderersReloadHook;
import artifacts.neoforge.client.ArmRenderHandler;
import artifacts.neoforge.client.ArtifactCooldownOverlayRenderer;
import artifacts.neoforge.client.HeliumFlamingoOverlayRenderer;
import artifacts.neoforge.client.UmbrellaArmPoseHandler;
import artifacts.neoforge.integration.curios.CuriosClientIntegration;
import artifacts.registry.ModEntityTypes;
import artifacts.registry.ModItems;
import artifacts.registry.ModKeyMappings;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.item.ItemProperties;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModList;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.AddReloadListenerEvent;

public class ArtifactsNeoForgeClient {

    public ArtifactsNeoForgeClient(IEventBus modBus) {
        ArtifactsClient.init();

        modBus.addListener(this::onClientSetup);
        modBus.addListener(this::registerGuiLayers);
        modBus.addListener(this::registerLayerDefinitions);
        modBus.addListener(this::registerEntityRenderers);
        modBus.addListener((RegisterKeyMappingsEvent event) -> ModKeyMappings.register(event::register));

        boolean curiosLoaded = ClientEquipmentIntegrationUtils.hasIntegration(ModCompat.CURIOS);

        if (ClientEquipmentIntegrationUtils.hasIntegration(ModCompat.TRINKETS) || curiosLoaded) {
            ArmRenderHandler.setup();
        }
        if (curiosLoaded) {
            modBus.addListener(CuriosClientIntegration::onAddLayers);
        }

        NeoForge.EVENT_BUS.addListener(this::addReloadListeners);

        NeoForge.EVENT_BUS.addListener((ClientTickEvent.Post event) -> ArtifactsClient.onClientTick(Minecraft.getInstance()));
    }

    public void onClientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(
                () -> ItemProperties.register(
                        ModItems.UMBRELLA.value(),
                        Artifacts.id("blocking"),
                        (stack, level, entity, seed) -> entity != null && entity.isUsingItem() && entity.getUseItem() == stack ? 1 : 0
                )
        );
        event.enqueueWork(ArtifactsClient::onClientStarted);
        ArtifactRenderers.register();
        UmbrellaArmPoseHandler.setup();
        ArtifactsClient.registerItemPropertyFunctions(ItemProperties::register);
    }

    public void addReloadListeners(AddReloadListenerEvent event) {
        if (ModList.get().isLoaded(ModCompat.TRINKETS)) {
            event.addListener(TrinketRenderersReloadHook.INSTANCE);
        }
    }

    public void registerGuiLayers(RegisterGuiLayersEvent event) {
        event.registerAbove(VanillaGuiLayers.AIR_LEVEL, Artifacts.id("helium_flamingo_charge"), HeliumFlamingoOverlayRenderer::render);
        event.registerAbove(VanillaGuiLayers.HOTBAR, Artifacts.id("artifact_cooldowns"), ArtifactCooldownOverlayRenderer::render);
    }

    public void registerLayerDefinitions(EntityRenderersEvent.RegisterLayerDefinitions event) {
        ArtifactsClient.registerLayerDefinitions(event::registerLayerDefinition);
    }

    public void registerEntityRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(ModEntityTypes.MIMIC.get(), MimicRenderer::new);
    }
}
