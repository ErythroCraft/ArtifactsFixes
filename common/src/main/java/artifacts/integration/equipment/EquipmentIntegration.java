package artifacts.integration.equipment;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

import java.util.function.BiFunction;

public interface EquipmentIntegration {

    void setup();

    <T> T reduceEquipment(LivingEntity entity, T init, BiFunction<ItemStack, T, T> f);

    boolean equipAccessory(LivingEntity entity, ItemStack stack);

    String name();
}
