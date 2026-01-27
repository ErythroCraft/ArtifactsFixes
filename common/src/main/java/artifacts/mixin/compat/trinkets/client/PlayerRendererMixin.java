package artifacts.mixin.compat.trinkets.client;

import artifacts.Artifacts;
import artifacts.equipment.client.EquipmentRenderingManager;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.player.AvatarRenderer;
import net.minecraft.world.entity.HumanoidArm;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AvatarRenderer.class)
public abstract class PlayerRendererMixin {

    @Inject(method = "renderLeftHand", at = @At("TAIL"))
    private void renderLeftGlove(PoseStack matrixStack, MultiBufferSource buffer, int light, AbstractClientPlayer player, CallbackInfo callbackInfo) {
        artifacts$renderArm(matrixStack, buffer, light, player, HumanoidArm.LEFT);
    }

    @Inject(method = "renderRightHand", at = @At("TAIL"))
    private void renderRightGlove(PoseStack matrixStack, MultiBufferSource buffer, int light, AbstractClientPlayer player, CallbackInfo callbackInfo) {
        artifacts$renderArm(matrixStack, buffer, light, player, HumanoidArm.RIGHT);
    }

    @Unique
    private static void artifacts$renderArm(PoseStack matrixStack, MultiBufferSource buffer, int light, AbstractClientPlayer player, HumanoidArm handSide) {
        if (!Artifacts.CONFIG.client.showFirstPersonGloves.get()) {
            return;
        }

        EquipmentRenderingManager.renderArm(matrixStack, buffer, light, player, handSide);
    }
}
