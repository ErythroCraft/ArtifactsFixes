package artifacts.mixin.ability.makepiglinsneutral;

import artifacts.registry.ModDataComponents;
import artifacts.util.AbilityHelper;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.piglin.PiglinAi;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(PiglinAi.class)
public abstract class PiglinAiMixin {

    @ModifyReturnValue(method = "isWearingGold", at = @At("RETURN"))
    private static boolean isWearingGold(boolean original, LivingEntity entity) {
        return original || AbilityHelper.hasComponent(ModDataComponents.MAKE_PIGLINS_NEUTRAL.get(), entity);
    }
}
