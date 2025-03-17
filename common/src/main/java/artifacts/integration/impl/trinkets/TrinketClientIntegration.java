package artifacts.integration.impl.trinkets;

import artifacts.client.CosmeticsHelper;
import artifacts.client.item.renderer.ArtifactRenderer;
import artifacts.client.item.renderer.GloveArtifactRenderer;
import artifacts.integration.EquipmentIntegrationConstants;
import artifacts.integration.client.ClientEquipmentIntegration;
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
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.function.Supplier;

public class TrinketClientIntegration implements ClientEquipmentIntegration {
    
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
    public boolean isVisibleOnHand(LivingEntity entity, InteractionHand hand, Item item) {
        return TrinketsApi.getTrinketComponent(entity).stream()
                .flatMap(component -> component.getAllEquipped().stream())
                .filter(tuple -> tuple.getA().inventory().getSlotType().getGroup().equals(
                        hand == InteractionHand.MAIN_HAND ? "hand" : "offhand"
                )).map(Tuple::getB)
                .filter(stack -> stack.is(item))
                .filter(stack -> !CosmeticsHelper.areCosmeticsToggledOffByPlayer(stack))
                .anyMatch(tuple -> true);
    }

    @Override
    public void renderArm(PoseStack matrixStack, MultiBufferSource buffer, int light, AbstractClientPlayer player, HumanoidArm side) {
        String groupId = side == player.getMainArm() ? "hand" : "offhand";
        TrinketsApi.getTrinketComponent(player).ifPresent(component -> {
            for (Tuple<SlotReference, ItemStack> pair : component.getAllEquipped()) {
                ItemStack stack = pair.getB();
                if (pair.getA().inventory().getSlotType().getGroup().equals(groupId)
                        && stack.getItem() instanceof WearableArtifactItem // Not every trinket is an artifact
                        && !CosmeticsHelper.areCosmeticsToggledOffByPlayer(stack)) {
                    GloveArtifactRenderer gloveRenderer = GloveArtifactRenderer.getGloveRenderer(stack);
                    if (gloveRenderer != null) {
                        gloveRenderer.renderFirstPersonArm(matrixStack, buffer, light, player, side, stack.hasFoil());
                    }
                }
            }
        });
    }

    @Override
    public String name() {
        return EquipmentIntegrationConstants.TRINKETS;
    }

    public record ArtifactTrinketRenderer(ArtifactRenderer renderer) implements TrinketRenderer {
        @Override
        public void render(ItemStack stack, SlotReference slotReference, EntityModel<? extends LivingEntity> entityModel, PoseStack poseStack, MultiBufferSource multiBufferSource, int light, LivingEntity entity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
            if (CosmeticsHelper.areCosmeticsToggledOffByPlayer(stack)) {
                return;
            }
            int index = slotReference.index() + (slotReference.inventory().getSlotType().getGroup().equals("hand") ? 0 : 1);
            renderer.render(stack, entity, index, poseStack, multiBufferSource, light, limbSwing, limbSwingAmount, partialTicks, ageInTicks, netHeadYaw, headPitch);
        }
    }
}
