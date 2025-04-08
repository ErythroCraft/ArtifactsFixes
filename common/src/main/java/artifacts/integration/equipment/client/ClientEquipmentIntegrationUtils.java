package artifacts.integration.equipment.client;

import artifacts.client.item.renderer.ArtifactRenderer;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.item.Item;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Supplier;

public class ClientEquipmentIntegrationUtils {

    private static final Map<String, ClientEquipmentIntegration> INTEGRATIONS = new LinkedHashMap<>();

    public static void registerIntegration(ClientEquipmentIntegration integration) {
        String name = integration.name();

        if (INTEGRATIONS.containsKey(name)) {
            throw new IllegalStateException("Duplicate Client Equipment Integration detected! [Name: " + name + "]");
        }

        INTEGRATIONS.put(name, integration);
    }

    public static boolean hasIntegration(String name) {
        return INTEGRATIONS.containsKey(name);
    }

    public static void registerArtifactRenderer(Item item, Supplier<ArtifactRenderer> rendererSupplier) {
        for (ClientEquipmentIntegration value : INTEGRATIONS.values()) {
            value.registerArtifactRenderer(item, rendererSupplier);
        }
    }

    @Nullable
    public static ArtifactRenderer getArtifactRenderer(Item item) {
        for (ClientEquipmentIntegration value : INTEGRATIONS.values()) {
            ArtifactRenderer renderer = value.getArtifactRenderer(item);

            if (renderer != null) {
                return renderer;
            }
        }

        return null;
    }

    public static void renderArm(PoseStack matrixStack, MultiBufferSource buffer, int light, AbstractClientPlayer player, HumanoidArm side) {
        for (ClientEquipmentIntegration integration : INTEGRATIONS.values()) {
            integration.renderArm(matrixStack, buffer, light, player, side);
        }
    }
}
