package artifacts.neoforge.data;

import artifacts.Artifacts;
import artifacts.registry.ModItems;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.world.item.Item;

public class ItemModels extends ItemModelProvider {

    public ItemModels(PackOutput packOutput) {
        super(packOutput, Artifacts.MOD_ID);
    }

    @Override
    protected void registerModels() {
        BuiltInRegistries.ITEM.stream()
                .filter(item -> BuiltInRegistries.ITEM.getKey(item).getNamespace().equals(Artifacts.MOD_ID))
                .filter(item -> item != ModItems.MIMIC_SPAWN_EGG.value())
                .filter(item -> item != ModItems.UMBRELLA.value())
                .forEach(this::addGeneratedModel);
    }

    private void addGeneratedModel(Item item) {
        String name = BuiltInRegistries.ITEM.getKey(item).getPath();
        withExistingParent("item/" + name, "item/generated").texture("layer0", Artifacts.id("item/%s", name));
    }
}
