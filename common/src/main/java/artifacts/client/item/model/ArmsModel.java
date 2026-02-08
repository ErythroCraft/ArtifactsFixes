package artifacts.client.item.model;

import artifacts.client.item.ArtifactLayers;
import artifacts.client.item.ArtifactRenderers;
import artifacts.client.item.EquipmentRenderState;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.HumanoidArm;

import java.util.function.Function;

public class ArmsModel extends HumanoidModel<HumanoidRenderState> {

    public ArmsModel(ModelPart part, Function<Identifier, RenderType> renderType) {
        super(part, renderType);
    }

    public ArmsModel(ModelPart part) {
        this(part, RenderTypes::entityCutoutNoCull);
    }

    public static ArmsModel createClawsModel(boolean hasSlimArms) {
        return new ArmsModel(ArtifactRenderers.bakeLayer(ArtifactLayers.claws(hasSlimArms)));
    }

    public static ArmsModel createGloveModel(boolean hasSlimArms) {
        return new ArmsModel(ArtifactRenderers.bakeLayer(ArtifactLayers.glove(hasSlimArms)));
    }

    public static ArmsModel createGoldenHookModel(boolean hasSlimArms) {
        return new ArmsModel(ArtifactRenderers.bakeLayer(ArtifactLayers.goldenHook(hasSlimArms)));
    }

    public static ArmsModel createPocketPistonModel(boolean hasSlimArms) {
        return new ArmsModel(ArtifactRenderers.bakeLayer(ArtifactLayers.pocketPiston(hasSlimArms))) {

            @Override
            public void setupAnim(HumanoidRenderState renderState) {
                super.setupAnim(renderState);
                HumanoidArm mainHandSide = renderState.mainArm;
                getPistonHead(mainHandSide.getOpposite()).y = 0;
                getPistonHead(mainHandSide).y = EquipmentRenderState.from(renderState).pocketPistonExtensionLength * 2;
            }

            private ModelPart getPistonHead(HumanoidArm arm) {
                return getArm(arm)
                        .getChild("artifact")
                        .getChild("piston_head");
            }
        };
    }

    public static ArmsModel createOnionRingModel(boolean hasSlimArms) {
        return new ArmsModel(ArtifactRenderers.bakeLayer(ArtifactLayers.onionRing(hasSlimArms)));
    }

    public static ArmsModel createPickaxeHeaterModel(boolean hasSlimArms) {
        return new ArmsModel(ArtifactRenderers.bakeLayer(ArtifactLayers.pickaxeHeater(hasSlimArms)));
    }

    public static ArmsModel createWitheredBraceletModel(boolean hasSlimArms) {
        return new ArmsModel(ArtifactRenderers.bakeLayer(ArtifactLayers.witheredBracelet(hasSlimArms)));
    }

    public static MeshDefinition createEmptyArms(CubeListBuilder leftArm, CubeListBuilder rightArm, boolean hasSlimArms) {
        MeshDefinition mesh = createMesh(CubeDeformation.NONE, 0);

        mesh.getRoot().addOrReplaceChild("left_arm", CubeListBuilder.create(), PartPose.ZERO);
        mesh.getRoot().addOrReplaceChild("right_arm", CubeListBuilder.create(), PartPose.ZERO);

        float armWidth = hasSlimArms ? 3 : 4;
        mesh.getRoot().getChild("left_arm").addOrReplaceChild(
                "artifact",
                leftArm,
                PartPose.offset(- 1 + armWidth / 2, 10, 0)
        );
        mesh.getRoot().getChild("right_arm").addOrReplaceChild(
                "artifact",
                rightArm,
                PartPose.offset(1 - armWidth / 2, 10, 0)
        );

        return mesh;
    }

    public static MeshDefinition createArms(CubeListBuilder leftArm, CubeListBuilder rightArm, boolean hasSlimArms) {
        float armWidth = hasSlimArms ? 3 : 4;

        leftArm.texOffs(0, 0);
        leftArm.addBox(-armWidth / 2, -12, -2, armWidth, 12, 4, new CubeDeformation(0.5F));
        rightArm.texOffs(16, 0);
        rightArm.addBox(-armWidth / 2, -12, -2, armWidth, 12, 4, new CubeDeformation(0.5F));

        return createEmptyArms(leftArm, rightArm, hasSlimArms);
    }

    public static MeshDefinition createSleevedArms(CubeListBuilder leftArm, CubeListBuilder rightArm, boolean hasSlimArms) {
        float armWidth = hasSlimArms ? 3 : 4;

        leftArm.texOffs(0, 16);
        leftArm.addBox(-armWidth / 2, -12, -2, armWidth, 12, 4, new CubeDeformation(0.75F));
        rightArm.texOffs(16, 16);
        rightArm.addBox(-armWidth / 2, -12, -2, armWidth, 12, 4, new CubeDeformation(0.75F));

        return createArms(leftArm, rightArm, hasSlimArms);
    }

    public static MeshDefinition createSleevedArms(boolean hasSlimArms) {
        return createSleevedArms(CubeListBuilder.create(), CubeListBuilder.create(), hasSlimArms);
    }

