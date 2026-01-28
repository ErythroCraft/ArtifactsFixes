package artifacts.neoforge.data.tags;

import artifacts.Artifacts;
import artifacts.registry.ModTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.DamageTypeTagsProvider;
import net.minecraft.world.damagesource.DamageTypes;

import java.util.concurrent.CompletableFuture;

public class DamageTypeTags extends DamageTypeTagsProvider {

    public DamageTypeTags(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(packOutput, lookupProvider, Artifacts.MOD_ID);
    }

    @Override
    protected void addTags(HolderLookup.Provider arg) {
        tag(ModTags.IS_HOT_FLOOR).add(DamageTypes.HOT_FLOOR);
    }
}
