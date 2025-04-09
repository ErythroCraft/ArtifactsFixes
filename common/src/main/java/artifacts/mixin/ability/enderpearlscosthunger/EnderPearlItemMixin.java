package artifacts.mixin.ability.enderpearlscosthunger;

import artifacts.registry.ModAbilities;
import artifacts.util.AbilityHelper;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.EnderpearlItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(EnderpearlItem.class)
public abstract class EnderPearlItemMixin extends Item {

    public EnderPearlItemMixin(Properties properties) {
        super(properties);
    }

    @WrapOperation(method = "use", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;consume(ILnet/minecraft/world/entity/LivingEntity;)V"))
    private void shouldConsumeEnderPearl(ItemStack stack, int amount, LivingEntity entity, Operation<Void> operation) {
        if (AbilityHelper.hasAbilityActive(ModAbilities.ENDER_PEARLS_COST_HUNGER.get(), entity) && entity instanceof Player player) {
            int cost = AbilityHelper.minInt(ModAbilities.ENDER_PEARLS_COST_HUNGER.get(), player, 20, ability -> ability.cost().get(), false);
            if (player.getFoodData().getFoodLevel() >= cost) {
                if (cost > 0 && !player.isCreative()) {
                    player.getFoodData().setFoodLevel(player.getFoodData().getFoodLevel() - cost);
                    entity.level().playSound(
                            null,
                            player.getX(),
                            player.getY(),
                            player.getZ(),
                            SoundEvents.GENERIC_EAT,
                            SoundSource.PLAYERS,
                            0.5F,
                            0.8F + entity.getRandom().nextFloat() * 0.4F
                    );
                }
                int cooldown = AbilityHelper.maxInt(ModAbilities.ENDER_PEARLS_COST_HUNGER.get(), player, ability -> ability.cooldown().get(), false);
                if (!player.isCreative()) {
                    player.getCooldowns().addCooldown(this, cooldown * 20);
                }
                return;
            }
        }
        operation.call(stack, amount, entity);
    }
}
