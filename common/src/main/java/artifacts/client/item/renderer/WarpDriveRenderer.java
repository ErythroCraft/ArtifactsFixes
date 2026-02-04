package artifacts.client.item.renderer;

import artifacts.client.item.model.BeltModel;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class WarpDriveRenderer extends BeltArtifactRenderer {

    private final List<Identifier> overlayTextures;
    private final Random random = new Random();

    public WarpDriveRenderer(String name, BeltModel model) {
        super("%s/%s".formatted(name, name), model);
        overlayTextures = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            overlayTextures.add(ArtifactRenderer.getTexturePath(name, "%s_overlay%s".formatted(name, i)));
        }
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
        super.render(stack, renderState, entityModel, slotIndex, poseStack, multiBufferSource, light, partialTicks);

        int probability = 10;
        random.setSeed(((int) renderState.ageInTicks));
        if (random.nextInt(probability) == 0) {
            RenderType renderType = getModel().renderType(overlayTextures.get(random.nextInt(overlayTextures.size())));
            VertexConsumer builder = ItemRenderer.getFoilBuffer(multiBufferSource, renderType, false, false);
            getModel().renderToBuffer(poseStack, builder, LightTexture.pack(15, 15), OverlayTexture.NO_OVERLAY, 0xFFFFFFFF);
        }
    }
}
