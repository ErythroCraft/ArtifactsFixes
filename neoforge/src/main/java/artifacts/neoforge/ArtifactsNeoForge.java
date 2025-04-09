package artifacts.neoforge;

import artifacts.Artifacts;
import artifacts.config.screen.ArtifactsConfigScreen;
import artifacts.integration.ModCompat;
import artifacts.neoforge.event.ArtifactEventsNeoForge;
import artifacts.neoforge.event.SwimEventsNeoForge;
import artifacts.neoforge.network.NeoForgeNetworkHandler;
import artifacts.neoforge.registry.ModAttachmentTypes;
import artifacts.neoforge.registry.ModLootModifiers;
import artifacts.platform.PlatformServices;
import artifacts.registry.ModAbilities;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NewRegistryEvent;
import net.neoforged.neoforge.registries.RegistryBuilder;

@Mod(Artifacts.MOD_ID)
public class ArtifactsNeoForge {

    private static IEventBus modBus;

    public ArtifactsNeoForge(IEventBus modBus) {
        ArtifactsNeoForge.modBus = modBus;

        Artifacts.initConfigs();
        Artifacts.setup();
        if (FMLEnvironment.dist == Dist.CLIENT) {
            new ArtifactsNeoForgeClient(modBus);
        }

        ModLootModifiers.LOOT_MODIFIERS.register(modBus);
        ModAttachmentTypes.ATTACHMENT_TYPES.register(modBus);

        modBus.addListener(ArtifactsData::gatherData);
        modBus.addListener(this::createRegistries);
        modBus.addListener(NeoForgeNetworkHandler::registerPayloadHandlers);

        registerConfig();
        ArtifactEventsNeoForge.register();
        SwimEventsNeoForge.register();

        ArtifactsNeoForge.modBus = null;
    }

    private void registerConfig() {
        if (PlatformServices.platformHelper.isModLoaded(ModCompat.CLOTH_CONFIG)) {
            ModLoadingContext.get().registerExtensionPoint(
                    IConfigScreenFactory.class,
                    () -> (client, parent) -> new ArtifactsConfigScreen(parent).build()
            );
        }
    }

    private void createRegistries(NewRegistryEvent event) {
        event.create(new RegistryBuilder<>(ModAbilities.REGISTRY_KEY).sync(true));
    }

    public static void addDeferredRegister(DeferredRegister<?> register) {
        register.register(modBus);
    }
}
