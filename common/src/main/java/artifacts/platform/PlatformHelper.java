package artifacts.platform;

import artifacts.component.AbilityToggles;
import artifacts.component.SwimData;
import artifacts.integration.EquipmentIntegrationConstants;
import artifacts.integration.EquipmentIntegrationUtils;
import artifacts.integration.client.ClientEquipmentIntegrationUtils;
import artifacts.integration.impl.accessories.AccessoriesClientIntegration;
import artifacts.integration.impl.accessories.AccessoriesIntegration;
import artifacts.integration.impl.trinkets.TrinketClientIntegration;
import artifacts.integration.impl.trinkets.TrinketIntegration;
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
        if (PlatformServices.platformHelper.isModLoaded(EquipmentIntegrationConstants.TRINKETS) && !PlatformServices.platformHelper.isModLoaded("tclayer")) {
            EquipmentIntegrationUtils.registerIntegration(new TrinketIntegration());
        }

        if (PlatformServices.platformHelper.isModLoaded(EquipmentIntegrationConstants.ACCESSORIES)) {
            EquipmentIntegrationUtils.registerIntegration(new AccessoriesIntegration());
        }
    }

    default void setupClientIntegrations() {
        if (PlatformServices.platformHelper.isModLoaded(EquipmentIntegrationConstants.TRINKETS) && !PlatformServices.platformHelper.isModLoaded("tclayer")) {
            ClientEquipmentIntegrationUtils.registerIntegration(new TrinketClientIntegration());
        }

        if (PlatformServices.platformHelper.isModLoaded(EquipmentIntegrationConstants.ACCESSORIES)) {
            ClientEquipmentIntegrationUtils.registerIntegration(new AccessoriesClientIntegration());
        }
    }
}
