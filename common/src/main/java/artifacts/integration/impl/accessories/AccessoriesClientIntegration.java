package artifacts.integration.impl.accessories;

import artifacts.client.CosmeticsHelper;
import artifacts.client.item.renderer.ArtifactRenderer;
import artifacts.client.item.renderer.GloveArtifactRenderer;
import artifacts.integration.EquipmentIntegrationConstants;
import artifacts.integration.client.ClientEquipmentIntegration;
import com.mojang.blaze3d.vertex.PoseStack;
import io.wispforest.accessories.api.AccessoriesCapability;
import io.wispforest.accessories.api.AccessoriesContainer;
import io.wispforest.accessories.api.client.AccessoriesRendererRegistry;
import io.wispforest.accessories.api.client.AccessoryRenderer;
import io.wispforest.accessories.api.slot.SlotReference;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.Container;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

public class AccessoriesClientIntegration implements ClientEquipmentIntegration {

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
    public boolean isVisibleOnHand(LivingEntity entity, InteractionHand hand, Item item) {
        AccessoriesCapability capability = AccessoriesCapability.get(entity);

        if (capability != null) {
            AccessoriesContainer container = capability.getContainers().get("hand");

            if (container != null) {
                Container accessories = container.getAccessories();
                Container cosmetics = container.getCosmeticAccessories();

                int startSlot = hand == InteractionHand.MAIN_HAND ? 0 : 1;

                for (int slot = startSlot; slot < container.getSize(); slot += 2) {
                    if (container.shouldRender(slot)) continue;

                    ItemStack stack = cosmetics.getItem(slot);

                    if (stack.isEmpty()) stack = accessories.getItem(slot);

                    if (stack.getItem() == item) return true;
                }
            }
        }

        return false;
    }

    @Override
    public void renderArm(PoseStack matrixStack, MultiBufferSource buffer, int light, AbstractClientPlayer player, HumanoidArm side) {
        // NO-OP SEE shouldRenderInFirstPerson below
    }

    @Override
    public String name() {
        return EquipmentIntegrationConstants.ACCESSORIES;
    }

    public record ArtifactAccessoryRenderer(ArtifactRenderer renderer) implements AccessoryRenderer {
        @Override
        public <M extends LivingEntity> void render(ItemStack stack, SlotReference reference, PoseStack matrices, EntityModel<M> model, MultiBufferSource multiBufferSource, int light, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
            if (CosmeticsHelper.areCosmeticsToggledOffByPlayer(stack)) return;
            renderer.render(stack, reference.entity(), reference.slot(), matrices, multiBufferSource, light, limbSwing, limbSwingAmount, partialTicks, ageInTicks, netHeadYaw, headPitch);
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
