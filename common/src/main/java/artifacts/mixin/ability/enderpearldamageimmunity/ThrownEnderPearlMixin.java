package artifacts.mixin.ability.enderpearldamageimmunity;

import artifacts.equipment.EquipmentHelper;
import artifacts.registry.ModDataComponents;
import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.throwableitemprojectile.ThrownEnderpearl;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ThrownEnderpearl.class)
public class ThrownEnderPearlMixin {

    @WrapWithCondition(method = "onHit", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerPlayer;hurtServer(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/damagesource/DamageSource;F)Z"))
    private boolean shouldNullifyDamage(ServerPlayer player, ServerLevel level, DamageSource damageSource, float amount) {
        return !(player instanceof LivingEntity livingEntity && EquipmentHelper.hasAbilityActive(ModDataComponents.ENDER_PEARL_DAMAGE_IMMUNITY.get(), livingEntity, true));
    }
}
