package artifacts.fabric;

import artifacts.Artifacts;
import artifacts.fabric.event.SwimEventsFabric;
import artifacts.fabric.registry.ModFeaturesFabric;
import artifacts.fabric.registry.ModLootTablesFabric;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.loot.v3.LootTableEvents;

public class ArtifactsFabric implements ModInitializer {

    @Override
    public void onInitialize() {
        Artifacts.init();
        SwimEventsFabric.register();
        ModFeaturesFabric.register();

        LootTableEvents.MODIFY.register((key, builder, source, registries) -> ModLootTablesFabric.onLootTableLoad(key, builder, source));
    }
}
