package artifacts.fabric.registry;

import artifacts.Artifacts;
import artifacts.registry.ModItems;
import net.fabricmc.fabric.api.creativetab.v1.FabricCreativeModeTab;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

public class ModItemsFabric {

    public static void registerCreativeModeTab() {
        ModItems.CREATIVE_MODE_TABS.register("main", () -> FabricCreativeModeTab.builder()
                .title(Component.translatable("%s.creative_tab".formatted(Artifacts.MOD_ID)))
                .icon(() -> new ItemStack(ModItems.BUNNY_HOPPERS.value()))
                .displayItems((_, output) -> ModItems.ITEMS.forEach(output::accept))
                .build()
        );
    }
}
