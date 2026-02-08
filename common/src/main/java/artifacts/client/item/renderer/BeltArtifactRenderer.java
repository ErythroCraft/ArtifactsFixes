package artifacts.client.item.renderer;

import artifacts.client.item.model.BeltModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.minecraft.resources.Identifier;

public class BeltArtifactRenderer extends ArtifactRenderer {

    private final Identifier texture;
    private final BeltModel model;

    public BeltArtifactRenderer(String name, BeltModel model) {
        this.texture = ArtifactRenderer.getTextureId(name);
        this.model = model;
    }

    @Override
    protected Identifier getTexture(HumanoidRenderState renderState, int slotIndex) {
        return texture;
    }

    @Override
    protected HumanoidModel<HumanoidRenderState> getModel(HumanoidRenderState renderState, int slotIndex) {
        model.setCharmPosition(slotIndex); // TODO consider moving this to setupAnim or using separate models
        return model;
    }
}
