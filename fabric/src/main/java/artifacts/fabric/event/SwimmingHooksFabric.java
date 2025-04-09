package artifacts.fabric.event;

import artifacts.component.SwimmingHooks;
import be.florens.expandability.api.EventResult;
import be.florens.expandability.api.fabric.LivingFluidCollisionCallback;
import be.florens.expandability.api.fabric.PlayerSwimCallback;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.material.FluidState;

public class SwimmingHooksFabric {

    public static void register() {
        PlayerSwimCallback.EVENT.register(SwimmingHooksFabric::onPlayerSwim);
        LivingFluidCollisionCallback.EVENT.register(SwimmingHooksFabric::onAquaDashersFluidCollision);
    }

    private static EventResult onPlayerSwim(Player player) {
        return SwimmingHooks.onPlayerSwim(player);
    }

    private static boolean onAquaDashersFluidCollision(LivingEntity entity, FluidState fluidState) {
        return SwimmingHooks.onFluidCollision(entity, fluidState);
    }
}
