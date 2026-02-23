package artifacts.integration;

import artifacts.platform.PlatformServices;
import net.minecraft.resources.Identifier;

public class ModCompat {

    public static final ModInfo NEOFORGE = new ModInfo("neoforge");

    public static final ModInfo CREEPER_OVERHAUL = new ModInfo("creeperoverhaul");
    public static final ModInfo EXPANDABILITY = new ModInfo("expandability");
    public static final ModInfo CURIOS = new ModInfo("curios");
    public static final ModInfo TRINKETS = new ModInfo("trinkets");
    public static final ModInfo ACCESSORIES = new ModInfo("accessories");
    public static final ModInfo CCLAYER = new ModInfo("cclayer");
    public static final ModInfo TCLAYER = new ModInfo("tclayer");
    public static final ModInfo LOOTR = new ModInfo("lootr");
    public static final ModInfo ORIGINS = new ModInfo("origins");
    public static final ModInfo QUARK = new ModInfo("quark");
    public static final ModInfo CLOTH_CONFIG = new ModInfo(NEOFORGE.isLoaded() ? "cloth_config" : "cloth-config");

    public record ModInfo(String modId) {

        public boolean isLoaded() {
            return PlatformServices.getModList().isModLoaded(modId);
        }

        public Identifier id(String path) {
            return Identifier.fromNamespaceAndPath(modId, path);
        }
    }
}
