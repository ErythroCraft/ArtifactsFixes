package artifacts.client.item.renderer;

import artifacts.client.item.model.BeltModel;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;

public class BeltArtifactRenderer implements ArtifactRenderer {

    private final Identifier texture;
    private final BeltModel model;

    public BeltArtifactRenderer(String name, BeltModel model) {
        this.texture = ArtifactRenderer.getTexturePath(name);
        this.model = model;
    }

    protected Identifier getTexture() {
        return texture;
    }

    protected BeltModel getModel() {
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
        if (!(renderState instanceof HumanoidRenderState humanoidRenderState)) {
            return;
        }
        BeltModel model = getModel();

        model.setCharmPosition(slotIndex);
        ArtifactRenderer.loadPoseFrom(model, entityModel);
        render(poseStack, multiBufferSource, light, stack.hasFoil());
    }

    protected void render(PoseStack matrixStack, MultiBufferSource buffer, int light, boolean hasFoil) {
        RenderType renderType = model.renderType(getTexture());
        VertexConsumer vertexBuilder = ItemRenderer.getFoilBuffer(buffer, renderType, false, hasFoil);
        model.renderToBuffer(matrixStack, vertexBuilder, light, OverlayTexture.NO_OVERLAY, 0xFFFFFFFF);
    }
}
