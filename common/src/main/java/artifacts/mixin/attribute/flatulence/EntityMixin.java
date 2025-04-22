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
    private void setShiftKeyDown(boolean isDown, CallbackInfo ci) {
        // noinspection ConstantValue
        if (isDown && !isShiftKeyDown() && ((Entity) (Object) this) instanceof LivingEntity entity && !entity.level().isClientSide()) {
            double chance = entity.getAttributeValue(ModAttributes.FLATULENCE);
            if (entity.getRandom().nextFloat() < chance) {
                entity.gameEvent(ModGameEvents.FART);
                entity.level().playSound(null, entity, ModSoundEvents.FART.value(), SoundSource.PLAYERS, 1, 0.9F + entity.getRandom().nextFloat() * 0.2F);
            }
        }
    }
}
