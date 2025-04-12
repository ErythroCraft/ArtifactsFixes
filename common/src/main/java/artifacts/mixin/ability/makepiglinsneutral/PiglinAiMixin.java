package artifacts.mixin.ability.makepiglinsneutral;

import artifacts.equipment.EquipmentHelper;
import artifacts.registry.ModDataComponents;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.piglin.PiglinAi;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(PiglinAi.class)
public abstract class PiglinAiMixin {

    @ModifyReturnValue(method = "isWearingGold", at = @At("RETURN"))
    private static boolean isWearingGold(boolean original, LivingEntity entity) {
        return original || EquipmentHelper.hasComponent(ModDataComponents.MAKE_PIGLINS_NEUTRAL.get(), entity);
    }
}
