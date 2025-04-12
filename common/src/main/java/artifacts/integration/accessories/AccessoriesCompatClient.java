package artifacts.integration.accessories;

import artifacts.equipment.client.EquipmentRenderingManager;
import artifacts.integration.ModCompat;
import artifacts.platform.PlatformServices;

public class AccessoriesCompatClient {

    public static void setup() {
        if (PlatformServices.platformHelper.isModLoaded(ModCompat.ACCESSORIES)) {
            EquipmentRenderingManager.register(new AccessoriesRenderingHandler());
        }
    }
}
