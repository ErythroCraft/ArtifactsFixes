package artifacts.integration;

import artifacts.platform.PlatformServices;
import net.minecraft.resources.Identifier;

public record CompatHandler(String modId) {

    public boolean isLoaded() {
        return PlatformServices.platformHelper.isModLoaded(modId);
    }

    public Identifier id(String path) {
        return Identifier.fromNamespaceAndPath(modId, path);
    }
}
