package artifacts.fabric.mixin.client;

import artifacts.Artifacts;
import artifacts.integration.EquipmentIntegrationUtils;
import artifacts.platform.PlatformServices;
import artifacts.registry.ModDataComponents;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.entity.player.Player;
import org.apache.commons.lang3.mutable.MutableInt;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Gui.class)
public abstract class GuiMixin {

    @Final
    @Shadow
    private Minecraft minecraft;

    @Shadow
    protected abstract Player getCameraPlayer();

    @Inject(method = "renderHotbarAndDecorations", at = @At(value = "TAIL"))
    private void renderHotbarAndDecorations(GuiGraphics guiGraphics, DeltaTracker deltaTracker, CallbackInfo ci) {
        Player player = this.getCameraPlayer();
        if (!Artifacts.CONFIG.client.enableCooldownOverlay.get() || player == null) {
            return;
        }

        int y = guiGraphics.guiHeight() - 16 - 3;
        int cooldownOverlayOffset = Artifacts.CONFIG.client.cooldownOverlayOffset.get();

        final int start, step;
        if (cooldownOverlayOffset < 0) {
            step = -20;
            start = guiGraphics.guiWidth() / 2 - 91 - 16 + cooldownOverlayOffset;
        } else {
            step = 20;
            start = guiGraphics.guiWidth() / 2 + 91 + cooldownOverlayOffset;
        }

        MutableInt k = new MutableInt(0);
        EquipmentIntegrationUtils.iterateEquippedAccessories(player, stack -> {
            if (!stack.isEmpty() && stack.has(ModDataComponents.ABILITIES.value()) && player.getCooldowns().isOnCooldown(stack.getItem())) {
                int x = start + step * k.intValue();
                k.add(1);
                guiGraphics.renderItem(player, stack, x, y, k.intValue() + 1);
                guiGraphics.renderItemDecorations(minecraft.font, stack, x, y);
            }
        });
    }
}
