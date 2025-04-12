package artifacts.equipment;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.function.BiFunction;

public class EquipmentSlotManager {

    private static final Set<EquipmentSlotProvider> SLOT_PROVIDERS = new LinkedHashSet<>();

    public static void register(EquipmentSlotProvider integration) {
        SLOT_PROVIDERS.add(integration);
    }

    protected static <T> T reduceEquipment(LivingEntity entity, T init, BiFunction<ItemStack, T, T> f) {
        for (EquipmentSlotProvider integration : SLOT_PROVIDERS) {
            init = integration.reduceEquipment(entity, init, f);
        }
        return init;
    }

    public static boolean tryEquipAccessory(LivingEntity entity, ItemStack stack) {
        for (EquipmentSlotProvider integration : SLOT_PROVIDERS) {
            if (integration.tryEquipItem(entity, stack)) {
                return true;
            }
        }
        return false;
    }
}
