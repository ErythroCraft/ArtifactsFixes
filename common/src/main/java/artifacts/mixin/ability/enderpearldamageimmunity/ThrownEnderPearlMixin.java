package artifacts.mixin.ability.enderpearldamageimmunity;

import artifacts.equipment.EquipmentHelper;
import artifacts.registry.ModDataComponents;
import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.throwableitemprojectile.ThrownEnderpearl;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ThrownEnderpearl.class)
public class ThrownEnderPearlMixin {

    @WrapWithCondition(method = "onHit", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;hurt(Lnet/minecraft/world/damagesource/DamageSource;F)Z"))
    private boolean shouldNullifyDamage(Entity entity, DamageSource damageSource, float amount) {
        return !(entity instanceof LivingEntity livingEntity && EquipmentHelper.hasAbilityActive(ModDataComponents.ENDER_PEARL_DAMAGE_IMMUNITY.get(), livingEntity, true));
    }
}
