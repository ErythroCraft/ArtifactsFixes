package artifacts.config;

import java.util.function.Supplier;

public class ClientConfig extends ConfigManager {

    public final Supplier<Boolean> showFirstPersonGloves = defineBool("showFirstPersonGloves", true, false,
            "Whether models for gloves are shown in first person");
    public final Supplier<Boolean> showTooltips = defineBool("showTooltips", true, false,
            "Whether artifacts have tooltips explaining their effects");
    public final Supplier<Boolean> useModdedMimicTextures = defineBool("useModdedMimicTextures", true, false,
            "Whether mimics can use textures from Lootr or Quark");
    public final Supplier<Boolean> enableCooldownOverlay = defineBool("enableCooldownOverlay", true, false,
            "Whether artifacts on cooldown should be displayed next to the hotbar");
    public final Supplier<Integer> cooldownOverlayOffset = defineInt("cooldownOverlayOffset", 10,
            "Location of the artifact cooldown gui element",
            "Distance from the hotbar measured in pixels",
            "Negative values place the element left of the hotbar");
    public final Supplier<Integer> heliumFlamingoOverlayOffset = defineInt("heliumFlamingoOverlayOffset", 0,
            "Controls the vertical position of the Helium Flamingo's charge meter");

    protected ClientConfig() {
        super("client");
    }
}
