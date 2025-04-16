package artifacts.component.ability.retaliation;

import artifacts.config.value.Value;
import artifacts.config.value.ValueTypes;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.LivingEntity;

public class ThornsEffect extends RetaliationEffect {

    public static final Codec<ThornsEffect> CODEC = RecordCodecBuilder.create(instance -> codecStart(instance)
            .and(ValueTypes.NON_NEGATIVE_INT.codec().fieldOf("min_damage").forGetter(ThornsEffect::minDamage))
            .and(ValueTypes.NON_NEGATIVE_INT.codec().fieldOf("max_damage").forGetter(ThornsEffect::maxDamage))
            .apply(instance, ThornsEffect::new)
    );

    public static final StreamCodec<ByteBuf, ThornsEffect> STREAM_CODEC = StreamCodec.composite(
            ValueTypes.FRACTION.streamCodec(),
            ThornsEffect::strikeChance,
            ValueTypes.DURATION.streamCodec(),
            ThornsEffect::cooldown,
            ValueTypes.NON_NEGATIVE_INT.streamCodec(),
            ThornsEffect::minDamage,
            ValueTypes.NON_NEGATIVE_INT.streamCodec(),
            ThornsEffect::maxDamage,
            ThornsEffect::new
    );

    private final Value<Integer> minDamage;
    private final Value<Integer> maxDamage;

    public ThornsEffect(Value<Double> strikeChance, Value<Integer> cooldown, Value<Integer> minDamage, Value<Integer> maxDamage) {
        super("thorns", strikeChance, cooldown);
        this.minDamage = minDamage;
        this.maxDamage = maxDamage;
    }

    public Value<Integer> minDamage() {
        return minDamage;
    }

    public Value<Integer> maxDamage() {
        return maxDamage;
    }

    @Override
    public boolean isNonCosmetic() {
        return super.isNonCosmetic() && maxDamage().get() > 0;
    }

    @Override
    protected void applyEffect(LivingEntity target, LivingEntity attacker) {
        if (attacker.attackable()) {
            int minDamage = minDamage().get();
            int maxDamage = maxDamage().get();
            if (maxDamage < minDamage) {
                minDamage = maxDamage;
            }
            int damage = minDamage + target.getRandom().nextInt(maxDamage - minDamage + 1);
            if (damage > 0) {
                attacker.hurt(target.damageSources().thorns(target), damage);
            }
        }
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ThornsEffect that)) return false;
        if (!super.equals(o)) return false;

        return minDamage.equals(that.minDamage) && maxDamage.equals(that.maxDamage);
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + minDamage.hashCode();
        result = 31 * result + maxDamage.hashCode();
        return result;
    }
}
