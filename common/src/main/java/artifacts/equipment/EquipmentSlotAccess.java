package artifacts.equipment;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.item.ItemStack;

public interface EquipmentSlotAccess extends SlotAccess {

    ItemStack get();

    @Override
    default boolean set(ItemStack stack) {
        return false;
    }

    void broadcastBreakEvent(LivingEntity entity);
}
