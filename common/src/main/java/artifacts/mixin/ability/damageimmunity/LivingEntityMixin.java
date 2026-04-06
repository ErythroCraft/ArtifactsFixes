package artifacts.mixin.ability.damageimmunity;

import artifacts.equipment.EquipmentHelper;
import artifacts.registry.ModDataComponents;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(LivingEntity.class)
public class LivingEntityMixin {

    @ModifyReturnValue(method = "isInvulnerableTo", at = @At("RETURN"))
    public boolean isInvulnerableTo(boolean original, ServerLevel level, DamageSource damageSource) {
        LivingEntity entity = (LivingEntity) (Object) this;
        if (!original && EquipmentHelper.hasAbilityActive(
                ModDataComponents.DAMAGE_IMMUNITY.get(), entity, true,
                ability -> ability.condition().test(entity) && damageSource.is(ability.tag())
        )) {
            return true;
        }
        return original;
    }
}
