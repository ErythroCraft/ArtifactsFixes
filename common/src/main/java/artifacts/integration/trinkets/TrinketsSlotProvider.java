package artifacts.integration.trinkets;

import artifacts.equipment.EquipmentSlotProvider;
import eu.pb4.trinkets.api.TrinketAttachment;
import eu.pb4.trinkets.api.TrinketInventory;
import eu.pb4.trinkets.api.TrinketsApi;
import eu.pb4.trinkets.impl.TrinketSlot;
import io.wispforest.accessories.api.slot.SlotReference;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

import java.util.Map;
import java.util.function.BiFunction;

public class TrinketsSlotProvider implements EquipmentSlotProvider {

    @Override
    public <T> T reduceEquipment(LivingEntity entity, T init, BiFunction<ItemStack, T, T> f) {
        for (Map<String, TrinketInventory> map : TrinketsApi.getAttachment(entity).getInventory().values()) {
            for (TrinketInventory inventory : map.values()) {
                for (int i = 0; i < inventory.getContainerSize(); i++) {
                    ItemStack item = inventory.getItem(i);
                    if (!item.isEmpty()) {
                        init = f.apply(item, init);
                    }
                }
            }
        }
        return init;
    }

    @Override
    public ItemStack tryEquip(LivingEntity entity, ItemStack stack, boolean allowSwapping) {
        // see TrinketItem::equipItem
        // TODO
        TrinketAttachment trinkets = TrinketsApi.getAttachment(entity);
        for (Map<String, TrinketInventory> group : trinkets.getInventory().values()) {
            for (TrinketInventory inventory : group.values()) {
                for (int slotId = 0; slotId < inventory.getContainerSize(); slotId++) {
                    SlotReference slotReference = new SlotReference(inventory, slotId);
                    ItemStack existingItem = inventory.getItem(slotId);
                    boolean canUnequip = TrinketsApi.getTrinket(existingItem.getItem()).canUnequip(existingItem, slotReference, entity);
                    if (TrinketSlot.canInsert(stack, slotReference, entity)
                            && canUnequip
                            && (allowSwapping || inventory.getItem(slotId).isEmpty())
                    ) {
                        inventory.setItem(slotId, stack);
                        return existingItem;
                    }
                }
            }
        }
        return stack;
    }
}
