package artifacts.component.ability;

import net.minecraft.world.entity.LivingEntity;

public interface TickingCompositeAbility<ENTRY extends TickingAbility> extends CompositeAbility<ENTRY>, TickingAbility {

    @Override
    default void onUnequip(LivingEntity entity) {
        for (ENTRY entry : entries()) {
            entry.onUnequip(entity);
        }
    }

    @Override
    default void wornTick(LivingEntity entity, boolean isOnCooldown, boolean isDisabled) {
        for (ENTRY entry : entries()) {
            entry.wornTick(entity, isOnCooldown, isDisabled);
        }
    }
}
