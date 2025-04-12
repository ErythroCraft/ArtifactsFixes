package artifacts.integration.minecraft;

import artifacts.equipment.EquipmentSlotProvider;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

import java.util.function.BiFunction;

public class ArmorSlotProvider implements EquipmentSlotProvider {

    @Override
    public <T> T reduceEquipment(LivingEntity entity, T init, BiFunction<ItemStack, T, T> f) {
        for (ItemStack item : entity.getArmorAndBodyArmorSlots()) {
            if (!item.isEmpty()) init = f.apply(item, init);
        }
        return init;
    }

    @Override
    public boolean tryEquipItem(LivingEntity entity, ItemStack stack) {
        return false;
    }
}
