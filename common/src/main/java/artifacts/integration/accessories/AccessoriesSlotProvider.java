package artifacts.integration.accessories;

import artifacts.equipment.EquipmentSlotProvider;
import io.wispforest.accessories.api.AccessoriesCapability;
import io.wispforest.accessories.api.equip.EquipAction;
import io.wispforest.accessories.api.slot.SlotEntryReference;
import io.wispforest.accessories.api.slot.SlotReference;
import it.unimi.dsi.fastutil.Pair;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

import java.util.function.BiFunction;

public class AccessoriesSlotProvider implements EquipmentSlotProvider {

    @Override
    public <T> T reduceEquipment(LivingEntity entity, T init, BiFunction<ItemStack, T, T> f) {
        AccessoriesCapability capability = AccessoriesCapability.get(entity);

        if (capability != null) {
            for (SlotEntryReference slotEntryReference : capability.getAllEquipped()) {
                init = f.apply(slotEntryReference.stack(), init);
            }
        }

        return init;
    }

    @Override
    public ItemStack tryEquip(LivingEntity entity, ItemStack stack, boolean allowSwapping) {
        AccessoriesCapability capability = AccessoriesCapability.get(entity);

        if (capability != null) {
            Pair<SlotReference, EquipAction> possibleLocation = capability.canEquipAccessory(stack, false);

            if (possibleLocation != null) {
                return possibleLocation.second().equipStack(stack).orElse(ItemStack.EMPTY);
            }
        }

        return stack;
    }
}
