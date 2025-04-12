package artifacts.integration.trinkets;

import artifacts.equipment.client.EquipmentRenderingManager;
import artifacts.integration.ModCompat;
import artifacts.platform.PlatformServices;

public class TrinketsCompatClient {

    public static void setup() {
        if (!PlatformServices.platformHelper.isModLoaded(ModCompat.TCLAYER)) {
            EquipmentRenderingManager.register(new TrinketsRenderingHandler());
        }
    }
}
