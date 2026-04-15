package artifacts.client.mimic;

import artifacts.Artifacts;
import artifacts.entity.MimicEntity;
import artifacts.integration.ModCompat;
import artifacts.integration.lootr.LootrCompat;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.resources.model.sprite.SpriteId;
import net.minecraft.resources.Identifier;
import net.minecraft.util.SpecialDates;

import java.time.Month;
import java.time.MonthDay;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

// TODO (Quark 26.1+): Update quark texture path
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

    public final SpriteId vanillaChestMaterial;
    public final List<SpriteId> chestMaterials;
    public final List<SpriteId> lootrMaterials;

    public MimicChestMaterials() {
        chestMaterials = new ArrayList<>();
        lootrMaterials = new ArrayList<>();

        boolean isChristmas = SpecialDates.isExtendedChristmas()
                || SpecialDates.dayNow().equals(MonthDay.of(Month.APRIL, 1));

        vanillaChestMaterial = isChristmas ? Sheets.CHEST_CHRISTMAS.single() : Sheets.CHEST_REGULAR.single();

        if (isChristmas) {
            chestMaterials.add(vanillaChestMaterial);
            return;
        }

        chestMaterials.add(vanillaChestMaterial);
        addQuarkMaterials(chestMaterials, "normal");

        if (ModCompat.LOOTR.isLoaded()) {
            lootrMaterials.add(createMaterial(ModCompat.LOOTR.id("entity/chest/normal")));
            addQuarkMaterials(lootrMaterials, "lootr_normal");
        }
    }

    private static SpriteId createMaterial(Identifier id) {
        return new SpriteId(Sheets.CHEST_SHEET, id);
    }

    private static void addQuarkMaterials(List<SpriteId> chestMaterials, String chestVariant) {
        if (ModCompat.QUARK.isLoaded()) {
            for (String chestMaterial : QUARK_CHEST_MATERIALS) {
                String path = String.format("quark_variant_chests/%s/%s", chestMaterial, chestVariant);
                chestMaterials.add(createMaterial(ModCompat.QUARK.id(path)));
            }
        }
    }

    private SpriteId getChestMaterial(MimicEntity mimic) {
        if (!Artifacts.CONFIG.client.useModdedMimicTextures.get()) {
            return vanillaChestMaterial;
        }
        List<SpriteId> materials = lootrMaterials.isEmpty() || LootrCompat.useVanillaTextures() ? chestMaterials : lootrMaterials;
        if (materials.size() == 1) {
            return materials.getFirst();
        }
        return materials.get((int) (Math.abs(mimic.getUUID().getMostSignificantBits()) % materials.size()));
    }

    public void setChestMaterial(MimicEntity mimic, MimicRenderState renderState) {
        renderState.chestMaterial = getChestMaterial(mimic);
    }
}
