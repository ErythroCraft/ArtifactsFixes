package artifacts.integration.impl.trinkets;

import artifacts.event.ArtifactHooks;
import artifacts.integration.ModCompat;
import artifacts.integration.equipment.EquipmentIntegration;
import artifacts.item.WearableArtifactItem;
import artifacts.platform.PlatformServices;
import artifacts.util.DamageSourceHelper;
import dev.emi.trinkets.api.*;
import dev.emi.trinkets.api.event.TrinketEquipCallback;
import dev.emi.trinkets.api.event.TrinketUnequipCallback;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

import java.util.Map;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Consumer;

public class TrinketsIntegration implements EquipmentIntegration {

    @Override
    public void setup() {
        PlatformServices.platformHelper.registryEntryAddCallback(item -> {
            if (item instanceof WearableArtifactItem wearableArtifactItem) {
                TrinketsApi.registerTrinket(item, new WearableArtifactTrinket(wearableArtifactItem));
            }
        });

        TrinketEquipCallback.EVENT.register((stack, slot, entity) -> ArtifactHooks.onItemChanged(entity, ItemStack.EMPTY, stack));
        TrinketUnequipCallback.EVENT.register((stack, slot, entity) -> ArtifactHooks.onItemChanged(entity, stack, ItemStack.EMPTY));
    }

    @Override
    public void iterateEquippedAccessories(LivingEntity entity, Consumer<ItemStack> consumer) {
        TrinketsApi.getTrinketComponent(entity).ifPresent(component -> {
            for (Map<String, TrinketInventory> map : component.getInventory().values()) {
                for (TrinketInventory inventory : map.values()) {
                    for (int i = 0; i < inventory.getContainerSize(); i++) {
                        ItemStack item = inventory.getItem(i);
                        if (!item.isEmpty()) {
                            consumer.accept(item);
                        }
                    }
                }
            }
        });
    }

    @Override
    public <T> T reduceAccessories(LivingEntity entity, T init, BiFunction<ItemStack, T, T> f) {
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
    public boolean equipAccessory(LivingEntity entity, ItemStack stack) {
        return TrinketItem.equipItem(entity, stack);
    }

    @Override
    public String name() {
        return ModCompat.TRINKETS;
    }

    public record WearableArtifactTrinket(WearableArtifactItem item) implements Trinket {

        @Override
        public TrinketEnums.DropRule getDropRule(ItemStack stack, SlotReference slot, LivingEntity entity) {
            if (DamageSourceHelper.shouldDestroyWornItemsOnDeath(entity)) {
                return TrinketEnums.DropRule.DESTROY;
            }
            return Trinket.super.getDropRule(stack, slot, entity);
        }
    }
}
