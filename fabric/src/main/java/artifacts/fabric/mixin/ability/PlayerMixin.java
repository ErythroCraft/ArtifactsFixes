package artifacts.fabric.mixin.ability;

import artifacts.event.ArtifactHooks;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Player.class)
public class PlayerMixin {

    @Inject(method = "actuallyHurt", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;setHealth(F)V", shift = At.Shift.AFTER))
    private void onEntityDamaged(ServerLevel level, DamageSource source, @Local(name = "dmg", argsOnly = true) float dmg, CallbackInfo ci) {
        LivingEntity self = (LivingEntity) (Object) this;
        if (!self.isInvulnerableTo(level, source)) {
            ArtifactHooks.onLivingDamaged(self, source, dmg);
        }
    }
}
