package artifacts.platform;

import artifacts.component.SwimData;
import artifacts.registry.Register;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SpawnEggItem;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Path;
import java.util.function.Consumer;

public interface PlatformHelper {

    @Nullable
    SwimData getSwimData(LivingEntity entity);

    Holder<Attribute> getSwimSpeedAttribute();

    boolean isFishingRod(ItemStack stack);

    Path getConfigDir();

    void addItemRegistryCallback(Consumer<Item> consumer);

    boolean isModLoaded(String modid);

    <R> Register<R> createRegister(ResourceKey<Registry<R>> registry);

    SpawnEggItem createMimicSpawnEgg(Item.Properties properties);

}
