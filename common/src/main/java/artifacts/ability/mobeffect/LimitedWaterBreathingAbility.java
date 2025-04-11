package artifacts.ability.mobeffect;

import artifacts.ability.AbilityWithTooltip;
import artifacts.config.value.Value;
import artifacts.config.value.ValueTypes;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;

import java.util.Objects;

public class LimitedWaterBreathingAbility extends ConstantMobEffectAbility implements AbilityWithTooltip {

    public static final Codec<LimitedWaterBreathingAbility> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ValueTypes.DURATION.codec().fieldOf("duration").forGetter(LimitedWaterBreathingAbility::duration),
            ValueTypes.BOOLEAN.codec().optionalFieldOf("infinite", Value.of(false)).forGetter(ability -> ability.isInfinite)
    ).apply(instance, LimitedWaterBreathingAbility::new));

    public static final StreamCodec<ByteBuf, LimitedWaterBreathingAbility> STREAM_CODEC = StreamCodec.composite(
            ValueTypes.DURATION.streamCodec(),
            LimitedWaterBreathingAbility::duration,
            ValueTypes.BOOLEAN.streamCodec(),
            ability -> ability.isInfinite,
            LimitedWaterBreathingAbility::new
    );

    private final Value<Integer> duration;
    private final Value<Boolean> isInfinite;

    public LimitedWaterBreathingAbility(Value<Integer> duration, Value<Boolean> isInfinite) {
        super(MobEffects.WATER_BREATHING);
        this.duration = duration;
        this.isInfinite = isInfinite;
    }

    public boolean isInfinite() {
        return isInfinite.get();
    }

    @Override
    public Value<Integer> duration() {
        return duration;
    }

    @Override
    public void addToTooltip(TooltipWriter writer) {
        if (isInfinite()) {
            writer.add("infinite");
        } else {
            writer.add("limited");
        }
    }

    protected int getAdditionalDuration(LivingEntity target) {
        if (!isInfinite()
                && target instanceof Player
                && target.getItemBySlot(EquipmentSlot.HEAD).is(Items.TURTLE_HELMET)
                && !target.isEyeInFluid(FluidTags.WATER)
        ) {
            return  200;
        }
        return 0;
    }

    @Override
    public boolean shouldShowIcon() {
        return !isInfinite();
    }

    @Override
    public boolean shouldApplyMobEffect(LivingEntity entity) {
        return isInfinite() || !entity.isEyeInFluid(FluidTags.WATER);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        LimitedWaterBreathingAbility that = (LimitedWaterBreathingAbility) o;
        return duration.equals(that.duration) && isInfinite.equals(that.isInfinite);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), duration, isInfinite);
    }
}
