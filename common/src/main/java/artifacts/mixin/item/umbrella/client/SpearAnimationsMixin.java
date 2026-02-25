package artifacts.mixin.item.umbrella.client;

import artifacts.registry.ModTags;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.client.model.effects.SpearAnimations;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.minecraft.util.Ease;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.item.ItemStack;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(SpearAnimations.class)
public abstract class SpearAnimationsMixin {

    @Shadow
    static float progress(float f, float g, float h) {
        throw new UnsupportedOperationException();
    }

    @ModifyExpressionValue(method = "thirdPersonHandUse", at = @At(value = "FIELD", opcode = Opcodes.GETFIELD, ordinal = 0, target = "Lnet/minecraft/client/model/geom/ModelPart;yRot:F"))
    private static <T extends HumanoidRenderState> float modifyViewingDirectionPitch(float original, ModelPart arm, ModelPart head, boolean isRightArm, ItemStack stack, T renderState) {
        if (stack.is(ModTags.UMBRELLAS)) {
            // reduce the amount of left/right sway
            return original / 2;
        }
        return original;
    }

    @ModifyExpressionValue(method = "thirdPersonHandUse", at = @At(value = "FIELD", opcode = Opcodes.GETFIELD, ordinal = 0, target = "Lnet/minecraft/client/model/geom/ModelPart;xRot:F"))
    private static <T extends HumanoidRenderState> float modifyViewingDirectionYaw(float original, ModelPart arm, ModelPart head, boolean isRightArm, ItemStack stack, T renderState) {
        if (stack.is(ModTags.UMBRELLAS)) {
            float attackTime = isRightArm ^ renderState.mainArm == HumanoidArm.RIGHT ? 0 : renderState.attackTime;
            // use the same ease-in and ease-out used in SpearAnimations::thirdPersonAttackHand
            float easeIn = 1 - Ease.inOutSine(progress(attackTime, 0, 0.05F));
            float easeOut = Ease.inOutExpo(progress(attackTime, 0.4F, 1));
            float easeInOut = easeIn + easeOut;
            // point umbrella towards the player's looking direction when attacking, keep it upright otherwise
            return original * (1 - easeInOut)
                    - Mth.PI / 4 * easeInOut;
        }
        return original;
    }
}
