package artifacts.fabric.mixin.ability;

import artifacts.event.ArtifactHooks;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = LivingEntity.class)
public abstract class LivingEntityMixin {

    @Inject(at = @At("TAIL"), method = "tick")
    private void tick(CallbackInfo info) {
        LivingEntity self = (LivingEntity) (Object) this;
        if (self.isRemoved()) {
            return;
        }
        ArtifactHooks.livingUpdate(self);
    }

    @Inject(method = "actuallyHurt", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;setHealth(F)V", shift = At.Shift.AFTER))
    private void onEntityDamaged(ServerLevel level, DamageSource source, float dmg, CallbackInfo ci) {
        LivingEntity self = (LivingEntity) (Object) this;
        if (!self.isInvulnerableTo(level, source)) {
            ArtifactHooks.onLivingDamaged(self, source, dmg);
        }
    }
}
