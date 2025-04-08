package artifacts.neoforge.client;

import artifacts.Artifacts;
import artifacts.integration.equipment.client.ClientEquipmentIntegrationUtils;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.neoforge.client.event.RenderArmEvent;
import net.neoforged.neoforge.common.NeoForge;

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
