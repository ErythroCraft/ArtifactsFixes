package artifacts.client.mimic;

import artifacts.Artifacts;
import artifacts.client.mimic.model.MimicChestLayerModel;
import artifacts.client.mimic.model.MimicModel;
import artifacts.entity.MimicEntity;
import artifacts.integration.ModCompat;
import artifacts.integration.lootr.LootrCompat;
import artifacts.platform.PlatformServices;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.resources.model.Material;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

public class MimicChestLayer extends RenderLayer<MimicEntity, MimicModel> {

    public static final List<String> QUARK_CHEST_MATERIALS = Arrays.asList(
            "oak",
            "spruce",
            "birch",
            "cherry",
            "jungle",
            "acacia",
            "dark_oak",
            "warped",
            "crimson",
            "azalea",
            "blossom",
            "mangrove",
            "bamboo"
    );

    private final MimicChestLayerModel chestModel;
    public final Material vanillaChestMaterial;
    public final List<Material> chestMaterials;
    public final List<Material> lootrMaterials;

    public MimicChestLayer(RenderLayerParent<MimicEntity, MimicModel> entityRenderer, EntityModelSet modelSet) {
        super(entityRenderer);
        chestModel = new MimicChestLayerModel(modelSet.bakeLayer(MimicChestLayerModel.LAYER_LOCATION));
        chestMaterials = new ArrayList<>();
        lootrMaterials = new ArrayList<>();

        boolean isChristmas = isChristmas();
        vanillaChestMaterial = isChristmas ? Sheets.CHEST_XMAS_LOCATION : Sheets.CHEST_LOCATION;

        if (isChristmas) {
            chestMaterials.add(vanillaChestMaterial);
            return;
        }

        chestMaterials.add(vanillaChestMaterial);
        addQuarkMaterials(chestMaterials, "normal");

        if (PlatformServices.getModList().isModLoaded(ModCompat.LOOTR)) {
            lootrMaterials.add(createMaterial(ModCompat.LOOTR, "chest"));
            addQuarkMaterials(lootrMaterials, "lootr_normal");
        }
    }

    private static boolean isChristmas() {
        Calendar calendar = Calendar.getInstance();
        return calendar.get(Calendar.MONTH) == Calendar.DECEMBER && calendar.get(Calendar.DATE) >= 24 && calendar.get(Calendar.DATE) <= 26
                || calendar.get(Calendar.MONTH) == Calendar.APRIL && calendar.get(Calendar.DATE) == 1;
    }

    private static void addQuarkMaterials(List<Material> chestMaterials, String chestVariant) {
        if (PlatformServices.getModList().isModLoaded(ModCompat.QUARK)) {
            for (String chestMaterial : QUARK_CHEST_MATERIALS) {
                chestMaterials.add(createMaterial(ModCompat.QUARK, String.format("quark_variant_chests/%s/%s", chestMaterial, chestVariant)));
            }
        }
    }

    private static Material createMaterial(String modId, String location) {
        ResourceLocation chestAtlas = ResourceLocation.parse("textures/atlas/chest.png");
        return new Material(chestAtlas, ResourceLocation.fromNamespaceAndPath(modId, location));
    }

    @Override
    public void render(PoseStack matrixStack, MultiBufferSource buffer, int packedLight, MimicEntity mimic, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        if (!mimic.isInvisible()) {
            matrixStack.pushPose();

            matrixStack.mulPose(Axis.XP.rotationDegrees(180));
            matrixStack.translate(-0.5, -1.5, -0.5);

            getParentModel().copyPropertiesTo(chestModel);
            chestModel.prepareMobModel(mimic, limbSwing, limbSwingAmount, partialTicks);
            chestModel.setupAnim(mimic, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
            VertexConsumer builder = getChestMaterial(mimic).buffer(buffer, RenderType::entityCutout);
            chestModel.renderToBuffer(matrixStack, builder, packedLight, LivingEntityRenderer.getOverlayCoords(mimic, 0), 0xFFFFFFFF);

            matrixStack.popPose();
        }
    }

    private Material getChestMaterial(MimicEntity mimic) {
        if (!Artifacts.CONFIG.client.useModdedMimicTextures.get()) {
            return vanillaChestMaterial;
        }
        List<Material> materials = lootrMaterials.isEmpty() || LootrCompat.useVanillaTextures() ? chestMaterials : lootrMaterials;
        if (materials.size() == 1) {
            return materials.getFirst();
        }
        return materials.get((int) (Math.abs(mimic.getUUID().getMostSignificantBits()) % materials.size()));
    }
}
