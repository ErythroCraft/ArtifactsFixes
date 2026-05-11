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

import java.util.function.Supplier;

@Mixin(GameRenderer.class)
public class GameRendererMixin {

    @ModifyReturnValue(method = "getNightVisionScale", at = @At("RETURN"))
    private static float getNightVisionScale(float original, LivingEntity camera, float a) {
        MobEffectInstance effect = camera.getEffect(MobEffects.NIGHT_VISION);
        if (effect == null || !effect.endsWithin(12 * 20)) {
            return original;
        }
        // TODO: don't skip disabled items to prevent weird flicker when toggling off
        double scale = EquipmentHelper.maxDouble(ModDataComponents.REDUCED_NIGHT_VISION, camera, Supplier::get, false);
        if (scale == 0) {
            return original;
        }
        return Mth.lerp(Math.max(0, effect.getDuration() - a - 11 * 20) / (12 * 20 - 11 * 20), (float) scale, original);
    }
}
