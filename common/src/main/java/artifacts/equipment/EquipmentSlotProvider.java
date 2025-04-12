package artifacts.equipment;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

import java.util.function.BiFunction;

public interface EquipmentSlotProvider {

    <T> T reduceEquipment(LivingEntity entity, T init, BiFunction<ItemStack, T, T> f);

    boolean tryEquipItem(LivingEntity entity, ItemStack stack);

}
