package artifacts.client;

import artifacts.Artifacts;
import artifacts.equipment.EquipmentHelper;
import artifacts.item.WearableArtifactItem;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.entity.player.Player;
import org.apache.commons.lang3.mutable.MutableInt;

public class CooldownOverlayRenderer {

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

        EquipmentHelper.iterateEquipment(player, stack -> {
            if (!stack.isEmpty() && stack.getItem() instanceof WearableArtifactItem && player.getCooldowns().isOnCooldown(stack)) {
                int x = start + step * k.intValue();
                k.add(1);
                guiGraphics.renderItem(player, stack, x, y, k.intValue() + 1);
                guiGraphics.renderItemDecorations(Minecraft.getInstance().font, stack, x, y);
            }
        });
    }
}
