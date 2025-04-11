package artifacts.registry;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;

import java.util.function.Consumer;

public class ModKeyMappings {

    private static final KeyMapping ACTIVATE_HELIUM_FLAMINGO = createUnboundKeyMapping("artifacts.key.helium_flamingo.activate");
    public static final KeyMapping TOGGLE_CHARM_OF_SHRINKING = createUnboundKeyMapping("artifacts.key.charm_of_shrinking.activate");
    public static final KeyMapping TOGGLE_CHARM_OF_SINKING = createUnboundKeyMapping("artifacts.key.charm_of_sinking.activate");
    public static final KeyMapping TOGGLE_NIGHT_VISION_GOGGLES = createUnboundKeyMapping("artifacts.key.night_vision_goggles.toggle");
    public static final KeyMapping TOGGLE_SCARF_OF_INVISIBILITY = createUnboundKeyMapping("artifacts.key.scarf_of_invisibility.toggle");
    public static final KeyMapping TOGGLE_UNIVERSAL_ATTRACTOR = createUnboundKeyMapping("artifacts.key.universal_attractor.toggle");

    private static KeyMapping createUnboundKeyMapping(String name) {
        return new KeyMapping(name, InputConstants.Type.KEYSYM, InputConstants.UNKNOWN.getValue(), "artifacts.key_category");
    }

    public static KeyMapping getHeliumFlamingoKey() {
        if (!ACTIVATE_HELIUM_FLAMINGO.isUnbound()) {
            return ACTIVATE_HELIUM_FLAMINGO;
        }
        return Minecraft.getInstance().options.keySprint;
    }

    public static void register(Consumer<KeyMapping> registration) {
        registration.accept(ACTIVATE_HELIUM_FLAMINGO);
        registration.accept(TOGGLE_CHARM_OF_SHRINKING);
        registration.accept(TOGGLE_CHARM_OF_SINKING);
        registration.accept(TOGGLE_NIGHT_VISION_GOGGLES);
        registration.accept(TOGGLE_SCARF_OF_INVISIBILITY);
        registration.accept(TOGGLE_UNIVERSAL_ATTRACTOR);
    }
}
