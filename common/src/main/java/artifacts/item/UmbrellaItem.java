package artifacts.item;

import artifacts.Artifacts;
import artifacts.equipment.EquipmentHelper;
import artifacts.registry.ModDataComponents;
import artifacts.registry.ModTags;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

import java.util.List;

// TODO move tooltips/logic to component
public class UmbrellaItem extends ArtifactItem {

    public UmbrellaItem(Properties properties) {
        super(properties);
    }

    @Override
    public boolean isCosmetic() {
        return !Artifacts.CONFIG.items.umbrellaIsGlider.get() && !Artifacts.CONFIG.items.umbrellaIsShield.get();
    }

    @Override
    protected void addEffectsTooltip(List<MutableComponent> tooltip) {
        if (Artifacts.CONFIG.items.umbrellaIsGlider.get()) {
            tooltip.add(tooltipLine("glider"));
        }
        if (Artifacts.CONFIG.items.umbrellaIsShield.get()) {
            tooltip.add(tooltipLine("shield"));
        }
    }

    public static void onLivingUpdate(LivingEntity entity) {
        if (UmbrellaItem.shouldGlide(entity)) {
            entity.fallDistance = 0;
        }
    }

    public static boolean shouldGlide(LivingEntity entity) {
        return !entity.onGround()
                && entity.getDeltaMovement().y < 0
                && !entity.hasEffect(MobEffects.SLOW_FALLING)
                && Artifacts.CONFIG.items.umbrellaIsGlider.get()
                && !(entity.isInWater() && !EquipmentHelper.hasAbilityActive(ModDataComponents.SINKING.get(), entity, true))
                && UmbrellaItem.isHoldingUmbrellaUpright(entity);
    }

    public static boolean isHoldingUmbrellaUpright(LivingEntity entity, InteractionHand hand) {
        return entity.getItemInHand(hand).is(ModTags.UMBRELLAS)
                && (!entity.isUsingItem() || entity.getUsedItemHand() != hand)
                && (!entity.swinging || entity.swingingArm != hand);
    }

    public static boolean isHoldingUmbrellaUpright(LivingEntity entity) {
        return isHoldingUmbrellaUpright(entity, InteractionHand.MAIN_HAND) || isHoldingUmbrellaUpright(entity, InteractionHand.OFF_HAND);
    }

    public static boolean isHoldingUmbrellaUpright(Entity entity) {
        return entity instanceof LivingEntity livingEntity
            && isHoldingUmbrellaUpright(livingEntity);
    }

}
