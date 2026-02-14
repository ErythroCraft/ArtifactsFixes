package artifacts.client.item.model;

import artifacts.client.item.EquipmentRenderState;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.minecraft.world.entity.HumanoidArm;
import org.jetbrains.annotations.Nullable;

public class PocketPistonModel extends HumanoidModel<HumanoidRenderState> {

    private final @Nullable ModelPart leftPistonHead;
    private final @Nullable ModelPart rightPistonHead;

    public PocketPistonModel(ModelPart modelPart) {
        super(modelPart);
        this.leftPistonHead = leftArm.hasChild("artifact")
                ? leftArm.getChild("artifact").getChild("piston_head")
                : null;
        this.rightPistonHead = rightArm.hasChild("artifact")
                ? rightArm.getChild("artifact").getChild("piston_head")
                : null;
    }

    public static MeshDefinition createPocketPiston(boolean hasSlimArms) {
        CubeListBuilder leftArm = CubeListBuilder.create();
        CubeListBuilder rightArm = CubeListBuilder.create();
        CubeListBuilder leftPistonHead = CubeListBuilder.create();
        CubeListBuilder rightPistonHead = CubeListBuilder.create();

        float armWidth = hasSlimArms ? 3 : 4;
        float armDepth = 4;
        float d = 0.5F / 4 + 0.01F;

        // piston base
        CubeDeformation baseDeformation = new CubeDeformation(d * armWidth, d * 3, d * armDepth);
        leftArm.texOffs(0, 0);
        leftArm.addBox(-armWidth / 2, -3, -armDepth / 2, armWidth, 3, armDepth, baseDeformation);
        rightArm.texOffs(16, 0);
        rightArm.addBox(-armWidth / 2, -3, -armDepth / 2, armWidth, 3, armDepth, baseDeformation);

        // piston rod
        CubeDeformation rodDeformation = new CubeDeformation(d * armWidth / 2, 0, d * armDepth / 2);
        leftPistonHead.texOffs(0, 12);
        leftPistonHead.addBox(-(armWidth - 2) / 2, -2 + d * 3, -(armDepth - 2) / 2, armWidth - 2, 2, armDepth - 2, rodDeformation);
        rightPistonHead.texOffs(16, 12);
        rightPistonHead.addBox(-(armWidth - 2) / 2, -2 + d * 3, -(armDepth - 2) / 2, armWidth - 2, 2, armDepth - 2, rodDeformation);

        // piston head
        CubeDeformation headDeformation = new CubeDeformation(d * armWidth, d, d * armDepth);
        leftPistonHead.texOffs(0, 7);
        leftPistonHead.addBox(-armWidth / 2, d * 3 + d, -armDepth / 2, armWidth, 1, armDepth, headDeformation);
        rightPistonHead.texOffs(16, 7);
        rightPistonHead.addBox(-armWidth / 2, d * 3 + d, -armDepth / 2, armWidth, 1, armDepth, headDeformation);

        MeshDefinition mesh = ArmsModel.createEmptyArms(leftArm, rightArm, hasSlimArms);
        mesh.getRoot()
                .getChild("left_arm")
                .getChild("artifact")
                .addOrReplaceChild("piston_head", leftPistonHead, PartPose.ZERO);
        mesh.getRoot()
                .getChild("right_arm")
                .getChild("artifact")
                .addOrReplaceChild("piston_head", rightPistonHead, PartPose.ZERO);

        return mesh;
    }

    @Override
    public void setupAnim(HumanoidRenderState renderState) {
        super.setupAnim(renderState);
        ModelPart mainHandPistonHead = getPistonHead(renderState.mainArm);
        ModelPart offHandPistonHead = getPistonHead(renderState.mainArm.getOpposite());

        if (mainHandPistonHead != null) {
            mainHandPistonHead.y = EquipmentRenderState.from(renderState).pocketPistonExtensionLength * 2;
        }
        if (offHandPistonHead != null){
            offHandPistonHead.y = 0;
        }
    }

    private @Nullable ModelPart getPistonHead(HumanoidArm arm) {
        return arm == HumanoidArm.LEFT ? leftPistonHead : rightPistonHead;
    }
}
