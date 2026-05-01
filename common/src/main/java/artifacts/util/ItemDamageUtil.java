package artifacts.util;

import artifacts.config.value.Value;
import artifacts.equipment.EquipmentSlotAccess;
import artifacts.registry.ModDataComponents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public class ItemDamageUtil {

    public static boolean isIndestructible(ItemStack stack) {
        Value<Boolean> indestructible = stack.get(ModDataComponents.INDESTRUCTIBLE.get());
        return indestructible != null && indestructible.get();
    }

    public static boolean needsRepair(ItemStack stack) {
        return isIndestructible(stack) && stack.nextDamageWillBreak();
    }

    // called from ItemStack#processdurabilityChange
    public static int processDurabilityChange(ItemStack stack, int original) {
        // do nothing if the item doesn't have the indestructible component,
        // or if the item is being repaired
        if (!isIndestructible(stack) || original <= 0) {
            return original;
        }
        // don't damage if durability is already at 1 or below
        if (stack.nextDamageWillBreak()) {
            return 0;
        }
        // leave indestructible items at 1 durability (= getMaxDamage - 1)
        int durabilityRemaining = Math.max(0, stack.getMaxDamage() - 1 - stack.getDamageValue());
        return Math.min(original, durabilityRemaining);
    }

    public static void hurtAndBreak(EquipmentSlotAccess slotAccess, int damage, LivingEntity entity) {
        if (damage > 0 && entity.level() instanceof ServerLevel level) {
            ServerPlayer player = null;
            if (entity instanceof ServerPlayer) {
                player = (ServerPlayer) entity;
            }
            slotAccess.get().hurtAndBreak(damage, level, player, _ -> slotAccess.broadcastBreakEvent(entity));
        }
    }
}