    public static MeshDefinition createClaws(boolean hasSlimArms) {
        CubeListBuilder leftArm = CubeListBuilder.create();
        CubeListBuilder rightArm = CubeListBuilder.create();

        // claw 1 lower
        leftArm.texOffs(0, 0);
        leftArm.addBox(hasSlimArms ? -2 : -1.5F, 0, -1.5F, 3, 5, 1);
        rightArm.texOffs(8, 0);
        rightArm.addBox(hasSlimArms ? -1 : -1.5F, 0, -1.5F, 3, 5, 1);

        // claw 2 lower
        leftArm.texOffs(0, 6);
        leftArm.addBox(hasSlimArms ? -2 : -1.5F, 0, 0.5F, 3, 5, 1);
        rightArm.texOffs(8, 6);
        rightArm.addBox(hasSlimArms ? -1 : -1.5F, 0, 0.5F, 3, 5, 1);

        // claw 1 upper
        leftArm.texOffs(16, 0);
        leftArm.addBox(hasSlimArms ? 1 : 1.5F, 0, -1.5F, 1, 4, 1);
        rightArm.texOffs(20, 0);
        rightArm.addBox(hasSlimArms ? -2 : -2.5F, 0, -1.5F, 1, 4, 1);

        // claw 2 upper
        leftArm.texOffs(16, 6);
        leftArm.addBox(hasSlimArms ? 1 : 1.5F, 0, 0.5F, 1, 4, 1);
        rightArm.texOffs(20, 6);
        rightArm.addBox(hasSlimArms ? -2 : -2.5F, 0, 0.5F, 1, 4, 1);

        return createEmptyArms(leftArm, rightArm, hasSlimArms);
    }

    public static MeshDefinition createGoldenHook(boolean hasSlimArms) {
        CubeListBuilder leftArm = CubeListBuilder.create();
        CubeListBuilder rightArm = CubeListBuilder.create();

        // hook
        leftArm.texOffs(32, 0);
        leftArm.addBox(-2.5F, 2, -0.5F, 5, 5, 1);
        rightArm.texOffs(48, 0);
        rightArm.addBox(-2.5F, 2, -0.5F, 5, 5, 1);

        // hook base
        leftArm.texOffs(32, 6);
        leftArm.addBox(-0.5F, 0, -0.5F, 1, 2, 1);
        rightArm.texOffs(48, 6);
        rightArm.addBox(-0.5F, 0, -0.5F, 1, 2, 1);

        return createSleevedArms(leftArm, rightArm, hasSlimArms);
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

        MeshDefinition mesh = createEmptyArms(leftArm, rightArm, hasSlimArms);
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

    public static MeshDefinition createBracelet(boolean hasSlimArms, int size, int h) {
        CubeListBuilder leftArm = CubeListBuilder.create();
        CubeListBuilder rightArm = CubeListBuilder.create();

        float armWidth = hasSlimArms ? size - 1 : size;

        leftArm.texOffs(0, 0);
        leftArm.addBox(-1 - armWidth / 2, h, -1 - size / 2F, armWidth + 2, 2, 1);
        rightArm.texOffs(16, 0);
        rightArm.addBox(-1 - armWidth / 2, h, -1 - size / 2F, armWidth + 2, 2, 1);

        leftArm.texOffs(0, 3);
        leftArm.addBox(-1 - armWidth / 2, h, size / 2F, armWidth + 2, 2, 1);
        rightArm.texOffs(16, 3);
        rightArm.addBox(-1 - armWidth / 2, h, size / 2F, armWidth + 2, 2, 1);

        leftArm.texOffs(0, 6);
        leftArm.addBox(armWidth / 2, h, - size / 2F, 1, 2, size);
        rightArm.texOffs(16, 6);
        rightArm.addBox(armWidth / 2, h, - size / 2F, 1, 2, size);

        leftArm.texOffs(0, 12);
        leftArm.addBox(-1 - armWidth / 2, h, - size / 2F, 1, 2, size);
        rightArm.texOffs(16, 12);
        rightArm.addBox(-1 - armWidth / 2, h, - size / 2F, 1, 2, size);

        return createEmptyArms(leftArm, rightArm, hasSlimArms);
    }

    public static MeshDefinition createPickaxeHeater(boolean hasSlimArms) {
        CubeListBuilder leftArm = CubeListBuilder.create();
        CubeListBuilder rightArm = CubeListBuilder.create();

        float armWidth = hasSlimArms ? 3 : 4;
        float armDepth = 4;
        float deformationY = (12 + 0.5F * 2) / 12F;
        CubeDeformation deformation = new CubeDeformation(0, 2 * 0.5F / 12, 2 * 0.5F / 4);

        leftArm.texOffs(32, 0);
        leftArm.addBox(armWidth / 2 + 0.5F, 0.5F - 2 * deformationY - 1, -armDepth / 4, 1, 2, 2, deformation);
        rightArm.texOffs(48, 0);
        rightArm.addBox(- armWidth / 2 - 1.5F, 0.5F - 2 * deformationY - 1, -armDepth / 4, 1, 2, 2, deformation);

        return createSleevedArms(leftArm, rightArm, hasSlimArms);
    }
}
