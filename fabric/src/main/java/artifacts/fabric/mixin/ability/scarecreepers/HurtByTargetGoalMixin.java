package artifacts.fabric.mixin.ability.scarecreepers;

import artifacts.registry.ModDataComponents;
import artifacts.registry.ModTags;
import artifacts.util.AbilityHelper;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.TargetGoal;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(HurtByTargetGoal.class)
public abstract class HurtByTargetGoalMixin extends TargetGoal {

    public HurtByTargetGoalMixin(Mob mob, boolean checkVisibility) {
        super(mob, checkVisibility);
        throw new UnsupportedOperationException();
    }

    @Inject(method = "canUse", at = @At("HEAD"), cancellable = true)
    private void cancelRevenge(CallbackInfoReturnable<Boolean> info) {
        LivingEntity attacker = mob.getLastHurtByMob();
        if (ModTags.isInTag(mob.getType(), ModTags.CREEPERS) && AbilityHelper.hasAbilityActive(ModDataComponents.SCARE_CREEPERS.get(), attacker, true)) {
            info.setReturnValue(false); // early return intended!
        }
    }
}
