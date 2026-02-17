package artifacts.client.item.renderer;

import artifacts.client.item.ArmsModelSet;
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

public class GloveArtifactRenderer extends ArtifactRenderer {

    private final Identifier wideTexture;
    private final Identifier slimTexture;
    private final Identifier wideGlowTexture;
    private final Identifier slimGlowTexture;
    private final ArmsModelSet<HumanoidModel<HumanoidRenderState>> models;

    protected GloveArtifactRenderer(
            Identifier wideTexture,
            Identifier slimTexture,
            @Nullable Identifier wideGlowTexture,
            @Nullable Identifier slimGlowTexture,
            ArmsModelSet<HumanoidModel<HumanoidRenderState>> models
    ) {
        this.wideTexture = wideTexture;
        this.slimTexture = slimTexture;
        this.wideGlowTexture = wideGlowTexture;
        this.slimGlowTexture = slimGlowTexture;
        this.models = models;
    }

    public static GloveArtifactRenderer create(String name, ArmsModelSet<HumanoidModel<HumanoidRenderState>> models) {
        return create("%s/%s_wide".formatted(name, name), "%s/%s_slim".formatted(name, name), models);
    }

    public static GloveArtifactRenderer create(String wideTextureName, String slimTextureName, ArmsModelSet<HumanoidModel<HumanoidRenderState>> models) {
        return new GloveArtifactRenderer(getTextureId(wideTextureName), getTextureId(slimTextureName), null, null, models);
    }

    public static GloveArtifactRenderer createGlowing(String name, ArmsModelSet<HumanoidModel<HumanoidRenderState>> models) {
        return new GloveArtifactRenderer(
                getTextureId("%s/%s_wide".formatted(name, name)),
                getTextureId("%s/%s_slim".formatted(name, name)),
                getTextureId("%s/%s_wide_overlay".formatted(name, name)),
                getTextureId("%s/%s_slim_overlay".formatted(name, name)),
                models
        );
    }

    private static PlayerModelType getModelType(LivingEntityRenderState renderState) {
        return renderState instanceof AvatarRenderState avatarRenderState ? avatarRenderState.skin.model() : PlayerModelType.WIDE;
    }

    private static HumanoidArm getArm(HumanoidRenderState renderState, int slotIndex) {
        InteractionHand hand = slotIndex % 2 == 0 ? InteractionHand.MAIN_HAND : InteractionHand.OFF_HAND;
        return hand == InteractionHand.MAIN_HAND ? renderState.mainArm : renderState.mainArm.getOpposite();
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
        if (getModelType(renderState) == PlayerModelType.SLIM) {
            return slimTexture;
        }
        return wideTexture;
    }

    @Override
    protected @Nullable Identifier getFullBrightOverlayTexture(HumanoidRenderState renderState, int slotIndex) {
        if (getModelType(renderState) == PlayerModelType.SLIM) {
            return slimGlowTexture;
        }
        return wideGlowTexture;
    }

    @Override
    protected HumanoidModel<HumanoidRenderState> getModel(HumanoidRenderState renderState, int slotIndex) {
        return models.get(getArm(renderState, slotIndex), getModelType(renderState));
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
