package artifacts.integration;

import artifacts.platform.PlatformServices;

public class ModCompat {

    public static final String CURIOS = "curios";
    public static final String TRINKETS = "trinkets";
    public static final String ACCESSORIES = "accessories";
    public static final String CCLAYER = "cclayer";
    public static final String TCLAYER = "tclayer";
    public static final String LOOTR = "lootr";
    public static final String ORIGINS = "origins";
    public static final String QUARK = "quark";
    public static final String CLOTH_CONFIG = PlatformServices.getModList().isModLoaded("neoforge") ? "cloth_config" : "cloth-config";

}
