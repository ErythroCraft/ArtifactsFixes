package artifacts.integration.trinkets;

import artifacts.equipment.EquipmentSlotProvider;
import dev.emi.trinkets.api.TrinketComponent;
import dev.emi.trinkets.api.TrinketInventory;
import dev.emi.trinkets.api.TrinketItem;
import dev.emi.trinkets.api.TrinketsApi;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

import java.util.Map;
import java.util.Optional;
import java.util.function.BiFunction;

public class TrinketsSlotProvider implements EquipmentSlotProvider {

    @Override
    public <T> T reduceEquipment(LivingEntity entity, T init, BiFunction<ItemStack, T, T> f) {
        Optional<TrinketComponent> component = TrinketsApi.getTrinketComponent(entity);
        if (component.isPresent()) {
            for (Map<String, TrinketInventory> map : component.get().getInventory().values()) {
                for (TrinketInventory inventory : map.values()) {
                    for (int i = 0; i < inventory.getContainerSize(); i++) {
                        ItemStack item = inventory.getItem(i);
                        if (!item.isEmpty()) {
                            init = f.apply(item, init);
                        }
                    }
                }
            }
        }
        return init;
    }

    @Override
    public boolean tryEquipItem(LivingEntity entity, ItemStack stack) {
        return TrinketItem.equipItem(entity, stack);
    }
}
