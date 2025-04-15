package artifacts.fabric.platform;

import artifacts.component.SwimData;
import artifacts.fabric.registry.FabricRegister;
import artifacts.fabric.registry.ModAttributesFabric;
import artifacts.fabric.registry.ModComponents;
import artifacts.platform.PlatformHelper;
import artifacts.registry.ModEntityTypes;
import artifacts.registry.Register;
import net.fabricmc.fabric.api.event.registry.RegistryEntryAddedCallback;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.FishingRodItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SpawnEggItem;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Path;
import java.util.function.Consumer;

public class FabricPlatformHelper implements PlatformHelper {

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
    public boolean isEyeInWater(Player player) {
        return player.isEyeInFluid(FluidTags.WATER);
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
    public void addItemRegistryCallback(Consumer<Item> consumer) {
        RegistryEntryAddedCallback.event(BuiltInRegistries.ITEM)
                .register((i, resourceLocation, item) -> consumer.accept(item));
    }

    @Override
    public boolean isModLoaded(String modid) {
        return FabricLoader.getInstance().isModLoaded(modid);
    }

    @Override
    public <R> Register<R> createRegister(ResourceKey<Registry<R>> registry) {
        return new FabricRegister<>(registry);
    }

    @Override
    public SpawnEggItem createMimicSpawnEgg(Item.Properties properties) {
        return new SpawnEggItem(ModEntityTypes.MIMIC.get(), 0xFFFFFF, 0xFFFFFF, properties);
    }
}
