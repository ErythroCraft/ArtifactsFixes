package artifacts.neoforge.integration.curios;

import artifacts.equipment.client.EquipmentRenderingManager;
import artifacts.integration.ModCompat;
import artifacts.mixin.accessors.client.LivingEntityRendererAccessor;
import artifacts.platform.PlatformServices;
import artifacts.registry.ModLootTables;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.world.entity.EntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import top.theillusivec4.curios.client.CuriosLayer;

import java.util.Set;

public class CuriosCompatClient {

    public static void setup(IEventBus modBus) {
        if (!PlatformServices.getModList().isModLoaded(ModCompat.CCLAYER)) {
            EquipmentRenderingManager.register(new CuriosRenderingHandler());
        }

        modBus.addListener(CuriosCompatClient::onAddLayers);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static void onAddLayers(EntityRenderersEvent.AddLayers event) {
        Set<EntityType<?>> entities = ModLootTables.ENTITY_EQUIPMENT.keySet();
        loop:
        for (EntityType<?> entity : entities) {
            EntityRenderer<?, ?> renderer = event.getRenderer(entity);
            if (renderer == null) {
                continue;
            }
            LivingEntityRenderer livingEntityRenderer = (LivingEntityRenderer<?, ?, ?>) renderer;
            for (RenderLayer<?, ?> layer : ((LivingEntityRendererAccessor<?, ?>) livingEntityRenderer).getLayers()) {
                if (layer instanceof CuriosLayer<?, ?>) {
                    continue loop;
                }
            }
            livingEntityRenderer.addLayer(new CuriosLayer<>(livingEntityRenderer, event.getContext()));
        }
    }
}
