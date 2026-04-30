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
        if (!isIndestructible(stack)) {
            return original;
        }
        if (stack.nextDamageWillBreak()) {
            return 0;
        }
        int durabilityRemaining = stack.getDamageValue() - stack.getMaxDamage();
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
