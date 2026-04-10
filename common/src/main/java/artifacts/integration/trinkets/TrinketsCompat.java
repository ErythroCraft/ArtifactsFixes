package artifacts.integration.trinkets;

import artifacts.equipment.EquipmentSlotManager;
import artifacts.event.ArtifactHooks;
import artifacts.integration.ModCompat;
import artifacts.util.DamageSourceHelper;
import eu.pb4.trinkets.api.TrinketDropRule;
import eu.pb4.trinkets.api.event.TrinketDropCallback;
import eu.pb4.trinkets.api.event.TrinketEquipCallback;
import eu.pb4.trinkets.api.event.TrinketUnequipCallback;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.item.ItemStack;

public class TrinketsCompat {

    public static void setup() {
        if (!ModCompat.TCLAYER.isLoaded()) {
            EquipmentSlotManager.register(new TrinketsSlotProvider());
        }

        TrinketEquipCallback.EVENT.register((stack, _, entity) -> ArtifactHooks.onItemChanged(entity, ItemStack.EMPTY, stack));
        TrinketUnequipCallback.EVENT.register((stack, _, entity) -> ArtifactHooks.onItemChanged(entity, stack, ItemStack.EMPTY));
        TrinketDropCallback.EVENT.register(TrinketsCompat::onDropItem);
    }

    public static TrinketDropRule onDropItem(TrinketDropRule dropRule, ItemStack stack, SlotAccess slotAccess, LivingEntity entity) {
        if (dropRule == TrinketDropRule.DEFAULT && DamageSourceHelper.shouldDestroyWornItemOnDeath(entity, stack)) {
            return TrinketDropRule.DESTROY;
        }
        return dropRule;
    }
}
