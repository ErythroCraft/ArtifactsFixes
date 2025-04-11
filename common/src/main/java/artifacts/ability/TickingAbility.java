package artifacts.ability;

import net.minecraft.world.entity.LivingEntity;

public interface TickingAbility extends EquipmentAbility {

    default void wornTick(LivingEntity entity, boolean isOnCooldown, boolean isDisabled) {

    }

    default void onUnequip(LivingEntity entity) {

    }
}
