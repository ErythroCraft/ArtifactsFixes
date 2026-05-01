package artifacts.equipment;

import artifacts.util.ItemDamageUtil;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public interface EquipmentSlotAccess extends SlotAccess {

    ItemStack get();

    @Override
    default boolean set(ItemStack stack) {
        return false;
    }

    void broadcastBreakEvent(LivingEntity entity);

    default boolean isDisabledOrBroken() {
        return ItemDamageUtil.isDisabledOrBroken(get());
    }

    default boolean isOnCooldown(LivingEntity entity) {
        if (entity instanceof Player player) {
            return player.getCooldowns().isOnCooldown(get());
        }
        return false;
    }

    default void addCooldown(LivingEntity entity, int cooldown) {
        if (cooldown > 0 && entity instanceof Player player && !get().isEmpty()) {
            player.getCooldowns().addCooldown(get(), cooldown);
        }
    }

    default void hurtAndBreak(LivingEntity entity, int damage) {
        if (damage > 0 && entity.level() instanceof ServerLevel level) {
            ServerPlayer player = null;
            if (entity instanceof ServerPlayer) {
                player = (ServerPlayer) entity;
            }
            get().hurtAndBreak(damage, level, player, _ -> broadcastBreakEvent(entity));
        }
    }
}
