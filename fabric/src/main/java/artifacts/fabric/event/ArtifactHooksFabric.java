package artifacts.fabric.event;

import artifacts.event.ArtifactHooks;
import be.florens.expandability.api.EventResult;
import be.florens.expandability.api.fabric.LivingFluidCollisionCallback;
import be.florens.expandability.api.fabric.PlayerSwimCallback;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.material.FluidState;

public class ArtifactHooksFabric {

    public static void register() {
        PlayerSwimCallback.EVENT.register(ArtifactHooksFabric::onPlayerSwim);
        LivingFluidCollisionCallback.EVENT.register(ArtifactHooksFabric::onAquaDashersFluidCollision);
    }

    private static EventResult onPlayerSwim(Player player) {
        return ArtifactHooks.onPlayerSwim(player);
    }

    private static boolean onAquaDashersFluidCollision(LivingEntity entity, FluidState fluidState) {
        return ArtifactHooks.onFluidCollision(entity, fluidState);
    }
}
