package artifacts.fabric;

import artifacts.Artifacts;
import artifacts.fabric.event.SwimEventsFabric;
import artifacts.fabric.network.FabricNetworkHandler;
import artifacts.fabric.registry.ModFeaturesFabric;
import artifacts.fabric.registry.ModLootTablesFabric;
import artifacts.registry.ModAbilities;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.fabricmc.fabric.api.event.registry.RegistryAttribute;
import net.fabricmc.fabric.api.loot.v3.LootTableEvents;

public class ArtifactsFabric implements ModInitializer {

    @Override
    public void onInitialize() {
        Artifacts.initConfigs();
        createRegistries();
        Artifacts.setup();
        SwimEventsFabric.register();
        ModFeaturesFabric.register();
        FabricNetworkHandler.registerClientboundPayloads();
        FabricNetworkHandler.registerServerboundPayloads();
        FabricNetworkHandler.registerServerboundReceivers();

        LootTableEvents.MODIFY.register((key, builder, source, registries) -> ModLootTablesFabric.onLootTableLoad(key, builder, source));
    }

    private void createRegistries() {
        FabricRegistryBuilder.createSimple(ModAbilities.REGISTRY_KEY)
                .attribute(RegistryAttribute.SYNCED)
                .buildAndRegister();
    }
}
