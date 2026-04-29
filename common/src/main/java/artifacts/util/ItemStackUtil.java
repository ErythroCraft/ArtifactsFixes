package artifacts.util;

import artifacts.equipment.EquipmentSlotAccess;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;

public class ItemStackUtil {

    public static void hurtAndBreak(EquipmentSlotAccess slotAccess, int damage, LivingEntity entity) {
        if (entity.level() instanceof ServerLevel level) {
            ServerPlayer player = null;
            if (entity instanceof ServerPlayer) {
                player = (ServerPlayer) entity;
            }
            slotAccess.get().hurtAndBreak(damage, level, player, _ -> slotAccess.broadcastBreakEvent(entity));
        }
    }
}
