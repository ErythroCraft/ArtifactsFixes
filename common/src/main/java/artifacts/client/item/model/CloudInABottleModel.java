package artifacts.client.item.model;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;

import java.util.function.Function;

public class CloudInABottleModel extends BeltModel {

    private final ModelPart cloud;

    public CloudInABottleModel(ModelPart part, Function<Identifier, RenderType> renderType, CharmPose charmPose) {
        super(part, renderType, charmPose);
        this.cloud = charm.getChild("cloud");
    }

    @Override
    public void setupAnim(HumanoidRenderState renderState) {
        super.setupAnim(renderState);
        cloud.yRot = (renderState.ageInTicks) / 50;
        cloud.y = Mth.cos((renderState.ageInTicks) / 30) / 2;
    }
}
