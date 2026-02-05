package artifacts.client;

import artifacts.item.UmbrellaItem;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;

public class UmbrellaArmPoseHelper {

    // TODO call from extractHumanoidRenderState
    public static void setUmbrellaArmPose(HumanoidRenderState renderState, LivingEntity entity) {
        boolean isHoldingOffHand = UmbrellaItem.isHoldingUmbrellaUpright(entity, InteractionHand.OFF_HAND);
        boolean isHoldingMainHand = UmbrellaItem.isHoldingUmbrellaUpright(entity, InteractionHand.MAIN_HAND);
        boolean isRightHanded = entity.getMainArm() == HumanoidArm.RIGHT;

        if ((isHoldingMainHand && isRightHanded) || (isHoldingOffHand && !isRightHanded)) {
            renderState.rightArmPose = HumanoidModel.ArmPose.THROW_TRIDENT;
        }
        if ((isHoldingMainHand && !isRightHanded) || (isHoldingOffHand && isRightHanded)) {
            renderState.leftArmPose = HumanoidModel.ArmPose.THROW_TRIDENT;
        }
    }
}
