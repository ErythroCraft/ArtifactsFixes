package artifacts.integration.trinkets;

import artifacts.client.item.renderer.ArtifactRenderer;
import artifacts.client.item.renderer.GloveArtifactRenderer;
import artifacts.equipment.client.EquipmentRenderingHandler;
import artifacts.item.WearableArtifactItem;
import com.mojang.blaze3d.vertex.PoseStack;
import dev.emi.trinkets.api.SlotReference;
import dev.emi.trinkets.api.TrinketsApi;
import dev.emi.trinkets.api.client.TrinketRenderer;
import dev.emi.trinkets.api.client.TrinketRendererRegistry;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.function.Supplier;

public class TrinketsRenderingHandler implements EquipmentRenderingHandler {
    
    @Override
    public void registerArtifactRenderer(Item item, Supplier<ArtifactRenderer> rendererSupplier) {
        TrinketRendererRegistry.registerRenderer(item, new ArtifactTrinketRenderer(rendererSupplier.get()));
    }

    @Override
    public @Nullable ArtifactRenderer getArtifactRenderer(Item item) {
        Optional<TrinketRenderer> renderer = TrinketRendererRegistry.getRenderer(item);
        if (renderer.isPresent() && renderer.get() instanceof ArtifactTrinketRenderer artifactTrinketRenderer) {
            return artifactTrinketRenderer.renderer();
        }
        return null;
    }

    @Override
    public void renderArm(PoseStack matrixStack, MultiBufferSource buffer, int light, AbstractClientPlayer player, HumanoidArm side) {
        String groupId = side == player.getMainArm() ? "hand" : "offhand";
        TrinketsApi.getTrinketComponent(player).ifPresent(component -> {
            for (Tuple<SlotReference, ItemStack> pair : component.getAllEquipped()) {
                ItemStack stack = pair.getB();
                if (pair.getA().inventory().getSlotType().getGroup().equals(groupId) && stack.getItem() instanceof WearableArtifactItem) {
                    GloveArtifactRenderer gloveRenderer = GloveArtifactRenderer.getGloveRenderer(stack);
                    if (gloveRenderer != null) {
                        gloveRenderer.renderFirstPersonArm(matrixStack, buffer, light, player, side, stack.hasFoil());
                    }
                }
            }
        });
    }

    public record ArtifactTrinketRenderer(ArtifactRenderer renderer) implements TrinketRenderer {

        @Override
        public void render(ItemStack stack, SlotReference slotReference, EntityModel<? extends LivingEntity> entityModel, PoseStack poseStack, MultiBufferSource multiBufferSource, int light, LivingEntity entity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
            int index = slotReference.index() + (slotReference.inventory().getSlotType().getGroup().equals("hand") ? 0 : 1);
            renderer.render(stack, entity, index, poseStack, multiBufferSource, light, limbSwing, limbSwingAmount, partialTicks, ageInTicks, netHeadYaw, headPitch);
        }
    }
}
