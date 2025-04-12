package artifacts.client;

import artifacts.component.ability.DoubleJumpAbility;
import artifacts.network.DoubleJumpPacket;
import artifacts.network.NetworkHandler;
import artifacts.registry.ModDataComponents;
import artifacts.util.AbilityHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;

public class CloudInABottleInputHandler {

    private static boolean canDoubleJump;
    private static boolean hasReleasedJumpKey;

    public static void onClientTick(Minecraft instance) {
        LocalPlayer player = instance.player;
        // noinspection ConstantValue
        if (player != null && player.input != null) {
            handleCloudInABottleInput(player);
        }
    }

    private static void handleCloudInABottleInput(LocalPlayer player) {
        if ((player.onGround() || player.onClimbable()) && (!player.isInWater() || AbilityHelper.hasAbilityActive(ModDataComponents.SINKING.get(), player, true))) {
            hasReleasedJumpKey = false;
            canDoubleJump = true;
        } else if (!player.input.jumping) {
            hasReleasedJumpKey = true;
        } else if (!player.getAbilities().flying && canDoubleJump && hasReleasedJumpKey) {
            canDoubleJump = false;
            if (AbilityHelper.hasAbilityActive(ModDataComponents.DOUBLE_JUMP.get(), player, true)) {
                NetworkHandler.sendToServer(new DoubleJumpPacket());
                DoubleJumpAbility.jump(player);
            }
        }
    }
}
