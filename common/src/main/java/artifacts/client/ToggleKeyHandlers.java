package artifacts.client;

import artifacts.ability.ArtifactAbility;
import artifacts.component.AbilityToggles;
import artifacts.network.NetworkHandler;
import artifacts.network.ToggleArtifactPacket;
import artifacts.platform.PlatformServices;
import artifacts.registry.ModAbilities;
import artifacts.registry.ModKeyMappings;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ToggleKeyHandlers {

    private static final Map<ArtifactAbility.Type<?>, KeyMapping> TOGGLE_KEY_MAPPINGS = new HashMap<>();
    private static final List<ToggleInputHandler> INPUT_HANDLERS = new ArrayList<>();

    public static void init() {
        addToggleInputHandler(ModAbilities.NIGHT_VISION.value(), ModKeyMappings.TOGGLE_NIGHT_VISION_GOGGLES);
        addToggleInputHandler(ModAbilities.ATTRACT_ITEMS.value(), ModKeyMappings.TOGGLE_UNIVERSAL_ATTRACTOR);
    }

    public static void onClientTick() {
        for (ToggleInputHandler inputHandler : INPUT_HANDLERS) {
            inputHandler.onClientTick();
        }
    }

    public static KeyMapping getToggleKey(ArtifactAbility.Type<?> ability) {
        return TOGGLE_KEY_MAPPINGS.get(ability);
    }

    private static void addToggleInputHandler(ArtifactAbility.Type<?> ability, KeyMapping toggleKey) {
        TOGGLE_KEY_MAPPINGS.put(ability, toggleKey);
        ToggleInputHandler handler = new ToggleInputHandler(ability);
        INPUT_HANDLERS.add(handler);
    }

    private static class ToggleInputHandler {

        private boolean wasToggleKeyDown;
        private final ArtifactAbility.Type<?> ability;

        public ToggleInputHandler(ArtifactAbility.Type<?> ability) {
            this.ability = ability;
        }

        public void onClientTick() {
            boolean isToggleKeyDown = getToggleKey(ability).isDown();
            if (isToggleKeyDown && !wasToggleKeyDown) {
                AbilityToggles abilityToggles = PlatformServices.platformHelper.getAbilityToggles(Minecraft.getInstance().player);
                if (abilityToggles != null) {
                    abilityToggles.toggle(ability, Minecraft.getInstance().player);
                    NetworkHandler.sendToServer(new ToggleArtifactPacket(ability));
                }
            }
            wasToggleKeyDown = isToggleKeyDown;
        }
    }
}
