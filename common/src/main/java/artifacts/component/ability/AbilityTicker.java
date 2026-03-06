package artifacts.component.ability;

import net.minecraft.world.entity.LivingEntity;

public interface AbilityTicker<ABILITY extends EquipmentAbility> {

    void wornTick(ABILITY ability, LivingEntity entity, boolean isOnCooldown, boolean isDisabled);

    default void onUnequip(ABILITY ability, LivingEntity entity) {

    }
}
