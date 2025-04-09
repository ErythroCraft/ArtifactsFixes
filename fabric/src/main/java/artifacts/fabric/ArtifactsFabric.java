package artifacts.fabric;

import artifacts.Artifacts;
import artifacts.event.ArtifactHooks;
import artifacts.fabric.event.SwimmingHooksFabric;
import artifacts.fabric.network.FabricNetworkHandler;
import artifacts.fabric.registry.ModFeaturesFabric;
import artifacts.fabric.registry.ModLootTablesFabric;
import artifacts.registry.ModAbilities;
import artifacts.registry.ModEntityTypes;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.fabricmc.fabric.api.event.registry.RegistryAttribute;
import net.fabricmc.fabric.api.loot.v3.LootTableEvents;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;

public class ArtifactsFabric implements ModInitializer {

    @Override
    public void onInitialize() {
        Artifacts.initConfigs();
        createRegistries();
        Artifacts.setup();
        Artifacts.onCommonSetup();
        SwimmingHooksFabric.register();
        ModFeaturesFabric.register();
        FabricNetworkHandler.registerClientboundPayloads();
        FabricNetworkHandler.registerServerboundPayloads();
        FabricNetworkHandler.registerServerboundReceivers();
        ModEntityTypes.registerMobAttributes(FabricDefaultAttributeRegistry::register);


        ServerEntityEvents.ENTITY_LOAD.register((entity, level) -> ArtifactHooks.onEntityAdded(entity));

        ServerLifecycleEvents.SERVER_STARTING.register(Artifacts::onServerStarting);
        ServerLifecycleEvents.SERVER_STOPPING.register(server -> Artifacts.onServerStopping());
        LootTableEvents.MODIFY.register((key, builder, source, registries) -> ModLootTablesFabric.onLootTableLoad(key, builder, source));
    }

    private void createRegistries() {
        FabricRegistryBuilder.createSimple(ModAbilities.REGISTRY_KEY)
                .attribute(RegistryAttribute.SYNCED)
                .buildAndRegister();
    }
}
