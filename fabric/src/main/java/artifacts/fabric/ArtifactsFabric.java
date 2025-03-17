package artifacts.fabric;

import artifacts.Artifacts;
import artifacts.fabric.event.SwimEventsFabric;
import artifacts.fabric.registry.ModFeatures;
import artifacts.fabric.registry.ModLootTablesFabric;
import artifacts.registry.ModItems;
import artifacts.registry.RegistryHolder;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.loot.v2.LootTableEvents;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;

import java.util.List;

public class ArtifactsFabric implements ModInitializer {

    @Override
    public void onInitialize() {
        Artifacts.init();
        register(BuiltInRegistries.ITEM, ModItems.ITEMS);

        SwimEventsFabric.register();
        ModFeatures.register();

        // TODO loot.v3
        LootTableEvents.MODIFY.register(ModLootTablesFabric::onLootTableLoad);
    }

    @SuppressWarnings("SameParameterValue")
    private static <R> void register(Registry<R> registry, List<RegistryHolder<R, ?>> holders) {
        holders.forEach(holder -> register(registry, holder));
    }

    public static <R> void register(Registry<R> registry, RegistryHolder<R, ?> holder) {
        holder.bind(Registry.registerForHolder(registry, Artifacts.key(registry.key(), holder.unwrapKey().orElseThrow().location().getPath()), holder.getFactory().get()));
    }
}
