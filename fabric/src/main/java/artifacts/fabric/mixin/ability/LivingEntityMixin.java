package artifacts.fabric.mixin.ability;

import artifacts.event.ArtifactHooks;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = LivingEntity.class)
public abstract class LivingEntityMixin {

    @Inject(at = @At("TAIL"), method = "tick")
    private void tick(CallbackInfo info) {
        // noinspection ConstantConditions
        LivingEntity entity = (LivingEntity) (Object) this;
        if (entity.isRemoved()) {
            return;
        }
        ArtifactHooks.livingUpdate(entity);
    }
}
