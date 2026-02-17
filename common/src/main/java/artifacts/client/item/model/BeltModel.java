package artifacts.client.item.model;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.resources.Identifier;

import java.util.function.Function;

public class BeltModel extends HumanoidModel<HumanoidRenderState> {

    protected final ModelPart charm;
    private final CharmPose charmPose;

    public BeltModel(ModelPart part, Function<Identifier, RenderType> renderType, CharmPose charmPose) {
        super(part, renderType);
        this.charm = body.getChild("charm");
        this.charmPose = charmPose;
    }

    public BeltModel(ModelPart part, CharmPose charmPose) {
        this(part, RenderTypes::entityCutoutNoCull, charmPose);
    }

    @Override
    public void setupAnim(HumanoidRenderState renderState) {
        super.setupAnim(renderState);
        setCharmPosition(0); // TODO add slot index to renderstate or split into separate models
    }

    public void setCharmPosition(int slot) {
        float xOffset = slot % 2 == 0 ? charmPose.xOffset() : -charmPose.xOffset();
        float zOffset = slot % 4 < 2 ? charmPose.zOffset() : -charmPose.zOffset();
        charm.setPos(xOffset, 9, zOffset);

        float rotation = slot % 4 < 2 ? 0 : (float) -Math.PI;
        rotation += slot % 2 == 0 ^ slot % 4 >= 2 ? charmPose.rotation() : -charmPose.rotation();
        charm.yRot = rotation;
    }

}
