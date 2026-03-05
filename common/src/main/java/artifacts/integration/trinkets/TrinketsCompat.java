package artifacts.integration.trinkets;

import artifacts.equipment.EquipmentSlotManager;
import artifacts.event.ArtifactHooks;
import artifacts.integration.ModCompat;
import artifacts.util.DamageSourceHelper;
import dev.emi.trinkets.api.SlotReference;
import dev.emi.trinkets.api.TrinketEnums;
import dev.emi.trinkets.api.event.TrinketDropCallback;
import dev.emi.trinkets.api.event.TrinketEquipCallback;
import dev.emi.trinkets.api.event.TrinketUnequipCallback;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public class TrinketsCompat {

    public static void setup() {
        if (!ModCompat.TCLAYER.isLoaded()) {
            EquipmentSlotManager.register(new TrinketsSlotProvider());
        }

        TrinketEquipCallback.EVENT.register((stack, slot, entity) -> ArtifactHooks.onItemChanged(entity, ItemStack.EMPTY, stack));
        TrinketUnequipCallback.EVENT.register((stack, slot, entity) -> ArtifactHooks.onItemChanged(entity, stack, ItemStack.EMPTY));
        TrinketDropCallback.EVENT.register(TrinketsCompat::onDropItem);
    }

    public static TrinketEnums.DropRule onDropItem(TrinketEnums.DropRule dropRule, ItemStack stack, SlotReference slotReference, LivingEntity entity) {
        if (dropRule == TrinketEnums.DropRule.DEFAULT && DamageSourceHelper.shouldDestroyWornItemOnDeath(entity, stack)) {
            return TrinketEnums.DropRule.DESTROY;
        }
        return dropRule;
    }
}
