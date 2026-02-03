package artifacts.client.item.renderer;

import artifacts.Artifacts;
import artifacts.config.value.Value;
import artifacts.registry.ModDataComponents;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.resources.Identifier;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public interface ArtifactRenderer {

    default void renderVisible(
            ItemStack stack,
            LivingEntity entity,
            int slotIndex,
            PoseStack poseStack,
            MultiBufferSource multiBufferSource,
            int light,
            float limbSwing,
            float limbSwingAmount,
            float partialTicks,
            float ageInTicks,
            float netHeadYaw,
            float headPitch
    ) {
        Value<Boolean> hideWhenInvisible = stack.get(ModDataComponents.HIDE_WHEN_INVISIBLE.get());
        if (hideWhenInvisible != null && hideWhenInvisible.get() && entity.hasEffect(MobEffects.INVISIBILITY)) {
            return;
        }
        render(stack, entity, slotIndex, poseStack, multiBufferSource, light, limbSwing, limbSwingAmount, partialTicks, ageInTicks, netHeadYaw, headPitch);
    }

    // TODO implement default method, add getModel(RenderState) method
    void render(
            ItemStack stack,
            LivingEntity entity,
            int slotIndex,
            PoseStack poseStack,
            MultiBufferSource multiBufferSource,
            int light,
            float limbSwing,
            float limbSwingAmount,
            float partialTicks,
            float ageInTicks,
            float netHeadYaw,
            float headPitch
    );

    static Identifier getTexturePath(String... names) {
        StringBuilder path = new StringBuilder("textures/entity/wearable");
        for (String name : names) {
            path.append('/');
            path.append(name);
        }
        path.append(".png");
        return Artifacts.id(path.toString());
    }

    static void followBodyRotations(EntityModel<? extends LivingEntityRenderState> source, HumanoidModel<?> model) {
        if (source instanceof HumanoidModel<?> bipedModel) {
            model.head.loadPose(bipedModel.head.storePose());
            model.body.loadPose(bipedModel.body.storePose());
            model.leftArm.loadPose(bipedModel.leftArm.storePose());
            model.rightArm.loadPose(bipedModel.rightArm.storePose());
            model.leftLeg.loadPose(bipedModel.leftLeg.storePose());
            model.rightLeg.loadPose(bipedModel.rightLeg.storePose());
        }
    }
}
