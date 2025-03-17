package artifacts.fabric.platform;

import artifacts.Artifacts;
import artifacts.component.AbilityToggles;
import artifacts.component.SwimData;
import artifacts.fabric.registry.ModAttributesFabric;
import artifacts.fabric.registry.ModComponents;
import artifacts.platform.PlatformHelper;
import net.fabricmc.fabric.api.event.registry.RegistryEntryAddedCallback;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.FishingRodItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Path;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class FabricPlatformHelper implements PlatformHelper {

    @Nullable
    @Override
    public AbilityToggles getAbilityToggles(LivingEntity entity) {
        return ModComponents.ABILITY_TOGGLES.getNullable(entity);
    }

    @Nullable
    @Override
    public SwimData getSwimData(LivingEntity entity) {
        return ModComponents.SWIM_DATA.getNullable(entity);
    }

    @Override
    public Holder<Attribute> getSwimSpeedAttribute() {
        return ModAttributesFabric.SWIM_SPEED;
    }

    @Override
    public Holder<Attribute> registerAttribute(String name, Supplier<? extends Attribute> supplier) {
        return Registry.registerForHolder(BuiltInRegistries.ATTRIBUTE, Artifacts.key(Registries.ATTRIBUTE, name), supplier.get());
    }

    @Override
    public boolean isEyeInWater(Player player) {
        return player.isEyeInFluid(FluidTags.WATER);
    }

    @Override
    public boolean areBootsHidden(LivingEntity entity) {
        return false;
    }

    @Override
    public boolean isFishingRod(ItemStack stack) {
        return stack.getItem() instanceof FishingRodItem;
    }

    @Override
    public Path getConfigDir() {
        return FabricLoader.getInstance().getConfigDir();
    }

    @Override
    public void registryEntryAddCallback(Consumer<Item> consumer) {
        RegistryEntryAddedCallback.event(BuiltInRegistries.ITEM)
                .register((i, resourceLocation, item) -> consumer.accept(item));
    }

    @Override
    public boolean isModLoaded(String modid) {
        return FabricLoader.getInstance().isModLoaded(modid);
    }
}
