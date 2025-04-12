package artifacts.integration.accessories;

import artifacts.item.WearableArtifactItem;
import artifacts.registry.ModDataComponents;
import artifacts.util.DamageSourceHelper;
import io.wispforest.accessories.api.Accessory;
import io.wispforest.accessories.api.DropRule;
import io.wispforest.accessories.api.SoundEventData;
import io.wispforest.accessories.api.slot.SlotReference;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.item.ItemStack;

public record WearableArtifactAccessory(WearableArtifactItem item) implements Accessory {

    @Override
    public DropRule getDropRule(ItemStack stack, SlotReference reference, DamageSource source) {
        if (DamageSourceHelper.shouldDestroyWornItemsOnDeath(reference.entity())) {
            return DropRule.DESTROY;
        }
        return Accessory.super.getDropRule(stack, reference, source);
    }

    @Override
    public SoundEventData getEquipSound(ItemStack stack, SlotReference reference) {
        SoundEvent soundEvent = stack.get(ModDataComponents.EQUIP_SOUND.get());
        if (soundEvent != null) {
            return new SoundEventData(Holder.direct(soundEvent), 1, 1);
        }
        return Accessory.super.getEquipSound(stack, reference);
    }

    @Override
    public boolean canEquipFromUse(ItemStack stack) {
        return stack.get(DataComponents.FOOD) == null;
    }
}
