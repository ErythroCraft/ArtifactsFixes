package artifacts.neoforge.client;

import artifacts.Artifacts;
import artifacts.client.item.renderer.GloveArtifactRenderer;
import artifacts.integration.client.ClientEquipmentIntegration;
import artifacts.integration.client.ClientEquipmentIntegrationUtils;
import artifacts.item.WearableArtifactItem;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.neoforge.client.event.RenderArmEvent;
import net.neoforged.neoforge.common.NeoForge;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.type.inventory.ICurioStacksHandler;
import top.theillusivec4.curios.api.type.inventory.IDynamicStackHandler;

public abstract class ArmRenderHandler {

    public static void setup() {
        NeoForge.EVENT_BUS.addListener(EventPriority.LOW, ArmRenderHandler::onRenderArm);
    }

    public static void onRenderArm(RenderArmEvent event) {
        if (!Artifacts.CONFIG.client.showFirstPersonGloves.get() || event.isCanceled()) {
            return;
        }

        ClientEquipmentIntegrationUtils.renderArm(event.getPoseStack(), event.getMultiBufferSource(), event.getPackedLight(), event.getPlayer(), event.getArm());
    }
}
