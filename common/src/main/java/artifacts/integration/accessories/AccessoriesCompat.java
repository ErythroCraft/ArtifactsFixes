package artifacts.integration.accessories;

import artifacts.equipment.EquipmentSlotManager;
import artifacts.event.ArtifactHooks;
import artifacts.item.WearableArtifactItem;
import artifacts.platform.PlatformServices;
import io.wispforest.accessories.api.core.AccessoryRegistry;
import io.wispforest.accessories.api.events.AccessoryChangeCallback;

public class AccessoriesCompat {

    public static void setup() {
        EquipmentSlotManager.register(new AccessoriesSlotProvider());
        PlatformServices.getPlatformHelper().addItemRegistryCallback(item -> {
            if (item instanceof WearableArtifactItem wearableArtifactItem) {
                AccessoryRegistry.register(item, new WearableArtifactAccessory(wearableArtifactItem));
            }
        });

        AccessoryChangeCallback.EVENT.register(
                (prevStack, currentStack, slotReference, slotStateChange) -> ArtifactHooks.onItemChanged(slotReference.entity(), prevStack, currentStack)
        );
    }
}
