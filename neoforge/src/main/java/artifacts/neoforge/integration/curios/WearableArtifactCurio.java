package artifacts.neoforge.integration.curios;

import artifacts.item.WearableArtifactItem;
import artifacts.registry.ModDataComponents;
import artifacts.util.DamageSourceHelper;
import net.minecraft.core.component.DataComponents;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.common.DropRule;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

public record WearableArtifactCurio(WearableArtifactItem item) implements ICurioItem {

    @Override
    public DropRule getDropRule(SlotContext slotContext, DamageSource source, boolean recentlyHit, ItemStack stack) {
        if (DamageSourceHelper.shouldDestroyWornItemsOnDeath(slotContext.entity())) {
            return DropRule.DESTROY;
        }
        return ICurioItem.super.getDropRule(slotContext, source, recentlyHit, stack);
    }

    @Override
    public boolean canEquipFromUse(SlotContext slotContext, ItemStack stack) {
        return stack.get(DataComponents.FOOD) == null;
    }

    @Override
    public void onEquipFromUse(SlotContext slotContext, ItemStack stack) {
        SoundEvent equipSound = stack.get(ModDataComponents.EQUIP_SOUND);
        if (equipSound != null) {
            LivingEntity entity = slotContext.entity();
            Vec3 pos = entity.position();
            slotContext.entity().level().playSound(null, pos.x(), pos.y(), pos.z(), equipSound, slotContext.entity().getSoundSource());
        }
    }
}
