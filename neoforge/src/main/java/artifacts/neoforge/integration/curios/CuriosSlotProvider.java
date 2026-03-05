package artifacts.neoforge.integration.curios;

import artifacts.equipment.EquipmentSlotProvider;
import net.minecraft.core.NonNullList;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.type.capability.ICuriosItemHandler;
import top.theillusivec4.curios.api.type.inventory.ICurioStacksHandler;
import top.theillusivec4.curios.api.type.inventory.IDynamicStackHandler;

import java.util.Map;
import java.util.Optional;
import java.util.function.BiFunction;

public class CuriosSlotProvider implements EquipmentSlotProvider {

    @Override
    public <T> T reduceEquipment(LivingEntity entity, T init, BiFunction<ItemStack, T, T> f) {
        Optional<ICuriosItemHandler> itemHandler = CuriosApi.getCuriosInventory(entity);
        if (itemHandler.isPresent()) {
            for (ICurioStacksHandler stacksHandler : itemHandler.get().getCurios().values()) {
                for (int i = 0; i < stacksHandler.getStacks().getSlots(); i++) {
                    ItemStack item = stacksHandler.getStacks().getStackInSlot(i);
                    if (!item.isEmpty()) {
                        init = f.apply(item, init);
                    }
                }
            }
        }
        return init;
    }

    @Override
    @SuppressWarnings("removal")
    public ItemStack tryEquip(LivingEntity entity, ItemStack stack, boolean allowSwapping) {
        Optional<ICuriosItemHandler> optional = CuriosApi.getCuriosInventory(entity);
        if (optional.isEmpty()) {
            return stack;
        }

        Map<String, ICurioStacksHandler> curios = optional.get().getCurios();

        for (Map.Entry<String, ICurioStacksHandler> entry : curios.entrySet()) {
            IDynamicStackHandler stackHandler = entry.getValue().getStacks();
            NonNullList<Boolean> activeStates = entry.getValue().getActiveStates();

            for (int slot = 0; slot < stackHandler.getSlots(); slot++) {
                boolean active = activeStates.size() > slot && activeStates.get(slot);

                if (!active) {
                    continue;
                }

                if (stackHandler.isItemValid(slot, stack)) {
                    ItemStack existingStack = stackHandler.getStackInSlot(slot);

                    if (existingStack.isEmpty()) {
                        stackHandler.setStackInSlot(slot, stack.copy());
                        return ItemStack.EMPTY;
                    } else if (allowSwapping) {
                        if (stackHandler.extractItem(slot, existingStack.getMaxStackSize(), true).getCount() == existingStack.getCount()) {
                            ItemStack present = stackHandler.getStackInSlot(slot);
                            stackHandler.setStackInSlot(slot, stack.copy());
                            return present.copy();
                        }
                    }
                }
            }
        }
        return stack;
    }
}
