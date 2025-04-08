package artifacts.fabric;

import artifacts.Artifacts;
import artifacts.fabric.event.SwimEventsFabric;
import artifacts.fabric.registry.ModFeaturesFabric;
import artifacts.fabric.registry.ModLootTablesFabric;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.loot.v2.LootTableEvents;

public class ArtifactsFabric implements ModInitializer {

    @Override
    public void onInitialize() {
        Artifacts.init();
        SwimEventsFabric.register();
        ModFeaturesFabric.register();

        // TODO loot.v3
        LootTableEvents.MODIFY.register(ModLootTablesFabric::onLootTableLoad);
    }
}
