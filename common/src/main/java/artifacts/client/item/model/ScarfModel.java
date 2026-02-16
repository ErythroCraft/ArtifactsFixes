package artifacts.client.item.model;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.resources.Identifier;

import java.util.Set;
import java.util.function.Function;

public class ScarfModel extends HumanoidModel<HumanoidRenderState> {

    private final ModelPart cloak = body.getChild("cloak");

    public ScarfModel(ModelPart part, Function<Identifier, RenderType> renderType) {
        super(part, renderType);
    }

    @Override
    public void setupAnim(HumanoidRenderState renderState) {
        super.setupAnim(renderState);

        // TODO fix rendering on non-player entities
        if (renderState instanceof AvatarRenderState avatarRenderState) {
            cloak.xRot = body.xRot + (6 + avatarRenderState.capeLean / 2 + avatarRenderState.capeFlap) / 180 * (float) Math.PI;
        }
    }

    public static MeshDefinition createScarf() {
        MeshDefinition mesh = createMesh(new CubeDeformation(0.51F), 0);

        mesh.getRoot().addOrReplaceChild(
                "body",
                CubeListBuilder.create()
                        .texOffs(0, 16)
                        .addBox(-6.01F, -2, -4, 12, 6, 8),
                PartPose.ZERO
        );

        mesh.getRoot().getChild("body").addOrReplaceChild(
                "cloak",
                CubeListBuilder.create()
                        .texOffs(32, 0)
                        .addBox(-5, 0, 0, 5, 12, 2),
                PartPose.offset(0, 0, 1.99F)
        );

        mesh.getRoot().getChild("head").clearRecursively();
        mesh.getRoot().retainPartsAndChildren(Set.of("body", "head"));

        return mesh;
    }
}
