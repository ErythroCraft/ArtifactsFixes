package artifacts.integration.trinkets;

import artifacts.item.WearableArtifactItem;
import artifacts.registry.ModDataComponents;
import artifacts.util.DamageSourceHelper;
import dev.emi.trinkets.api.SlotReference;
import dev.emi.trinkets.api.Trinket;
import dev.emi.trinkets.api.TrinketEnums;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public record WearableArtifactTrinket(WearableArtifactItem item) implements Trinket {

    @Override
    public TrinketEnums.DropRule getDropRule(ItemStack stack, SlotReference slot, LivingEntity entity) {
        if (DamageSourceHelper.shouldDestroyWornItemsOnDeath(entity)) {
            return TrinketEnums.DropRule.DESTROY;
        }
        return Trinket.super.getDropRule(stack, slot, entity);
    }

    @Override
    public boolean canEquipFromUse(ItemStack stack, LivingEntity entity) {
        return !stack.has(DataComponents.FOOD);
    }

    @Override
    public Holder<SoundEvent> getEquipSound(ItemStack stack, SlotReference slot, LivingEntity entity) {
        SoundEvent equipSound = stack.get(ModDataComponents.EQUIP_SOUND.get());
        if (equipSound != null) {
            return Holder.direct(equipSound);
        }
        return Trinket.super.getEquipSound(stack, slot, entity);
    }
}
