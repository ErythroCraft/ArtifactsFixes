package artifacts.client.item.renderer;

import artifacts.client.item.model.LegsModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.minecraft.resources.Identifier;

import java.util.function.Function;

public class BootArtifactRenderer extends ArtifactRenderer {

    private final Identifier texture;
    private final LegsModel model;
    private final LegsModel armorModel;

    public BootArtifactRenderer(String name, Function<Boolean, LegsModel> model) {
        this.texture = ArtifactRenderer.getTextureId(name);
        this.model = model.apply(false);
        this.armorModel = model.apply(true);
    }

    @Override
    protected HumanoidModel<HumanoidRenderState> getModel(HumanoidRenderState renderState, int slotIndex) {
        if (renderState == null || renderState.feetEquipment.isEmpty()) {
            return model;
        }
        return armorModel;
    }

    @Override
    protected Identifier getTexture(HumanoidRenderState renderState, int slotIndex) {
        return texture;
    }
}
