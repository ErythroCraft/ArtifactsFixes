package artifacts.ability.mobeffect;

import artifacts.config.value.Value;
import net.minecraft.core.Holder;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.Nullable;

public abstract class ConstantMobEffectAbility implements MobEffectAbility {

    private final Holder<MobEffect> mobEffect;
    protected final Value<Integer> level;

    protected ConstantMobEffectAbility(Holder<MobEffect> mobEffect) {
        this(mobEffect, Value.of(1));
    }

    protected ConstantMobEffectAbility(Holder<MobEffect> mobEffect, Value<Integer> level) {
        this.mobEffect = mobEffect;
        this.level = level;
    }

    @Override
    public Holder<MobEffect> mobEffect() {
        return mobEffect;
    }

    @Override
    public Value<Integer> level() {
        return level;
    }

    public boolean isInfinite() {
        return true;
    }

    @Override
    public boolean shouldShowIcon() {
        return true;
    }

    @Override
    public int getDuration(LivingEntity entity) {
        return MobEffectAbility.super.getDuration(entity) + getAdditionalDuration(entity) + 19;
    }

    public Value<Integer> duration() {
        return Value.of(10);
    }

    protected int getAdditionalDuration(LivingEntity target) {
        return 0;
    }

    @Nullable
    protected LivingEntity getTarget(LivingEntity entity) {
        return entity;
    }

    protected boolean shouldApplyMobEffect(LivingEntity entity) {
        return true;
    }

    @Override
    public boolean isTickingAbility() {
        return true;
    }

    @Override
    public void wornTick(LivingEntity entity, boolean isOnCooldown, boolean isDisabled) {
        if (!entity.level().isClientSide() && !isDisabled && !isOnCooldown && isNonCosmetic() && shouldApplyMobEffect(entity)) {
            LivingEntity target = getTarget(entity);
            if (target != null) {
                target.addEffect(createEffect(entity));
            }
        }
    }

    @Override
    public void onUnequip(LivingEntity entity) {
        if (!entity.level().isClientSide() && getTarget(entity) == entity) {
            MobEffectInstance instance = entity.getEffect(mobEffect());
            if (instance != null
                    && instance.getAmplifier() == getAmplifier()
                    && !instance.isVisible()
                    && instance.endsWithin(duration().get() * 20 + 19)
            ) {
                entity.removeEffect(mobEffect());
            }
        }
    }
}
