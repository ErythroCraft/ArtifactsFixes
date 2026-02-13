package artifacts.client.item.renderer;

import artifacts.client.item.model.BeltModel;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class WarpDriveRenderer extends SimpleArtifactRenderer {

    private static final int OVERLAY_TEXTURE_COUNT = 4;

    private final List<Identifier> overlayTextures;
    private final Random random = new Random();

    public WarpDriveRenderer(String name, BeltModel model) {
        super(ArtifactRenderer.getTextureId(name, name), null, model);
        overlayTextures = new ArrayList<>();
        for (int i = 0; i < OVERLAY_TEXTURE_COUNT; i++) {
            overlayTextures.add(ArtifactRenderer.getTextureId(name, "%s_overlay%s".formatted(name, i)));
        }
    }

    @Override
    protected @Nullable Identifier getFullBrightOverlayTexture(HumanoidRenderState renderState, int slotIndex) {
        int probability = 10;
        random.setSeed(((int) renderState.ageInTicks));
        if (random.nextInt(probability) == 0) {
            return overlayTextures.get(random.nextInt(overlayTextures.size()));
        }
        return null;
    }
}
