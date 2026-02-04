package artifacts.client.item.renderer;

import artifacts.client.item.model.LegsModel;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;

import java.util.function.Function;

public class BootArtifactRenderer implements ArtifactRenderer {

    private final Identifier texture;
    private final LegsModel model;
    private final LegsModel armorModel;

    public BootArtifactRenderer(String name, Function<Boolean, LegsModel> model) {
        this.texture = ArtifactRenderer.getTexturePath(name);
        this.model = model.apply(false);
        this.armorModel = model.apply(true);
    }

    protected Identifier getTexture() {
        return texture;
    }

    protected HumanoidModel<HumanoidRenderState> getModel(HumanoidRenderState renderState) {
        return renderState.feetEquipment.isEmpty() ? model : armorModel;
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
        HumanoidModel<HumanoidRenderState> model = getModel(humanoidRenderState);

        ArtifactRenderer.loadPoseFrom(model, entityModel);
        render(model, poseStack, multiBufferSource, light, stack.hasFoil());
    }

    protected void render(EntityModel<?> model, PoseStack matrixStack, MultiBufferSource buffer, int light, boolean hasFoil) {
        RenderType renderType = model.renderType(getTexture());
        VertexConsumer vertexBuilder = ItemRenderer.getFoilBuffer(buffer, renderType, false, hasFoil);
        model.renderToBuffer(matrixStack, vertexBuilder, light, OverlayTexture.NO_OVERLAY, 0xFFFFFFFF);
    }
}
