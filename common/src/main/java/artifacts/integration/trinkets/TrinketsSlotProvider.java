package artifacts.integration.trinkets;

import artifacts.equipment.EquipmentSlotAccess;
import artifacts.equipment.EquipmentSlotProvider;
import eu.pb4.trinkets.api.TrinketAttachment;
import eu.pb4.trinkets.api.TrinketInventory;
import eu.pb4.trinkets.api.TrinketSlotAccess;
import eu.pb4.trinkets.api.TrinketsApi;
import eu.pb4.trinkets.api.callback.TrinketCallback;
import eu.pb4.trinkets.impl.TrinketSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

import java.util.Map;
import java.util.function.BiFunction;

public class TrinketsSlotProvider implements EquipmentSlotProvider {

    @Override
    public <T> T reduceEquipment(LivingEntity entity, T init, BiFunction<EquipmentSlotAccess, T, T> f) {
        for (Map<String, TrinketInventory> map : TrinketsApi.getAttachment(entity).getInventory().values()) {
            for (TrinketInventory inventory : map.values()) {
                for (int slot = 0; slot < inventory.getContainerSize(); slot++) {
                    ItemStack item = inventory.getItem(slot);
                    if (!item.isEmpty()) {
                        EquipmentSlotAccess slotAccess = new SlotAccess(inventory.getOrCreateSlotAccess(slot));
                        init = f.apply(slotAccess, init);
                    }
                }
            }
        }
        return init;
    }

    @Override
    public ItemStack tryEquip(LivingEntity entity, ItemStack stack, boolean allowSwapping) {
        // see TrinketItem::equipItem
        TrinketAttachment trinkets = TrinketsApi.getAttachment(entity);
        for (Map<String, TrinketInventory> group : trinkets.getInventory().values()) {
            for (TrinketInventory inventory : group.values()) {
                for (int slot = 0; slot < inventory.getContainerSize(); slot++) {
                    TrinketSlotAccess slotAccess = inventory.getOrCreateSlotAccess(slot);
                    ItemStack existingItem = inventory.getItem(slot);
                    boolean canUnequip = TrinketCallback.getCallback(existingItem).canUnequip(existingItem, slotAccess, entity);
                    if (TrinketSlot.canInsert(stack, slotAccess, entity)
                            && canUnequip
                            && (allowSwapping || inventory.getItem(slot).isEmpty())
                    ) {
                        inventory.setItem(slot, stack);
                        return existingItem;
                    }
                }
            }
        }
        return stack;
    }

    private record SlotAccess(TrinketSlotAccess slotAccess) implements EquipmentSlotAccess {

        @Override
        public ItemStack get() {
            return slotAccess.get();
        }

        @Override
        public void broadcastBreakEvent(LivingEntity entity) {
            TrinketsApi.onTrinketBroken(
                    slotAccess.get(),
                    slotAccess,
                    entity
            );
        }
    }
}
