package artifacts.neoforge.event;

import artifacts.component.SwimmingHooks;
import be.florens.expandability.api.EventResult;
import be.florens.expandability.api.forge.LivingFluidCollisionEvent;
import be.florens.expandability.api.forge.PlayerSwimEvent;
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.common.NeoForge;

public class SwimmingHooksNeoForge {

    public static void register() {
        if (ModList.get().isLoaded("expandability")) {
            NeoForge.EVENT_BUS.addListener(SwimmingHooksNeoForge::onPlayerSwim);
            NeoForge.EVENT_BUS.addListener(SwimmingHooksNeoForge::onAquaDashersFluidCollision);
        }
    }

    public static void onPlayerSwim(PlayerSwimEvent event) {
        if (event.getResult() == EventResult.PASS) {
            event.setResult(SwimmingHooks.onPlayerSwim(event.getEntity()));
        }
    }

    private static void onAquaDashersFluidCollision(LivingFluidCollisionEvent event) {
        if (SwimmingHooks.onFluidCollision(event.getEntity(), event.getFluidState())) {
            event.setColliding(true);
        }
    }
}
