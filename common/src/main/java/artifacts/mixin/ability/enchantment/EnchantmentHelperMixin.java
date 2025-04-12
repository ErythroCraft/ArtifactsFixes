package artifacts.mixin.ability.enchantment;

import artifacts.platform.PlatformServices;
import artifacts.util.AbilityHelper;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(EnchantmentHelper.class)
public class EnchantmentHelperMixin {

    @ModifyReturnValue(method = "getFishingTimeReduction", at = @At("RETURN"))
    private static float increaseFishingTimeReduction(float original, ServerLevel level, ItemStack stack, Entity entity) {
        // Lure >5 breaks fishing, don't return more than 25 unless original was more than 25
        if (original > 25) {
            return original;
        }
        if (PlatformServices.platformHelper.isFishingRod(stack) && entity instanceof LivingEntity livingEntity) {
            return Math.min(25, original + 5 * AbilityHelper.getEnchantmentLevelIncrease(Enchantments.LURE, livingEntity));
        }
        return original;
    }

    @ModifyReturnValue(method = "getFishingLuckBonus", at = @At("RETURN"))
    private static int increaseFishingLuckBonus(int original, ServerLevel level, ItemStack stack, Entity entity) {
        if (PlatformServices.platformHelper.isFishingRod(stack) && entity instanceof LivingEntity livingEntity) {
            return original + AbilityHelper.getEnchantmentLevelIncrease(Enchantments.LUCK_OF_THE_SEA, livingEntity);
        }
        return original;
    }
}
