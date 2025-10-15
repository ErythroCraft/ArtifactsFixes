package artifacts.fabric;

import artifacts.Artifacts;
import artifacts.event.ArtifactHooks;
import artifacts.fabric.event.ArtifactHooksFabric;
import artifacts.fabric.network.FabricNetworkHandler;
import artifacts.fabric.registry.ModFeaturesFabric;
import artifacts.fabric.registry.ModLootTablesFabric;
import artifacts.fabric.registry.ModResourceConditions;
import artifacts.registry.ModEntityTypes;
import artifacts.registry.ModGameEvents;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.loot.v3.LootTableEvents;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.fabricmc.fabric.api.registry.SculkSensorFrequencyRegistry;

public class ArtifactsFabric implements ModInitializer {

    @Override
    public void onInitialize() {
        Artifacts.setup();
        ArtifactHooksFabric.register();
        ModFeaturesFabric.register();
        ModResourceConditions.register();
        FabricNetworkHandler.registerClientboundPayloads();
        FabricNetworkHandler.registerServerboundPayloads();
        FabricNetworkHandler.registerServerboundReceivers();
        ModEntityTypes.registerMobAttributes(FabricDefaultAttributeRegistry::register);

        ServerEntityEvents.ENTITY_LOAD.register((entity, level) -> ArtifactHooks.onEntityAdded(entity));

        ModGameEvents.VIBRATION_FREQUENCIES.forEach((holder, frequency) ->
                SculkSensorFrequencyRegistry.register(holder.unwrapKey().orElseThrow(), frequency)
        );

        ServerLifecycleEvents.SERVER_STARTING.register(Artifacts::onServerStarting);
        ServerLifecycleEvents.SERVER_STOPPING.register(server -> Artifacts.onServerStopping());
        LootTableEvents.MODIFY.register((key, builder, source, registries) -> ModLootTablesFabric.onLootTableLoad(key, builder, source));
    }
}
