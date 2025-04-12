package artifacts.integration.equipment;

import artifacts.integration.ModCompat;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

import java.util.function.BiFunction;

public class VanillaEquipmentIntegration implements EquipmentIntegration {

    @Override
    public void setup() {}

    @Override
    public <T> T reduceEquipment(LivingEntity entity, T init, BiFunction<ItemStack, T, T> f) {
        for (ItemStack item : entity.getArmorAndBodyArmorSlots()) {
            if (!item.isEmpty()) init = f.apply(item, init);
        }
        return init;
    }

    @Override
    public boolean equipAccessory(LivingEntity entity, ItemStack stack) {
        return false;
    }

    @Override
    public String name() {
        return ModCompat.MINECRAFT;
    }
}
