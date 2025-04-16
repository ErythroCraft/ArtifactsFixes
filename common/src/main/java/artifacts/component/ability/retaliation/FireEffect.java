package artifacts.component.ability.retaliation;

import artifacts.config.value.Value;
import artifacts.config.value.ValueTypes;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;

public class FireEffect extends RetaliationEffect {

    public static final Codec<FireEffect> CODEC = RecordCodecBuilder.create(
            instance -> codecStart(instance)
                    .and(ValueTypes.DURATION.codec().fieldOf("duration").forGetter(FireEffect::fireDuration))
                    .and(ValueTypes.BOOLEAN.codec().optionalFieldOf("grant_fire_resistance", Value.of(true)).forGetter(FireEffect::grantsFireResistance))
                    .apply(instance, FireEffect::new)
    );

    public static final StreamCodec<ByteBuf, FireEffect> STREAM_CODEC = StreamCodec.composite(
            ValueTypes.FRACTION.streamCodec(),
            FireEffect::strikeChance,
            ValueTypes.DURATION.streamCodec(),
            FireEffect::cooldown,
            ValueTypes.DURATION.streamCodec(),
            FireEffect::fireDuration,
            ValueTypes.BOOLEAN.streamCodec(),
            FireEffect::grantsFireResistance,
            FireEffect::new
    );

    private final Value<Integer> fireDuration;
    private final Value<Boolean> grantsFireResistance;

    public FireEffect(Value<Double> strikeChance, Value<Integer> cooldown, Value<Integer> fireDuration, Value<Boolean> grantsFireResistance) {
        super("fire", strikeChance, cooldown);
        this.fireDuration = fireDuration;
        this.grantsFireResistance = grantsFireResistance;
    }

    public Value<Integer> fireDuration() {
        return fireDuration;
    }

    public Value<Boolean> grantsFireResistance() {
        return grantsFireResistance;
    }

    @Override
    public boolean isNonCosmetic() {
        return super.isNonCosmetic() && fireDuration().get() > 0;
    }

    @Override
    protected void applyEffect(LivingEntity target, LivingEntity attacker) {
        if (!attacker.fireImmune() && attacker.attackable() && fireDuration().get() > 0) {
            if (grantsFireResistance().get()) {
                target.addEffect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, fireDuration().get() * 20, 0, false, false, true));
            }
            attacker.igniteForSeconds(fireDuration().get());
        }
    }

    @Override
    public void addToTooltip(TooltipWriter writer) {
        super.addToTooltip(writer);
        if (grantsFireResistance().get()) {
            writer.add("fire.fire_resistance");
        }
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FireEffect that)) return false;
        if (!super.equals(o)) return false;

        return fireDuration.equals(that.fireDuration) && grantsFireResistance.equals(that.grantsFireResistance);
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + fireDuration.hashCode();
        result = 31 * result + grantsFireResistance.hashCode();
        return result;
    }
}
