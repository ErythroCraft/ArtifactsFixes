package artifacts.mixin.ability.nightvision.client;

import artifacts.registry.ModAbilities;
import artifacts.util.AbilityHelper;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(GameRenderer.class)
public class GameRendererMixin {

    @SuppressWarnings("unused")
    @ModifyReturnValue(method = "getNightVisionScale", at = @At("RETURN"))
    private static float getNightVisionScale(float original, LivingEntity entity, float f) {
        MobEffectInstance effect = entity.getEffect(MobEffects.NIGHT_VISION);
        if (effect == null || !effect.endsWithin(60)) {
            return original;
        }
        double scale = AbilityHelper.maxDouble(ModAbilities.NIGHT_VISION.get(), entity, ability -> ability.strength().get(), false);
        if (scale == 0) {
            return original;
        }
        return Mth.lerp(Math.max(0, effect.getDuration() - f - 40) / (60 - 40), (float) scale, original);
    }
}
