package artifacts.integration.impl.accessories;

import artifacts.event.ArtifactHooks;
import artifacts.integration.ModCompat;
import artifacts.integration.equipment.EquipmentIntegration;
import artifacts.item.WearableArtifactItem;
import artifacts.platform.PlatformServices;
import artifacts.registry.ModDataComponents;
import artifacts.util.DamageSourceHelper;
import io.wispforest.accessories.api.*;
import io.wispforest.accessories.api.events.AccessoryChangeCallback;
import io.wispforest.accessories.api.slot.SlotEntryReference;
import io.wispforest.accessories.api.slot.SlotReference;
import it.unimi.dsi.fastutil.Pair;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

import java.util.function.BiFunction;

public class AccessoriesIntegration implements EquipmentIntegration {

    @Override
    public void setup() {
        PlatformServices.platformHelper.registryEntryAddCallback(item -> {
            if (item instanceof WearableArtifactItem wearableArtifactItem) {
                AccessoriesAPI.registerAccessory(item, new WearableArtifactAccessory(wearableArtifactItem));
            }
        });

        AccessoryChangeCallback.EVENT.register(
                (prevStack, currentStack, slotReference, slotStateChange) -> ArtifactHooks.onItemChanged(slotReference.entity(), prevStack, currentStack)
        );
    }

    @Override
    public <T> T reduceEquipment(LivingEntity entity, T init, BiFunction<ItemStack, T, T> f) {
        AccessoriesCapability capability = AccessoriesCapability.get(entity);

        if (capability != null) {
            for (SlotEntryReference slotEntryReference : capability.getAllEquipped()) {
                init = f.apply(slotEntryReference.stack(), init);
            }
        }

        return init;
    }

    @Override
    public boolean equipAccessory(LivingEntity entity, ItemStack stack) {
        AccessoriesCapability capability = AccessoriesCapability.get(entity);

        if (capability != null) {
            Pair<SlotReference, EquipAction> possibleLocation = capability.canEquipAccessory(stack, false);

            if (possibleLocation != null) {
                possibleLocation.second().equipStack(stack);
                return true;
            }
        }

        return false;
    }

    @Override
    public String name() {
        return ModCompat.ACCESSORIES;
    }

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
}
