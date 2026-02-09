package artifacts.mixin.item.umbrella.client;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(HumanoidModel.class)
public abstract class HumanoidModelMixin<T extends LivingEntity> {

    @Shadow
    @Final
    public ModelPart rightArm;

    @Shadow
    @Final
    public ModelPart leftArm;

    // see https://github.com/florensie/artifacts-fabric/blob/1.16-fabric/src/main/java/artifacts/mixin/mixins/item/umbrella/client/HumanoidModelMixin.java
    /* TODO fix arm swing
    @SuppressWarnings("AmbiguousMixinReference")
    @Inject(method = "setupAnim", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;getMainArm()Lnet/minecraft/world/entity/HumanoidArm;"))
    private void reduceHandSwing(T entity, float f, float g, float h, float i, float j, CallbackInfo info) {
        boolean isHoldingOffHand = UmbrellaItem.isHoldingUmbrellaUpright(entity, InteractionHand.OFF_HAND);
        boolean isHoldingMainHand = UmbrellaItem.isHoldingUmbrellaUpright(entity, InteractionHand.MAIN_HAND);
        boolean isRightHanded = entity.getMainArm() == HumanoidArm.RIGHT;

        if ((isHoldingMainHand && isRightHanded) || (isHoldingOffHand && !isRightHanded)) {
            this.rightArm.xRot /= 8;
        }
        if ((isHoldingMainHand && !isRightHanded) || (isHoldingOffHand && isRightHanded)) {
            this.leftArm.xRot /= 8;
        }
    }
     */
}
