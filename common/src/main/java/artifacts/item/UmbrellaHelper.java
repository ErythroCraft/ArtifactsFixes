package artifacts.item;

import artifacts.component.ability.SimpleAbility;
import artifacts.equipment.EquipmentHelper;
import artifacts.registry.ModDataComponents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

public final class UmbrellaHelper {

    public static void onLivingUpdate(LivingEntity entity) {
        if (UmbrellaHelper.shouldGlide(entity)) {
            entity.fallDistance = 0;
        }
    }

    public static boolean shouldGlide(LivingEntity entity) {
        return !entity.onGround()
                && entity.getDeltaMovement().y < 0
                && !entity.hasEffect(MobEffects.SLOW_FALLING)
                && !(entity.isInWater() && !EquipmentHelper.hasAbilityActive(ModDataComponents.SINKING, entity))
                && UmbrellaHelper.isHoldingUmbrellaUpright(entity, true);
    }

    private static boolean isHoldingUmbrellaUpright(LivingEntity entity, InteractionHand hand, boolean allowCosmetic) {
        SimpleAbility gliderAbility = entity.getItemInHand(hand).get(ModDataComponents.HANDHELD_GLIDER.get());
        return gliderAbility != null
                && (allowCosmetic || gliderAbility.isNonCosmetic())
                && (!entity.isUsingItem() || entity.getUsedItemHand() != hand)
                && (!entity.swinging || entity.swingingArm != hand);
    }

    public static boolean isHoldingUmbrellaUpright(Entity entity, boolean allowCosmetic) {
        return entity instanceof LivingEntity livingEntity
            && (isHoldingUmbrellaUpright(livingEntity, InteractionHand.MAIN_HAND, allowCosmetic)
                || isHoldingUmbrellaUpright(livingEntity, InteractionHand.OFF_HAND, allowCosmetic));
    }
}
