package artifacts;

import artifacts.component.SwimEvents;
import artifacts.config.ConfigManager;
import artifacts.config.ModConfig;
import artifacts.entity.MimicEntity;
import artifacts.event.ArtifactHooks;
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

    public static void setup() {
        setupIntegrations();

        NetworkHandler.register();

        ModDataComponents.DATA_COMPONENT_TYPES.register();
        ModSoundEvents.SOUND_EVENTS.register();
        ModLootConditions.LOOT_CONDITION_TYPES.register();
        ModLootFunctions.LOOT_FUNCTION_TYPES.register();
        ModPlacementModifierTypes.PLACEMENT_MODIFIER_TYPES.register();
        ModAttributes.ATTRIBUTES.register();
        ModEntityTypes.ENTITY_TYPES.register();
        ModItems.ITEMS.register();
        ModItems.CREATIVE_MODE_TABS.register();
        ModFeatures.FEATURES.register();
        ModAbilities.ABILITIES.register();

        EntityAttributeRegistry.register(ModEntityTypes.MIMIC, MimicEntity::createMobAttributes);

        LifecycleEvent.SETUP.register(Artifacts::setupConfigs);

        LifecycleEvent.SERVER_STARTING.register(server -> CONFIG.configs.forEach(ConfigManager::readValuesFromConfig));
        PlayerEvent.PLAYER_JOIN.register(Artifacts.CONFIG.items::sendToClient);

        SwimEvents.register();
        ArtifactHooks.register();
    }

    public static void initConfigs() {
        CONFIG = new ModConfig();
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
