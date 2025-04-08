package artifacts.mixin.ability.applyeffectsaftereating;

import artifacts.ability.mobeffect.ApplyMobEffectAfterEatingAbility;
import artifacts.event.ArtifactHooks;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Player.class)
public abstract class PlayerMixin extends LivingEntity {

    protected PlayerMixin(EntityType<? extends LivingEntity> entityType, Level level) {
        super(entityType, level);
        throw new UnsupportedOperationException();
    }

    @Inject(method = "eat", at = @At("HEAD"))
    public void eat(Level level, ItemStack stack, FoodProperties properties, CallbackInfoReturnable<ItemStack> cir) {
        ApplyMobEffectAfterEatingAbility.applyEffects(this, properties);
        ArtifactHooks.applyBoneMealAfterEating(this, properties);
    }
}
