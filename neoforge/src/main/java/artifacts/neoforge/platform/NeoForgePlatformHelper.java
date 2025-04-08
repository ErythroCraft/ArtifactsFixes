package artifacts.neoforge.platform;

import artifacts.component.AbilityToggles;
import artifacts.component.SwimData;
import artifacts.integration.ModCompat;
import artifacts.integration.equipment.EquipmentIntegrationUtils;
import artifacts.integration.equipment.client.ClientEquipmentIntegrationUtils;
import artifacts.neoforge.integration.cosmeticarmor.CosmeticArmorCompat;
import artifacts.neoforge.integration.curios.CuriosClientIntegration;
import artifacts.neoforge.integration.curios.CuriosIntegration;
import artifacts.neoforge.registry.ModAttachmentTypes;
import artifacts.neoforge.registry.NeoForgeRegister;
import artifacts.platform.PlatformHelper;
import artifacts.platform.PlatformServices;
import artifacts.registry.ModEntityTypes;
import artifacts.registry.Register;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SpawnEggItem;
import net.neoforged.fml.ModList;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.fml.loading.FMLPaths;
import net.neoforged.neoforge.common.DeferredSpawnEggItem;
import net.neoforged.neoforge.common.ItemAbilities;
import net.neoforged.neoforge.common.NeoForgeMod;
import net.neoforged.neoforge.registries.callback.AddCallback;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Path;
import java.util.function.Consumer;

public class NeoForgePlatformHelper implements PlatformHelper {

    @Nullable
    @Override
    public AbilityToggles getAbilityToggles(LivingEntity entity) {
        return entity.getData(ModAttachmentTypes.ABILITY_TOGGLES);
    }

    @Nullable
    @Override
    public SwimData getSwimData(LivingEntity entity) {
        return entity.getData(ModAttachmentTypes.SWIM_DATA);
    }

    @Override
    public Holder<Attribute> getSwimSpeedAttribute() {
        return NeoForgeMod.SWIM_SPEED;
    }

    @Override
    public boolean isEyeInWater(Player player) {
        return player.isEyeInFluidType(NeoForgeMod.WATER_TYPE.value());
    }

    @Override
    public boolean areBootsHidden(LivingEntity entity) {
        if (entity instanceof Player player && ModList.get().isLoaded("cosmeticarmorreworked")) {
            return CosmeticArmorCompat.areBootsHidden(player);
        }
        return false;
    }

    @Override
    public boolean isFishingRod(ItemStack stack) {
        return stack.canPerformAction(ItemAbilities.FISHING_ROD_CAST);
    }

    @Override
    public Path getConfigDir() {
        return FMLPaths.CONFIGDIR.get();
    }

    @Override
    public void registryEntryAddCallback(Consumer<Item> consumer) {
        BuiltInRegistries.ITEM.addCallback((AddCallback<Item>) (registry, i, key, item) -> consumer.accept(item));
    }

    @Override
    public boolean isModLoaded(String modid) {
        return ModList.get().isLoaded(modid);
    }

    @Override
    public boolean isDedicatedServer() {
        return !FMLEnvironment.dist.isClient();
    }

    @Override
    public <R> Register<R> createRegister(ResourceKey<Registry<R>> registry) {
        return new NeoForgeRegister<>(registry);
    }

    @Override
    public SpawnEggItem createMimicSpawnEgg(Item.Properties properties) {
        return new DeferredSpawnEggItem(ModEntityTypes.MIMIC, 0xFFFFFF, 0xFFFFFF, properties);
    }

    public void setupIntegrations() {
        PlatformHelper.super.setupIntegrations();

        if (PlatformServices.platformHelper.isModLoaded(ModCompat.CURIOS) && !PlatformServices.platformHelper.isModLoaded(ModCompat.CCLAYER)) {
            EquipmentIntegrationUtils.registerIntegration(new CuriosIntegration());
        }
    }

    @Override
    public void setupClientIntegrations() {
        PlatformHelper.super.setupClientIntegrations();

        if (PlatformServices.platformHelper.isModLoaded(ModCompat.CURIOS) && !PlatformServices.platformHelper.isModLoaded(ModCompat.CCLAYER)) {
            ClientEquipmentIntegrationUtils.registerIntegration(new CuriosClientIntegration());
        }
    }
}
