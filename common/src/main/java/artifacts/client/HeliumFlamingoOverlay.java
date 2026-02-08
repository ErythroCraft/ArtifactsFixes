package artifacts.client;

import artifacts.Artifacts;
import artifacts.component.SwimData;
import artifacts.component.ability.SwimInAir;
import artifacts.equipment.EquipmentHelper;
import artifacts.platform.PlatformServices;
import artifacts.registry.ModDataComponents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;

public class HeliumFlamingoOverlay {

    // TODO split into separate textures
    private static final Identifier HELIUM_FLAMINGO_ICON = Artifacts.id("textures/gui/icons.png");

    // TODO cleanup, see Gui::renderAirBubbles
    public static boolean renderOverlay(int height, GuiGraphics guiGraphics, int screenWidth, int screenHeight) {
        if (!(Minecraft.getInstance().getCameraEntity() instanceof LivingEntity player)
                || !EquipmentHelper.hasAbilityActive(ModDataComponents.SWIM_IN_AIR.get(), player, false)
        ) {
            return false;
        }
        SwimData swimData = PlatformServices.getPlatformHelper().getSwimData(player);
        if (swimData == null) {
            return false;
        }
        double progress = 1 - swimData.getSwimProgress();

        height = height + Artifacts.CONFIG.client.heliumFlamingoOverlayOffset.get();
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
            guiGraphics.blit(RenderPipelines.GUI_TEXTURED, HELIUM_FLAMINGO_ICON, left - i * 8 - 9, top, -90, (i < full ? 0 : 9), 0, 9, 9, 32, 16);
        }

        return true;
    }
}
