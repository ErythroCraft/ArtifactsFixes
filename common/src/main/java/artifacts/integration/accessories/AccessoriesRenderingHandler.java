package artifacts.integration.accessories;

import artifacts.client.item.renderer.ArtifactRenderer;
import artifacts.client.item.renderer.GloveArtifactRenderer;
import artifacts.equipment.client.EquipmentRenderingHandler;
import com.mojang.blaze3d.vertex.PoseStack;
import io.wispforest.accessories.api.client.AccessoriesRendererRegistry;
import io.wispforest.accessories.api.client.AccessoryRenderer;
import io.wispforest.accessories.api.slot.SlotReference;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

public class AccessoriesRenderingHandler implements EquipmentRenderingHandler {

    @Override
    public void registerArtifactRenderer(Item item, Supplier<ArtifactRenderer> rendererSupplier) {
        AccessoriesRendererRegistry.registerRenderer(item, () -> new ArtifactAccessoryRenderer(rendererSupplier.get()));
    }

    @Override
    public @Nullable ArtifactRenderer getArtifactRenderer(Item item) {
        AccessoryRenderer renderer = AccessoriesRendererRegistry.getRender(item);
        if (renderer instanceof ArtifactAccessoryRenderer artifactAccessoryRenderer) {
            return artifactAccessoryRenderer.renderer();
        }
        return null;
    }

    @Override
    public void renderArm(PoseStack matrixStack, MultiBufferSource buffer, int light, AbstractClientPlayer player, HumanoidArm side) {
        // NO-OP SEE shouldRenderInFirstPerson below
    }

    public record ArtifactAccessoryRenderer(ArtifactRenderer renderer) implements AccessoryRenderer {
        @Override
        public <M extends LivingEntity> void render(ItemStack stack, SlotReference reference, PoseStack matrices, EntityModel<M> model, MultiBufferSource multiBufferSource, int light, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
            renderer.renderVisible(stack, reference.entity(), reference.slot(), matrices, multiBufferSource, light, limbSwing, limbSwingAmount, partialTicks, ageInTicks, netHeadYaw, headPitch);
        }

        @Override
        public <M extends LivingEntity> void renderOnFirstPerson(HumanoidArm side, ItemStack stack, SlotReference reference, PoseStack matrices, EntityModel<M> model, MultiBufferSource multiBufferSource, int light) {
            if (!(reference.entity() instanceof LocalPlayer player)) {
                return;
            }
            InteractionHand hand = side == player.getMainArm() ? InteractionHand.MAIN_HAND : InteractionHand.OFF_HAND;

            GloveArtifactRenderer gloveRenderer = GloveArtifactRenderer.getGloveRenderer(stack);

            if (gloveRenderer != null && reference.slot() % 2 == (hand == InteractionHand.MAIN_HAND ? 0 : 1)) {
                gloveRenderer.renderFirstPersonArm(matrices, multiBufferSource, light, (AbstractClientPlayer) reference.entity(), side, stack.hasFoil());
            }
        }
    }
}
