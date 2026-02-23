package artifacts.client.item.renderer;

import artifacts.Artifacts;
import artifacts.config.value.Value;
import artifacts.registry.ModDataComponents;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelPart;
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

    protected abstract Identifier getTexture(HumanoidRenderState renderState);

    protected @Nullable Identifier getFullBrightOverlayTexture(HumanoidRenderState renderState) {
        return null;
    }

    public void render(
            ItemStack stack,
            LivingEntityRenderState renderState,
            EntityModel<?> entityModel,
            int slotIndex,
            PoseStack poseStack,
            SubmitNodeCollector submitNodeCollector,
            int light
    ) {
        poseStack.pushPose();
        Value<Boolean> hideWhenInvisible = stack.get(ModDataComponents.HIDE_WHEN_INVISIBLE.get());
        if (hideWhenInvisible != null && hideWhenInvisible.get() && renderState.isInvisible) {
            return;
        }

        HumanoidRenderState humanoidRenderState = renderState instanceof HumanoidRenderState s ? s : DEFAULT_RENDER_STATE;

        HumanoidModel<HumanoidRenderState> model = getModel(humanoidRenderState, slotIndex);
        loadPoseFrom(model, entityModel, humanoidRenderState);

        if (entityModel instanceof GhastModel) {
            poseStack.translate(0, 1.25F, 0);
            poseStack.scale(9F, 9F, 9F);
        }

        Identifier texture = getTexture(humanoidRenderState);
        Identifier glowTexture = getFullBrightOverlayTexture(humanoidRenderState);

        renderModelWithFoil(model, humanoidRenderState, poseStack, submitNodeCollector, texture, light, stack.hasFoil());
        if (glowTexture != null) {
            renderModelWithFoil(model, humanoidRenderState, poseStack, submitNodeCollector, glowTexture, LightTexture.FULL_BRIGHT, stack.hasFoil());
        }
        poseStack.popPose();
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

    @SuppressWarnings("DataFlowIssue")
    protected static <S> void renderModelWithFoil(Model<S> model, S renderState, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, Identifier texture, int packedLight, boolean hasFoil) {
        RenderType renderType = model.renderType(texture);
        submitNodeCollector.order(0).submitModel(model, renderState, poseStack, renderType, packedLight, OverlayTexture.NO_OVERLAY, 0, null);
        if (hasFoil) {
            submitNodeCollector.order(1).submitModel(model, renderState, poseStack, RenderTypes.armorEntityGlint(), packedLight, OverlayTexture.NO_OVERLAY, 0, null);
        }
    }

    private static void loadPoseFrom(HumanoidModel<HumanoidRenderState> model, EntityModel<?> source, HumanoidRenderState renderState) {
        model.resetPose();
        // setup artifact animations
        model.setupAnim(renderState);
        // calling setupAnim is not enough, humanoidModel subclasses may apply additional transforms (e.g. zombie arms)
        if (source instanceof HumanoidModel<?> humanoidModel) {
            loadPoseFrom(model.head, humanoidModel.head);
            loadPoseFrom(model.body, humanoidModel.body);
            loadPoseFrom(model.leftArm, humanoidModel.leftArm);
            loadPoseFrom(model.rightArm, humanoidModel.rightArm);
            loadPoseFrom(model.leftLeg, humanoidModel.leftLeg);
            loadPoseFrom(model.rightLeg, humanoidModel.rightLeg);
        }
    }

    private static void loadPoseFrom(ModelPart modelPart, ModelPart source) {
        modelPart.x = source.x;
        modelPart.y = source.y;
        modelPart.z = source.z;
        modelPart.xRot = source.xRot;
        modelPart.yRot = source.yRot;
        modelPart.zRot = source.zRot;
        modelPart.xScale = source.xScale;
        modelPart.yScale = source.yScale;
        modelPart.zScale = source.zScale;
    }
}
