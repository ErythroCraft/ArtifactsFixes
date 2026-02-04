package artifacts.client.item.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.entity.state.GhastRenderState;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;

public class GenericArtifactRenderer implements ArtifactRenderer {

    private final Identifier texture;
    private final HumanoidModel<HumanoidRenderState> model;

    public GenericArtifactRenderer(String name, HumanoidModel<HumanoidRenderState> model) {
        this(ArtifactRenderer.getTexturePath(name), model);
    }

    public GenericArtifactRenderer(Identifier texture, HumanoidModel<HumanoidRenderState> model) {
        this.texture = texture;
        this.model = model;
    }

    protected Identifier getTexture() {
        return texture;
    }

    protected HumanoidModel<HumanoidRenderState> getModel() {
        return model;
    }

    @Override
    public void render(
            ItemStack stack,
            LivingEntityRenderState renderState,
            EntityModel<?> entityModel,
            int slotIndex,
            PoseStack poseStack,
            MultiBufferSource multiBufferSource,
            int light,
            float partialTicks
    ) {
        poseStack.pushPose();
        HumanoidModel<HumanoidRenderState> model = getModel();

        ArtifactRenderer.loadPoseFrom(model, entityModel);

        if (renderState instanceof GhastRenderState) {
            applyGhastTransforms(poseStack);
        }

        render(poseStack, multiBufferSource, light, stack.hasFoil());
        poseStack.popPose();
    }

    private void applyGhastTransforms(PoseStack poseStack) {
        HumanoidModel<HumanoidRenderState> model = getModel();
        model.head.yRot = model.body.yRot;
        poseStack.scale(2.5F, 2.5F, 2.5F);
        poseStack.translate(0, -2.5/16F, 0);
    }

    protected void render(PoseStack matrixStack, MultiBufferSource buffer, int light, boolean hasFoil) {
        HumanoidModel<HumanoidRenderState> model = getModel();
        RenderType renderType = model.renderType(getTexture());
        VertexConsumer vertexBuilder = ItemRenderer.getFoilBuffer(buffer, renderType, false, hasFoil);
        model.renderToBuffer(matrixStack, vertexBuilder, light, OverlayTexture.NO_OVERLAY, 0xFFFFFFFF);
    }
}
