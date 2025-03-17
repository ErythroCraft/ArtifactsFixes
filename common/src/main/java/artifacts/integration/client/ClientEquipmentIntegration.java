package artifacts.integration.client;

import artifacts.client.item.renderer.ArtifactRenderer;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

public interface ClientEquipmentIntegration {

    void registerArtifactRenderer(Item item, Supplier<ArtifactRenderer> rendererSupplier);

    @Nullable
    ArtifactRenderer getArtifactRenderer(Item item);

    boolean isVisibleOnHand(LivingEntity entity, InteractionHand hand, Item item);

    void renderArm(PoseStack matrixStack, MultiBufferSource buffer, int light, AbstractClientPlayer player, HumanoidArm side);

    String name();
}
