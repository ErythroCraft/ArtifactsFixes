package artifacts.neoforge.integration.curios;

import artifacts.equipment.EquipmentSlotManager;
import artifacts.event.ArtifactHooks;
import artifacts.integration.ModCompat;
import artifacts.item.WearableArtifactItem;
import artifacts.platform.PlatformServices;
import net.neoforged.neoforge.common.NeoForge;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.event.CurioChangeEvent;

public class CuriosCompat {

    public static void setup() {
        if (!PlatformServices.platformHelper.isModLoaded(ModCompat.CCLAYER)) {
            EquipmentSlotManager.register(new CuriosSlotProvider());
        }
        PlatformServices.platformHelper.addItemRegistryCallback(item -> {
            if (item instanceof WearableArtifactItem wearableArtifactItem) {
                CuriosApi.registerCurio(wearableArtifactItem, new WearableArtifactCurio(wearableArtifactItem));
            }
        });

        NeoForge.EVENT_BUS.addListener(
                (CurioChangeEvent event) -> ArtifactHooks.onItemChanged(event.getEntity(), event.getFrom(), event.getTo())
        );
    }
}
