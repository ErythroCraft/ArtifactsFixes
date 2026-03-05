package artifacts.fabric.event;

import artifacts.equipment.EquipmentSlotManager;
import artifacts.event.ArtifactHooks;
import artifacts.integration.ModCompat;
import be.florens.expandability.api.EventResult;
import be.florens.expandability.api.fabric.LivingFluidCollisionCallback;
import be.florens.expandability.api.fabric.PlayerSwimCallback;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.FluidState;

public class ArtifactHooksFabric {

    public static void register() {
        if (ModCompat.EXPANDABILITY.isLoaded()) {
            PlayerSwimCallback.EVENT.register(ArtifactHooksFabric::onPlayerSwim);
            LivingFluidCollisionCallback.EVENT.register(ArtifactHooksFabric::onAquaDashersFluidCollision);
        }
        UseItemCallback.EVENT.register(ArtifactHooksFabric::onUseItem);
    }

    private static EventResult onPlayerSwim(Player player) {
        return ArtifactHooks.onPlayerSwim(player);
    }

    private static boolean onAquaDashersFluidCollision(LivingEntity entity, FluidState fluidState) {
        return ArtifactHooks.onFluidCollision(entity, fluidState);
    }

    private static InteractionResult onUseItem(Player entity, Level level, InteractionHand hand) {
        return EquipmentSlotManager.tryEquipFromUse(entity, hand);
    }
}
