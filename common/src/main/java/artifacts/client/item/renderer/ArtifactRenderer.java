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
import net.minecraft.world.item.ItemStack;

/* TODO cleanup renderers
 * - make render() a default method
 * - add getModel(RenderState) method
 * - add @Nullable getOverlayTexture(RenderState) method
 * - split armed models into separate left- and right arm models, same for charms (or move logic to setupAnim if possible)
 */
public interface ArtifactRenderer {

    default void renderVisible(
            ItemStack stack,
            LivingEntityRenderState renderState,
            EntityModel<?> entityModel,
            int slotIndex,
            PoseStack poseStack,
            MultiBufferSource multiBufferSource,
            int light,
            float partialTicks
    ) {
        Value<Boolean> hideWhenInvisible = stack.get(ModDataComponents.HIDE_WHEN_INVISIBLE.get());
        if (hideWhenInvisible != null && hideWhenInvisible.get() && renderState.isInvisible) {
            return;
        }

        render(stack, renderState, entityModel, slotIndex, poseStack, multiBufferSource, light, partialTicks);
    }

    void render(
            ItemStack stack,
            LivingEntityRenderState renderState,
            EntityModel<?> entityModel,
            int slotIndex,
            PoseStack poseStack,
            MultiBufferSource multiBufferSource,
            int light,
            float partialTicks
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

    static void loadPoseFrom(HumanoidModel<?> model, EntityModel<?> source) {
        if (source instanceof HumanoidModel<?> humanoidModel) {
            // TODO storePose creates new PartPose instances
            model.head.loadPose(humanoidModel.head.storePose());
            model.body.loadPose(humanoidModel.body.storePose());
            model.leftArm.loadPose(humanoidModel.leftArm.storePose());
            model.rightArm.loadPose(humanoidModel.rightArm.storePose());
            model.leftLeg.loadPose(humanoidModel.leftLeg.storePose());
            model.rightLeg.loadPose(humanoidModel.rightLeg.storePose());
        }
    }
}
