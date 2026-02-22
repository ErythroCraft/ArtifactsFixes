package artifacts.client;

import artifacts.Artifacts;
import artifacts.component.SwimData;
import artifacts.component.ability.SwimInAir;
import artifacts.equipment.EquipmentHelper;
import artifacts.platform.PlatformServices;
import artifacts.registry.ModDataComponents;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;

public class HeliumFlamingoOverlay {

    private static final int NUM_AIR_BUBBLES = 10;
    private static final int AIR_BUBBLE_SIZE = 9;
    private static final int AIR_BUBBLE_SEPARATION = 8;
    private static final int AIR_BUBBLE_POPPING_DURATION = 2;
    private static final int EMPTY_AIR_BUBBLE_DELAY_DURATION = 2;
    private static final float AIR_BUBBLE_POP_SOUND_VOLUME_BASE = 0.5F;
    private static final float AIR_BUBBLE_POP_SOUND_VOLUME_INCREMENT = 0.1F;
    private static final float AIR_BUBBLE_POP_SOUND_PITCH_BASE = 1.0F;
    private static final float AIR_BUBBLE_POP_SOUND_PITCH_INCREMENT = 0.1F;
    private static final int NUM_AIR_BUBBLE_POPPED_BEFORE_SOUND_VOLUME_INCREASE = 3;
    private static final int NUM_AIR_BUBBLE_POPPED_BEFORE_SOUND_PITCH_INCREASE = 5;

    private static final Identifier AIR_SPRITE = Artifacts.id("hud/flamingo");
    private static final Identifier AIR_POPPING_SPRITE = Artifacts.id("hud/flamingo_bursting");
    private static final Identifier AIR_EMPTY_SPRITE = Identifier.withDefaultNamespace("hud/air_empty");

    private int lastBubblePopSoundPlayed = 0;

    public boolean renderOverlay(GuiGraphics guiGraphics, Player player, int height) {
        SwimData swimData = PlatformServices.getPlatformHelper().getSwimData(player);
        if (!EquipmentHelper.hasAbilityActive(ModDataComponents.SWIM_IN_AIR.get(), player, false) || swimData == null) {
            return false;
        }

        boolean isLosingAir = swimData.isSwimFlying();

        int maxProgress = isLosingAir
                ? SwimInAir.getMaxFlightDuration(player)
                : SwimInAir.getRechargeDuration(player);

        int progress = (int) Math.floor(swimData.getSwimFlyingCharge() * maxProgress);

        if (!isLosingAir && progress >= maxProgress) {
            return false;
        }

        int hotbarEdge = guiGraphics.guiWidth() / 2 + 91;
        // Set height from bottom of screen, add config offset
        height = guiGraphics.guiHeight() - height - Artifacts.CONFIG.client.heliumFlamingoOverlayOffset.get();

        int fullBubble = getCurrentAirSupplyBubble(progress, maxProgress, -AIR_BUBBLE_POPPING_DURATION);
        int poppingBubble = getCurrentAirSupplyBubble(progress, maxProgress, 0);
        int emptyBubble = getCurrentAirSupplyBubble(progress, maxProgress, EMPTY_AIR_BUBBLE_DELAY_DURATION);

        if (!isLosingAir) {
            lastBubblePopSoundPlayed = 0;
        }

        for (int currentBubble = 1; currentBubble <= NUM_AIR_BUBBLES; ++currentBubble) {
            int x = hotbarEdge - (currentBubble - 1) * AIR_BUBBLE_SEPARATION - AIR_BUBBLE_SIZE;
            Identifier bubbleSprite = null;
            if (currentBubble <= fullBubble) {
                bubbleSprite = AIR_SPRITE;
            } else if (currentBubble == poppingBubble && isLosingAir) {
                bubbleSprite = AIR_POPPING_SPRITE;
                playAirBubblePoppedSound(currentBubble, player, NUM_AIR_BUBBLES - emptyBubble);
            } else if (!isLosingAir || currentBubble > emptyBubble) {
                bubbleSprite = AIR_EMPTY_SPRITE;
            }

            if (bubbleSprite != null) {
                guiGraphics.blitSprite(RenderPipelines.GUI_TEXTURED, bubbleSprite, x, height, AIR_BUBBLE_SIZE, AIR_BUBBLE_SIZE);
            }
        }

        return true;
    }

    private static int getCurrentAirSupplyBubble(int currentAirSupply, float maxAirSupply, int offset) {
        return Mth.ceil(((currentAirSupply + offset) * NUM_AIR_BUBBLES) / maxAirSupply);
    }

    private void playAirBubblePoppedSound(int currentBubble, Player player, int emptyBubbles) {
        if (lastBubblePopSoundPlayed != currentBubble) {
            float volume = AIR_BUBBLE_POP_SOUND_VOLUME_BASE
                    + AIR_BUBBLE_POP_SOUND_VOLUME_INCREMENT
                    * Math.max(0, emptyBubbles - NUM_AIR_BUBBLE_POPPED_BEFORE_SOUND_VOLUME_INCREASE + 1);
            float pitch = AIR_BUBBLE_POP_SOUND_PITCH_BASE
                    + AIR_BUBBLE_POP_SOUND_PITCH_INCREMENT
                    * Math.max(0, emptyBubbles - NUM_AIR_BUBBLE_POPPED_BEFORE_SOUND_PITCH_INCREASE + 1);

            player.playSound(SoundEvents.BUBBLE_POP, volume, pitch);
            lastBubblePopSoundPlayed = currentBubble;
        }
    }
}
