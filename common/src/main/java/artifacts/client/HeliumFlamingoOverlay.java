package artifacts.client;

import artifacts.Artifacts;
import artifacts.component.SwimData;
import artifacts.component.ability.SwimInAir;
import artifacts.equipment.EquipmentHelper;
import artifacts.platform.PlatformServices;
import artifacts.registry.ModDataComponents;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;

public class HeliumFlamingoOverlay {

    private static final ResourceLocation HELIUM_FLAMINGO_ICON = Artifacts.id("textures/gui/icons.png");

    public static boolean renderOverlay(int height, GuiGraphics guiGraphics, int screenWidth, int screenHeight) {
        if (!(Minecraft.getInstance().getCameraEntity() instanceof LivingEntity player)
                || !EquipmentHelper.hasAbilityActive(ModDataComponents.SWIM_IN_AIR.get(), player, false)
        ) {
            return false;
        }
        SwimData swimData = PlatformServices.platformHelper.getSwimData(player);
        if (swimData == null) {
            return false;
        }
        double progress = 1 - swimData.getSwimProgress();

        RenderSystem.enableBlend();

        int left = screenWidth / 2 + 91;
        int top = screenHeight - height;

        if (progress == 1) {
            return false;
        }

        int duration = swimData.isSwimming()
                ? SwimInAir.getFlightDuration(player)
                : SwimInAir.getRechargeDuration(player);

        int full = Mth.ceil((progress - 2D / duration) * 10);
        int partial = Mth.ceil(progress * 10) - full;

        for (int i = 0; i < full + partial; ++i) {
            guiGraphics.blit(HELIUM_FLAMINGO_ICON, left - i * 8 - 9, top, -90, (i < full ? 0 : 9), 0, 9, 9, 32, 16);
        }

        RenderSystem.disableBlend();
        return true;
    }
}
