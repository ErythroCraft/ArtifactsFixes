package artifacts.mixin.attribute.flatulence;

import artifacts.registry.ModAttributes;
import artifacts.registry.ModGameEvents;
import artifacts.registry.ModSoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Entity.class)
public abstract class EntityMixin {

    @Shadow
    public abstract boolean isShiftKeyDown();

    @Inject(method = "setShiftKeyDown", at = @At("HEAD"))
    private void setShiftKeyDown(boolean shiftKeyDown, CallbackInfo ci) {
        Entity self = (Entity) (Object) this;
        if (shiftKeyDown && !isShiftKeyDown() && self instanceof LivingEntity livingEntity && !self.level().isClientSide()) {
            double chance = livingEntity.getAttributeValue(ModAttributes.FLATULENCE);
            if (self.getRandom().nextFloat() < chance) {
                self.gameEvent(ModGameEvents.FART);
                self.level().playSound(null, livingEntity, ModSoundEvents.FART.value(), SoundSource.PLAYERS, 1, 0.9F + self.getRandom().nextFloat() * 0.2F);
            }
        }
    }
}
