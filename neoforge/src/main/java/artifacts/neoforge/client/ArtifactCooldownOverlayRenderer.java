package artifacts.neoforge.client;

import artifacts.Artifacts;
import artifacts.integration.EquipmentIntegrationUtils;
import artifacts.item.WearableArtifactItem;
import artifacts.platform.PlatformServices;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.entity.player.Player;
import org.apache.commons.lang3.mutable.MutableInt;

public class ArtifactCooldownOverlayRenderer {

    @SuppressWarnings("unused")
    public static void render(GuiGraphics guiGraphics, DeltaTracker deltaTracker) {
        if (!Artifacts.CONFIG.client.enableCooldownOverlay.get() || !(Minecraft.getInstance().getCameraEntity() instanceof Player player)) {
            return;
        }

        int y = guiGraphics.guiHeight() - 16 - 3;
        int cooldownOverlayOffset = Artifacts.CONFIG.client.cooldownOverlayOffset.get();

        final int step, start;
        if (cooldownOverlayOffset < 0) {
            step = -20;
            start = guiGraphics.guiWidth() / 2 - 91 - 16 + cooldownOverlayOffset;
        } else {
            step = 20;
            start = guiGraphics.guiWidth() / 2 + 91 + cooldownOverlayOffset;
        }

        MutableInt k = new MutableInt(0);

        EquipmentIntegrationUtils.iterateEquippedAccessories(player, stack -> {
            if (!stack.isEmpty() && stack.getItem() instanceof WearableArtifactItem && player.getCooldowns().isOnCooldown(stack.getItem())) {
                int x = start + step * k.intValue();
                k.add(1);
                guiGraphics.renderItem(player, stack, x, y, k.intValue() + 1);
                guiGraphics.renderItemDecorations(Minecraft.getInstance().font, stack, x, y);
            }
        });
    }
}
