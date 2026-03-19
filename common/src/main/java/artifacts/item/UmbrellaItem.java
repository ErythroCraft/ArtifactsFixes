package artifacts.item;

import artifacts.component.ability.SimpleAbility;
import artifacts.equipment.EquipmentHelper;
import artifacts.registry.ModDataComponents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

public final class UmbrellaItem {

    public static void onLivingUpdate(LivingEntity entity) {
        if (UmbrellaItem.shouldGlide(entity)) {
            entity.fallDistance = 0;
        }
    }

    public static boolean shouldGlide(LivingEntity entity) {
        return !entity.onGround()
                && entity.getDeltaMovement().y < 0
                && !entity.hasEffect(MobEffects.SLOW_FALLING)
                && !(entity.isInWater() && !EquipmentHelper.hasAbilityActive(ModDataComponents.SINKING.get(), entity))
                && UmbrellaItem.isHoldingUmbrellaUpright(entity, true);
    }

    private static boolean isHoldingUmbrellaUpright(LivingEntity entity, InteractionHand hand, boolean ignoreCosmetic) {
        SimpleAbility gliderAbility = entity.getItemInHand(hand).get(ModDataComponents.HANDHELD_GLIDER.get());
        return gliderAbility != null
                && (ignoreCosmetic || gliderAbility.isNonCosmetic())
                && (!entity.isUsingItem() || entity.getUsedItemHand() != hand)
                && (!entity.swinging || entity.swingingArm != hand);
    }

    public static boolean isHoldingUmbrellaUpright(Entity entity, boolean ignoreCosmetic) {
        return entity instanceof LivingEntity livingEntity
            && (isHoldingUmbrellaUpright(livingEntity, InteractionHand.MAIN_HAND, ignoreCosmetic)
                || isHoldingUmbrellaUpright(livingEntity, InteractionHand.OFF_HAND, ignoreCosmetic));
    }
}
