package artifacts.integration.accessories;

import artifacts.equipment.EquipmentSlotManager;
import artifacts.event.ArtifactHooks;
import artifacts.util.DamageSourceHelper;
import io.wispforest.accessories.api.events.AccessoryChangeCallback;
import io.wispforest.accessories.api.events.DropRule;
import io.wispforest.accessories.api.events.OnDropCallback;
import io.wispforest.accessories.api.slot.SlotReference;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.item.ItemStack;

public class AccessoriesCompat {

    public static void setup() {
        EquipmentSlotManager.register(new AccessoriesSlotProvider());

        AccessoryChangeCallback.EVENT.register(
                (prevStack, currentStack, slotReference, _) -> ArtifactHooks.onItemChanged(slotReference.entity(), prevStack, currentStack)
        );

        OnDropCallback.EVENT.register(AccessoriesCompat::onDropItem);
    }

    private static DropRule onDropItem(DropRule dropRule, ItemStack stack, SlotReference reference, DamageSource damageSource) {
        if (dropRule == DropRule.DEFAULT && DamageSourceHelper.shouldDestroyWornItemOnDeath(reference.entity(), stack)) {
            return DropRule.DESTROY;
        }
        return dropRule;
    }
}
