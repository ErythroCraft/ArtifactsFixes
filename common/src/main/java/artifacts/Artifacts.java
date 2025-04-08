package artifacts;

import artifacts.component.SwimEvents;
import artifacts.config.ConfigManager;
import artifacts.config.ModConfig;
import artifacts.entity.MimicEntity;
import artifacts.event.ArtifactEvents;
import artifacts.integration.equipment.EquipmentIntegrationUtils;
import artifacts.integration.equipment.VanillaEquipmentIntegration;
import artifacts.network.NetworkHandler;
import artifacts.platform.PlatformServices;
import artifacts.registry.*;
import dev.architectury.event.events.common.LifecycleEvent;
import dev.architectury.event.events.common.PlayerEvent;
import dev.architectury.registry.level.entity.EntityAttributeRegistry;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Artifacts {

    public static final String MOD_ID = "artifacts";
    public static final Logger LOGGER = LogManager.getLogger();

    public static ModConfig CONFIG;

    public static ResourceLocation id(String path) {
        return ResourceLocation.fromNamespaceAndPath(MOD_ID, path);
    }

    public static ResourceLocation id(String path, String... args) {
        return ResourceLocation.fromNamespaceAndPath(MOD_ID, String.format(path, (Object[]) args));
    }

    public static <T> ResourceKey<T> key(ResourceKey<? extends Registry<T>> registry, String path) {
        return ResourceKey.create(registry, id(path));
    }

    public static void init() {
        CONFIG = new ModConfig();

        setupIntegrations();

        NetworkHandler.register();

        ModDataComponents.register();
        ModSoundEvents.SOUND_EVENTS.register();
        ModLootConditions.LOOT_CONDITIONS.register();
        ModLootFunctions.LOOT_FUNCTIONS.register();
        ModPlacementModifierTypes.PLACEMENT_MODIFIERS.register();
        ModItems.CREATIVE_MODE_TABS.register();
        ModEntityTypes.ENTITY_TYPES.register();
        ModFeatures.FEATURES.register();
        ModAbilities.register();

        EntityAttributeRegistry.register(ModEntityTypes.MIMIC::value, MimicEntity::createMobAttributes);

        LifecycleEvent.SETUP.register(Artifacts::setupConfigs);

        LifecycleEvent.SERVER_STARTING.register(server -> CONFIG.configs.forEach(ConfigManager::readValuesFromConfig));
        PlayerEvent.PLAYER_JOIN.register(Artifacts.CONFIG.items::sendToClient);

        SwimEvents.register();
        ArtifactEvents.register();
    }

    public static void setupConfigs() {
        CONFIG.setup();
    }

    public static void setupIntegrations() {
        PlatformServices.platformHelper.setupIntegrations();

        EquipmentIntegrationUtils.registerIntegration(new VanillaEquipmentIntegration());

        EquipmentIntegrationUtils.setupIntegrations();
    }
}
