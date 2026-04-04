package artifacts.fabric.integration;

import artifacts.config.screen.ArtifactsConfigScreen;
import artifacts.integration.ModCompat;
import artifacts.platform.PlatformServices;
import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;

public class ModMenuIntegration implements ModMenuApi {

    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        if (PlatformServices.getModList().isModLoaded(ModCompat.CLOTH_CONFIG)) {
            return parent -> new ArtifactsConfigScreen(parent).build();
        }
        return ModMenuApi.super.getModConfigScreenFactory();
    }
}
