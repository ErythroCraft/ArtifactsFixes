package artifacts.integration;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

import java.util.function.BiFunction;
import java.util.function.Consumer;

public interface EquipmentIntegration {

    void setup();

    void iterateEquippedAccessories(LivingEntity entity, Consumer<ItemStack> consumer);

    <T> T reduceAccessories(LivingEntity entity, T init, BiFunction<ItemStack, T, T> f);

    boolean equipAccessory(LivingEntity entity, ItemStack stack);

    String name();
}
