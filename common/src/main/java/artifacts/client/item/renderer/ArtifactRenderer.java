package artifacts.client.item.renderer;

import artifacts.Artifacts;
import artifacts.config.value.Value;
import artifacts.registry.ModDataComponents;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.monster.ghast.GhastModel;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public abstract class ArtifactRenderer {

    private static final HumanoidRenderState DEFAULT_RENDER_STATE = new HumanoidRenderState();

    protected abstract HumanoidModel<HumanoidRenderState> getModel(HumanoidRenderState renderState, int slotIndex);

    protected abstract Identifier getTexture(HumanoidRenderState renderState, int slotIndex);

    protected @Nullable Identifier getFullBrightOverlayTexture(HumanoidRenderState renderState, int slotIndex) {
        return null;
    }

    public void render(
            ItemStack stack,
            LivingEntityRenderState renderState,
            EntityModel<? extends LivingEntityRenderState> entityModel,
            int slotIndex,
            PoseStack poseStack,
            SubmitNodeCollector submitNodeCollector,
            int light
    ) {
        Value<Boolean> hideWhenInvisible = stack.get(ModDataComponents.HIDE_WHEN_INVISIBLE.get());
        if (hideWhenInvisible != null && hideWhenInvisible.get() && renderState.isInvisible) {
            return;
        }

        HumanoidRenderState humanoidRenderState = renderState instanceof HumanoidRenderState s ? s : DEFAULT_RENDER_STATE;

        HumanoidModel<HumanoidRenderState> model = getModel(humanoidRenderState, slotIndex);
        ArtifactRenderer.loadPoseFrom(model, entityModel, humanoidRenderState);

        if (entityModel instanceof GhastModel) {
            // TODO test rotation
            poseStack.scale(2.5F, 2.5F, 2.5F);
            poseStack.translate(0, -2.5/16F, 0);
        }

        Identifier texture = getTexture(humanoidRenderState, slotIndex);
        Identifier glowTexture = getFullBrightOverlayTexture(humanoidRenderState, slotIndex);

        renderModelWithFoil(model, humanoidRenderState, poseStack, submitNodeCollector, texture, light, stack.hasFoil());
        if (glowTexture != null) {
            renderModelWithFoil(model, humanoidRenderState, poseStack, submitNodeCollector, glowTexture, LightTexture.FULL_BRIGHT, stack.hasFoil());
        }
    }

    protected static Identifier getTextureId(String... names) {
        StringBuilder path = new StringBuilder("textures/entity/wearable");
        for (String name : names) {
            path.append('/');
            path.append(name);
        }
        path.append(".png");
        return Artifacts.id(path.toString());
    }

    private static <S> void renderModelWithFoil(Model<S> model, S renderState, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, Identifier texture, int light, boolean hasFoil) {
        RenderType renderType = model.renderType(texture);
        submitNodeCollector.order(0).submitModel(model, renderState, poseStack, renderType, light, OverlayTexture.NO_OVERLAY, 0, null);
        if (hasFoil) {
            submitNodeCollector.order(1).submitModel(model, renderState, poseStack, RenderTypes.armorEntityGlint(), light, OverlayTexture.NO_OVERLAY, 0, null);
        }
    }

    private static void loadPoseFrom(HumanoidModel<HumanoidRenderState> model, EntityModel<?> source, HumanoidRenderState renderState) {
        model.resetPose();
        model.setupAnim(renderState);
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
