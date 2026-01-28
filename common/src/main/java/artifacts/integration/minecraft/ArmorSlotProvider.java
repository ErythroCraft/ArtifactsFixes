package artifacts.integration.minecraft;

import artifacts.equipment.EquipmentSlotProvider;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

import java.util.function.BiFunction;

public class ArmorSlotProvider implements EquipmentSlotProvider {

    @Override
    public <T> T reduceEquipment(LivingEntity entity, T init, BiFunction<ItemStack, T, T> f) {
        for (EquipmentSlot slot : EquipmentSlot.VALUES) {
            if (EquipmentSlotGroup.HAND.test(slot)) {
                continue;
            }
            ItemStack stack = entity.getItemBySlot(slot);
            if (!stack.isEmpty()) {
                init = f.apply(stack, init);
            }
        }
        return init;
    }

    @Override
    public boolean tryEquipItem(LivingEntity entity, ItemStack stack) {
        return false;
    }
}
