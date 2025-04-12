package artifacts.mixin.ability.enchantment;

import artifacts.equipment.EquipmentHelper;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.functions.EnchantedCountIncreaseFunction;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(EnchantedCountIncreaseFunction.class)
public class EnchantedCountIncreaseFunctionMixin {

    @Shadow
    @Final
    private Holder<Enchantment> enchantment;

    @ModifyExpressionValue(method = "run", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/enchantment/EnchantmentHelper;getEnchantmentLevel(Lnet/minecraft/core/Holder;Lnet/minecraft/world/entity/LivingEntity;)I"))
    private int addLootingLevel(int level, ItemStack itemStack, LootContext lootContext) {
        Entity entity = lootContext.getParamOrNull(LootContextParams.THIS_ENTITY);

        if (this.enchantment.is(Enchantments.LOOTING) && entity instanceof LivingEntity livingEntity) {
            level += EquipmentHelper.getEnchantmentLevelIncrease(Enchantments.LOOTING, livingEntity);
        }

        return level;
    }
}
