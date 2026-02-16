package artifacts.neoforge.data;

import artifacts.Artifacts;
import artifacts.registry.ModItems;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.ModelProvider;
import net.minecraft.client.data.models.model.ModelTemplates;
import net.minecraft.core.Holder;
import net.minecraft.data.PackOutput;

public class ItemModels extends ModelProvider {

    public ItemModels(PackOutput packOutput) {
        super(packOutput, Artifacts.MOD_ID);
    }

    @Override
    protected void registerModels(BlockModelGenerators blockModels, ItemModelGenerators itemModels) {
       getKnownItems()
               .map(Holder::value)
               .filter(item -> item != ModItems.MIMIC_SPAWN_EGG.value())
               .filter(item -> item != ModItems.UMBRELLA.value())
               .forEach(item -> itemModels.generateFlatItem(item, ModelTemplates.FLAT_ITEM));

       itemModels.declareCustomModelItem(ModItems.MIMIC_SPAWN_EGG.value());
       itemModels.declareCustomModelItem(ModItems.UMBRELLA.value());
    }
}
