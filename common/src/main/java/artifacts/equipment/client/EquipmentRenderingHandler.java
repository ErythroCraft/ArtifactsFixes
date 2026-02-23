package artifacts.equipment.client;

import artifacts.client.item.renderer.ArtifactRenderer;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.item.Item;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

public interface EquipmentRenderingHandler {

    void registerArtifactRenderer(Item item, Supplier<ArtifactRenderer> rendererSupplier);

    @Nullable
    ArtifactRenderer getArtifactRenderer(Item item);

    void renderArm(PoseStack matrixStack, SubmitNodeCollector submitNodeCollector, int packedLight, AbstractClientPlayer player, HumanoidArm side);

}
