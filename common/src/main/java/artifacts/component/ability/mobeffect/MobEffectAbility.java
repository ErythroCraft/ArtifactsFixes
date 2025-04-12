package artifacts.component.ability.mobeffect;

import artifacts.component.ability.EquipmentAbility;
import artifacts.config.value.Value;
import artifacts.config.value.ValueTypes;
import com.mojang.datafixers.Products;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;

public interface MobEffectAbility extends EquipmentAbility {

    static <T extends MobEffectAbility> Products.P2<RecordCodecBuilder.Mu<T>, Holder<MobEffect>, Value<Integer>> codecStart(RecordCodecBuilder.Instance<T> instance) {
        return instance.group(
                BuiltInRegistries.MOB_EFFECT.holderByNameCodec().fieldOf("mob_effect").forGetter(MobEffectAbility::mobEffect),
                ValueTypes.mobEffectLevelField().forGetter(MobEffectAbility::level)
        );
    }

    static <T extends MobEffectAbility> Products.P3<RecordCodecBuilder.Mu<T>, Holder<MobEffect>, Value<Integer>, Value<Integer>> codecStartWithDuration(RecordCodecBuilder.Instance<T> instance) {
        return codecStart(instance).and(ValueTypes.DURATION.codec().fieldOf("duration").forGetter(MobEffectAbility::duration));
    }

    Holder<MobEffect> mobEffect();

    Value<Integer> level();

    Value<Integer> duration();

    default int getAmplifier() {
        return level().get() - 1;
    }

    default boolean isVisible() {
        return false;
    }

    default boolean shouldShowIcon() {
        return isVisible();
    }

    default int getDuration(LivingEntity entity) {
        return duration().get() * 20;
    }

    default MobEffectInstance createEffect(LivingEntity entity) {
        return createEffect(getDuration(entity));
    }

    default MobEffectInstance createEffect(int duration) {
        return new MobEffectInstance(mobEffect(), duration, getAmplifier(), false, isVisible(), shouldShowIcon());
    }

    @Override
    default boolean isNonCosmetic() {
        return level().get() > 0 && duration().get() > 0;
    }
}
