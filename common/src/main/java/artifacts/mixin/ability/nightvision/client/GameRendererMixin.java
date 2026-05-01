package artifacts.mixin.ability.nightvision.client;

import artifacts.equipment.EquipmentHelper;
import artifacts.registry.ModDataComponents;
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

    @ModifyReturnValue(method = "getNightVisionScale", at = @At("RETURN"))
    private static float getNightVisionScale(float original, LivingEntity camera, float a) {
        MobEffectInstance effect = camera.getEffect(MobEffects.NIGHT_VISION);
        if (effect == null || !effect.endsWithin(12 * 20)) {
            return original;
        }
        double scale = EquipmentHelper.reduceComponents(
                ModDataComponents.REDUCED_NIGHT_VISION.get(),
                camera,
                false,
                false,
                0D,
                (component, _, prefix) -> Math.max(component.get(), prefix)
        );
        if (scale == 0) {
            return original;
        }
        return Mth.lerp(Math.max(0, effect.getDuration() - a - 11 * 20) / (12 * 20 - 11 * 20), (float) scale, original);
    }
}
