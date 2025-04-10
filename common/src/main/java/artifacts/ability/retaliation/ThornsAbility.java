package artifacts.ability.retaliation;

import artifacts.config.value.Value;
import artifacts.config.value.ValueTypes;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.LivingEntity;

public class ThornsAbility extends RetaliationAbility {

    public static final Codec<ThornsAbility> CODEC = RecordCodecBuilder.create(instance -> codecStart(instance)
            .and(ValueTypes.NON_NEGATIVE_INT.codec().fieldOf("min_damage").forGetter(ThornsAbility::minDamage))
            .and(ValueTypes.NON_NEGATIVE_INT.codec().fieldOf("max_damage").forGetter(ThornsAbility::maxDamage))
            .apply(instance, ThornsAbility::new)
    );

    public static final StreamCodec<ByteBuf, ThornsAbility> STREAM_CODEC = StreamCodec.composite(
            ValueTypes.FRACTION.streamCodec(),
            ThornsAbility::strikeChance,
            ValueTypes.DURATION.streamCodec(),
            ThornsAbility::cooldown,
            ValueTypes.NON_NEGATIVE_INT.streamCodec(),
            ThornsAbility::minDamage,
            ValueTypes.NON_NEGATIVE_INT.streamCodec(),
            ThornsAbility::maxDamage,
            ThornsAbility::new
    );

    private final Value<Integer> minDamage;
    private final Value<Integer> maxDamage;

    public ThornsAbility(Value<Double> strikeChance, Value<Integer> cooldown, Value<Integer> minDamage, Value<Integer> maxDamage) {
        super(strikeChance, cooldown);
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
        if (!(o instanceof ThornsAbility that)) return false;
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
