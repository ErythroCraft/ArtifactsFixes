package artifacts.client.item.renderer;

import artifacts.client.item.model.ArmsModel;
import artifacts.equipment.client.EquipmentRenderingManager;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.resources.Identifier;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.player.PlayerModelType;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

public class GloveArtifactRenderer extends ArtifactRenderer {

    private final Identifier wideTexture;
    private final Identifier slimTexture;
    private final Identifier wideGlowTexture;
    private final Identifier slimGlowTexture;
    private final ArmsModel wideModel;
    private final ArmsModel slimModel;

    protected GloveArtifactRenderer(
            Identifier wideTexture,
            Identifier slimTexture,
            @Nullable Identifier wideGlowTexture,
            @Nullable Identifier slimGlowTexture,
            ArmsModel wideModel,
            ArmsModel slimModel
    ) {
        this.wideTexture = wideTexture;
        this.slimTexture = slimTexture;
        this.wideGlowTexture = wideGlowTexture;
        this.slimGlowTexture = slimGlowTexture;
        this.wideModel = wideModel;
        this.slimModel = slimModel;
    }

    public static GloveArtifactRenderer create(String name, Function<Boolean, ArmsModel> modelFactory) {
        return create("%s_wide".formatted(name), "%s_slim".formatted(name), modelFactory);
    }

    public static GloveArtifactRenderer create(String wideTextureName, String slimTextureName, Function<Boolean, ArmsModel> modelFactory) {
        return create(getTextureId(wideTextureName), getTextureId(slimTextureName), modelFactory);
    }

    private static GloveArtifactRenderer create(Identifier wideTexture, Identifier slimTexture, Function<Boolean, ArmsModel> modelFactory) {
        return new GloveArtifactRenderer(wideTexture, slimTexture, null, null, modelFactory.apply(false), modelFactory.apply(true));
    }

    @Nullable
    public static GloveArtifactRenderer getGloveRenderer(ItemStack stack) {
        if (!stack.isEmpty() && EquipmentRenderingManager.getArtifactRenderer(stack.getItem()) instanceof GloveArtifactRenderer gloveRenderer) {
            return gloveRenderer;
        }
        return null;
    }

    @Override
    protected Identifier getTexture(HumanoidRenderState renderState, int slotIndex) {
        return hasSlimArms(renderState) ? slimTexture : wideTexture;
    }

    @Override
    protected @Nullable Identifier getFullBrightOverlayTexture(HumanoidRenderState renderState, int slotIndex) {
        return hasSlimArms(renderState) ? slimGlowTexture : wideGlowTexture;
    }

    @Override
    protected HumanoidModel<HumanoidRenderState> getModel(HumanoidRenderState renderState, int slotIndex) {
        HumanoidModel<HumanoidRenderState> model = hasSlimArms(renderState) ? slimModel : wideModel;
        model.setAllVisible(false);
        model.getArm(getArm(renderState, slotIndex)).visible = true;
        return model;
    }

    protected static boolean hasSlimArms(LivingEntityRenderState renderState) {
        return renderState instanceof AvatarRenderState avatarRenderState && avatarRenderState.skin.model() == PlayerModelType.SLIM;
    }

    private static HumanoidArm getArm(HumanoidRenderState renderState, int slotIndex) {
        InteractionHand hand = slotIndex % 2 == 0 ? InteractionHand.MAIN_HAND : InteractionHand.OFF_HAND;
        return hand == InteractionHand.MAIN_HAND ? renderState.mainArm : renderState.mainArm.getOpposite();
    }

    public final void renderFirstPersonArm(PoseStack matrixStack, MultiBufferSource buffer, int light, AbstractClientPlayer player, HumanoidArm side, boolean hasFoil) {
        /* TODO fix first person rendering
        if (!player.isSpectator()) {
            boolean hasSlimArms = hasSlimArms(player);
            ArmsModel model = getModel(hasSlimArms);

            ModelPart arm = side == HumanoidArm.LEFT ? model.leftArm : model.rightArm;
            model.setAllVisible(false);
            arm.visible = true;

            model.crouching = false;
            model.attackTime = model.swimAmount = 0;
            model.setupAnim(player, 0, 0, 0, 0, 0);
            arm.xRot = 0;

            renderFirstPersonArm(model, arm, matrixStack, buffer, light, hasSlimArms, hasFoil);
        }
        */
    }
}
