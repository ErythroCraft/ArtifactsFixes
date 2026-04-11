package artifacts.client.mimic;

import artifacts.Artifacts;
import artifacts.entity.MimicEntity;
import artifacts.integration.ModCompat;
import artifacts.integration.lootr.LootrCompat;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.resources.model.Material;
import net.minecraft.resources.Identifier;
import net.minecraft.util.SpecialDates;

import java.time.Month;
import java.time.MonthDay;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MimicChestMaterials {

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

    public final Material vanillaChestMaterial;
    public final List<Material> chestMaterials;
    public final List<Material> lootrMaterials;

    public MimicChestMaterials() {
        chestMaterials = new ArrayList<>();
        lootrMaterials = new ArrayList<>();

        boolean isChristmas = SpecialDates.isExtendedChristmas()
                || SpecialDates.dayNow().equals(MonthDay.of(Month.APRIL, 1));

        vanillaChestMaterial = isChristmas ? Sheets.CHEST_XMAS_LOCATION : Sheets.CHEST_LOCATION;

        if (isChristmas) {
            chestMaterials.add(vanillaChestMaterial);
            return;
        }

        chestMaterials.add(vanillaChestMaterial);
        addQuarkMaterials(chestMaterials, "normal");

        if (ModCompat.LOOTR.isLoaded()) {
            lootrMaterials.add(createMaterial(ModCompat.LOOTR.id("chest")));
            addQuarkMaterials(lootrMaterials, "lootr_normal");
        }
    }

    private static Material createMaterial(Identifier id) {
        Identifier chestAtlas = Identifier.withDefaultNamespace("textures/atlas/chest.png");
        return new Material(chestAtlas, id);
    }

    private static void addQuarkMaterials(List<Material> chestMaterials, String chestVariant) {
        if (ModCompat.QUARK.isLoaded()) {
            for (String chestMaterial : QUARK_CHEST_MATERIALS) {
                String path = String.format("quark_variant_chests/%s/%s", chestMaterial, chestVariant);
                chestMaterials.add(createMaterial(ModCompat.QUARK.id(path)));
            }
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

    public void setChestMaterial(MimicEntity mimic, MimicRenderState renderState) {
        renderState.chestMaterial = getChestMaterial(mimic);
    }
}
