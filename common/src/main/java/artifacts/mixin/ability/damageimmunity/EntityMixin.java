package artifacts.mixin.ability.damageimmunity;

import artifacts.equipment.EquipmentHelper;
import artifacts.registry.ModDataComponents;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Entity.class)
public class EntityMixin {

    @SuppressWarnings("ConstantConditions")
    @ModifyReturnValue(method = "isInvulnerableTo", at = @At("RETURN"))
    public boolean isInvulnerableTo(boolean original, DamageSource damageSource) {
        if (!original && ((Object) this) instanceof LivingEntity entity && EquipmentHelper.hasAbilityActive(
                ModDataComponents.DAMAGE_IMMUNITY.get(), entity, true, ability -> damageSource.is(ability.tag())
        )) {
            return true;
        }
        return original;
    }
}
