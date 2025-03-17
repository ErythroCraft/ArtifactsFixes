package artifacts.neoforge.integration.curios;

import artifacts.item.WearableArtifactItem;
import artifacts.util.DamageSourceHelper;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.item.ItemStack;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurio;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

public record WearableArtifactCurio(WearableArtifactItem item) implements ICurioItem {

    @Override
    public ICurio.DropRule getDropRule(SlotContext slotContext, DamageSource source, int lootingLevel, boolean recentlyHit, ItemStack stack) {
        if (DamageSourceHelper.shouldDestroyWornItemsOnDeath(slotContext.entity())) {
            return ICurio.DropRule.DESTROY;
        }
        return ICurioItem.super.getDropRule(slotContext, source, lootingLevel, recentlyHit, stack);
    }

    @Override
    public ICurio.SoundInfo getEquipSound(SlotContext slotContext, ItemStack stack) {
        return new ICurio.SoundInfo(item.getEquipSound(), 1, item.getEquipSoundPitch());
    }

    @Override
    public boolean canEquipFromUse(SlotContext slotContext, ItemStack stack) {
        return item.getFoodProperties(stack, slotContext.entity()) == null;
    }
}
