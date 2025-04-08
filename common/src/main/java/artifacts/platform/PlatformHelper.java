package artifacts.platform;

import artifacts.component.AbilityToggles;
import artifacts.component.SwimData;
import artifacts.integration.ModCompat;
import artifacts.integration.equipment.EquipmentIntegrationUtils;
import artifacts.integration.equipment.client.ClientEquipmentIntegrationUtils;
import artifacts.integration.impl.accessories.AccessoriesClientIntegration;
import artifacts.integration.impl.accessories.AccessoriesIntegration;
import artifacts.integration.impl.trinkets.TrinketsClientIntegration;
import artifacts.integration.impl.trinkets.TrinketsIntegration;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Path;
import java.util.function.Consumer;
import java.util.function.Supplier;

public interface PlatformHelper {

    @Nullable
    AbilityToggles getAbilityToggles(LivingEntity entity);

    @Nullable
    SwimData getSwimData(LivingEntity entity);

    Holder<Attribute> getSwimSpeedAttribute();

    // TODO register attributes properly
    Holder<Attribute> registerAttribute(String name, Supplier<? extends Attribute> supplier);

    boolean isEyeInWater(Player player);

    boolean areBootsHidden(LivingEntity entity);

    boolean isFishingRod(ItemStack stack);

    Path getConfigDir();

    void registryEntryAddCallback(Consumer<Item> consumer);

    boolean isModLoaded(String modid);

    default void setupIntegrations() {
        if (PlatformServices.platformHelper.isModLoaded(ModCompat.TRINKETS) && !PlatformServices.platformHelper.isModLoaded(ModCompat.TCLAYER)) {
            EquipmentIntegrationUtils.registerIntegration(new TrinketsIntegration());
        }

        if (PlatformServices.platformHelper.isModLoaded(ModCompat.ACCESSORIES)) {
            EquipmentIntegrationUtils.registerIntegration(new AccessoriesIntegration());
        }
    }

    default void setupClientIntegrations() {
        if (PlatformServices.platformHelper.isModLoaded(ModCompat.TRINKETS) && !PlatformServices.platformHelper.isModLoaded(ModCompat.TCLAYER)) {
            ClientEquipmentIntegrationUtils.registerIntegration(new TrinketsClientIntegration());
        }

        if (PlatformServices.platformHelper.isModLoaded(ModCompat.ACCESSORIES)) {
            ClientEquipmentIntegrationUtils.registerIntegration(new AccessoriesClientIntegration());
        }
    }
}
