package artifacts.fabric.mixin.ability;

import artifacts.event.ArtifactHooks;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = {LivingEntity.class, Player.class})
public abstract class LivingEntitiesMixin extends Entity {

    @Shadow public abstract boolean isInvulnerableTo(ServerLevel serverLevel, DamageSource damageSource);

    public LivingEntitiesMixin(EntityType<?> type, Level world) {
        super(type, world);
        throw new UnsupportedOperationException();
    }

    @SuppressWarnings("ConstantConditions")
    @Inject(method = "actuallyHurt", allow = 1, at = @At(value = "JUMP", opcode = Opcodes.IFNE))
    private void onEntityDamaged(ServerLevel serverLevel, DamageSource damageSource, float amount, CallbackInfo ci) {
        if (!this.isInvulnerableTo(serverLevel, damageSource)) {
            LivingEntity self = (LivingEntity) (Object) this;
            ArtifactHooks.onLivingDamaged(self, damageSource, amount);
        }
    }
}
