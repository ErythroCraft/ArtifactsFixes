package artifacts.client;

import artifacts.component.ToggleIdentifier;
import artifacts.network.NetworkHandler;
import artifacts.network.ToggleKeyPressedPacket;
import artifacts.registry.ModKeyMappings;
import net.minecraft.client.KeyMapping;

import java.util.LinkedHashSet;
import java.util.Set;

public class ToggleKeyHandlers {

    private static final Set<ToggleInputListener> INPUT_HANDLERS = new LinkedHashSet<>();

    public static void init() {
        INPUT_HANDLERS.add(new ToggleInputListener(ToggleIdentifier.NIGHT_VISION_GOGGLES, ModKeyMappings.TOGGLE_NIGHT_VISION_GOGGLES));
        INPUT_HANDLERS.add(new ToggleInputListener(ToggleIdentifier.UNIVERSAL_ATTRACTOR, ModKeyMappings.TOGGLE_UNIVERSAL_ATTRACTOR));
    }

    public static void onClientTick() {
        for (ToggleInputListener inputHandler : INPUT_HANDLERS) {
            inputHandler.onClientTick();
        }
    }

    private static class ToggleInputListener {

        private final KeyMapping key;
        private final ToggleIdentifier identifier;

        private boolean wasToggleKeyDown;

        public ToggleInputListener(ToggleIdentifier identifier, KeyMapping key) {
            this.key = key;
            this.identifier = identifier;
        }

        public void onClientTick() {
            boolean isToggleKeyDown = key.isDown();
            if (isToggleKeyDown && !wasToggleKeyDown) {
                NetworkHandler.sendToServer(new ToggleKeyPressedPacket(identifier));
            }
            wasToggleKeyDown = isToggleKeyDown;
        }
    }
}
