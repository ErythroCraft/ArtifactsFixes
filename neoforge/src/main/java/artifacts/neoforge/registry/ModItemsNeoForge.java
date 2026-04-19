package artifacts.neoforge.registry;

import artifacts.Artifacts;
import artifacts.registry.ModItems;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;

public class ModItemsNeoForge {

    // TODO: move this back to common
    public static void registerCreativeModeTab() {
        ModItems.CREATIVE_MODE_TABS.register("main", () -> CreativeModeTab.builder()
                .title(Component.translatable("%s.creative_tab".formatted(Artifacts.MOD_ID)))
                .icon(() -> new ItemStack(ModItems.BUNNY_HOPPERS.value()))
                .displayItems((_, output) -> ModItems.ITEMS.forEach(output::accept))
                .build()
        );
        ModItems.CREATIVE_MODE_TABS.register();
    }
}
