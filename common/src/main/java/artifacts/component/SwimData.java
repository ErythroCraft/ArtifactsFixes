package artifacts.component;

import artifacts.component.ability.SwimInAir;
import artifacts.equipment.EquipmentHelper;
import artifacts.network.NetworkHandler;
import artifacts.network.SwimPacket;
import artifacts.registry.ModDataComponents;
import artifacts.registry.ModSoundEvents;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

public class SwimData {

    protected boolean isSwimming;
    protected boolean hasTouchedWater;
    protected double swimProgress;

    public boolean isSwimming() {
        return isSwimming;
    }

    public boolean isWet() {
        return hasTouchedWater;
    }

    public double getSwimProgress() {
        return swimProgress;
    }

    public void update(Player player) {
        if (player.isInWater() || player.isInLava() || player.fallDistance > 6) {
            hasTouchedWater = true;
        } else if (player.onGround() || player.getAbilities().flying) {
            hasTouchedWater = false;
        }
        boolean shouldToggle = isSwimming ? player.onGround() : player.isUnderWater() && player.isSwimming();
        if (shouldToggle) {
            toggleSwimming(player);
            syncSwimming(player);
        }
        updateSwimProgress(player);
    }

    private void updateSwimProgress(Player player) {
        if (shouldDeplete(player)) {
            int flightDuration = SwimInAir.getFlightDuration(player);
            swimProgress += 1D / flightDuration;
            if (swimProgress >= 1) {
                swimProgress = 1;
                toggleSwimming(player);
                syncSwimming(player);
            }
        } else if (swimProgress > 0) {
            int rechargeDuration = SwimInAir.getRechargeDuration(player);
            swimProgress -= 1D / rechargeDuration;
            swimProgress = Math.max(0, swimProgress);
        }
    }

    private boolean shouldDeplete(Player player) {
        return isSwimming && !player.isCreative()
                && (!player.isUnderWater() || EquipmentHelper.hasAbilityActive(ModDataComponents.SINKING.get(), player, true));
    }

    public void toggleSwimming(Player player) {
        if (isSwimming || SwimInAir.canSwim(player)) {
            isSwimming = !isSwimming;
            if (!isSwimming && !player.level().isClientSide()) {
                if (!player.onGround() && !player.isSilent()) {
                    player.level().playSound(null, player.getX(), player.getY(), player.getZ(),
                            ModSoundEvents.POP.value(), player.getSoundSource(), 1F, 1F
                    );
                }
                EquipmentHelper.iterateAbilities(
                        ModDataComponents.SWIM_IN_AIR.get(), player, true, true,
                        (ability, stack) -> player.getCooldowns().addCooldown(stack.getItem(), Math.max(5, ability.cooldown().get() * 20))
                );
            }
        }
    }

    public void syncSwimming(Player player) {
        if (player instanceof ServerPlayer serverPlayer) {
            NetworkHandler.sendToPlayer(serverPlayer, new SwimPacket(isSwimming));
        } else {
            NetworkHandler.sendToServer(new SwimPacket(isSwimming));
        }
    }
}
