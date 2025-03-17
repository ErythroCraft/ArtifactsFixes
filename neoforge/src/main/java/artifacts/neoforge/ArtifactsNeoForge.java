package artifacts.neoforge;

import artifacts.Artifacts;
import artifacts.config.screen.ArtifactsConfigScreen;
import artifacts.neoforge.event.ArtifactEventsNeoForge;
import artifacts.neoforge.event.SwimEventsNeoForge;
import artifacts.neoforge.registry.ModAttachmentTypes;
import artifacts.neoforge.registry.ModLootModifiers;
import artifacts.registry.ModAttributes;
import artifacts.registry.ModItems;
import artifacts.registry.RegistryHolder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.List;

@SuppressWarnings("unused")
@Mod(Artifacts.MOD_ID)
public class ArtifactsNeoForge {

    public ArtifactsNeoForge(IEventBus modBus) {
        Artifacts.init();
        if (FMLEnvironment.dist == Dist.CLIENT) {
            new ArtifactsNeoForgeClient(modBus);
        }

        ModLootModifiers.LOOT_MODIFIERS.register(modBus);
        ModAttachmentTypes.ATTACHMENT_TYPES.register(modBus);

        register(modBus, Registries.ATTRIBUTE, ModAttributes.ATTRIBUTES);
        register(modBus, Registries.ITEM, ModItems.ITEMS);

        modBus.addListener(ArtifactsData::gatherData);

        registerConfig();
        ArtifactEventsNeoForge.register();
        SwimEventsNeoForge.register();
    }

    private void registerConfig() {
        ModLoadingContext.get().registerExtensionPoint(
                IConfigScreenFactory.class,
                () -> (client, parent) -> new ArtifactsConfigScreen(parent).build()
        );
    }

    private <R> void register(IEventBus modBus, ResourceKey<Registry<R>> registry, List<RegistryHolder<R, ?>> holders) {
        DeferredRegister<R> register = DeferredRegister.create(registry, Artifacts.MOD_ID);
        for (RegistryHolder<R, ? extends R> holder : holders) {
            holder.bind(register.register(holder.unwrapKey().orElseThrow().location().getPath(), holder.getFactory()));
        }
        register.register(modBus);
    }
}
