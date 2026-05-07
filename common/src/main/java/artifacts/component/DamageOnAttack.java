package artifacts.component;

import artifacts.config.value.Value;
import artifacts.config.value.ValueTypes;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public record DamageOnAttack(Value<Integer> itemDamage, boolean requireMelee, boolean requireKill) {

    public static final Codec<DamageOnAttack> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ValueTypes.itemDamageField(1).forGetter(DamageOnAttack::itemDamage),
            Codec.BOOL.optionalFieldOf("require_melee", false).forGetter(DamageOnAttack::requireMelee),
            Codec.BOOL.optionalFieldOf("require_kill", false).forGetter(DamageOnAttack::requireKill)
    ).apply(instance, DamageOnAttack::new));

    public static final StreamCodec<ByteBuf, DamageOnAttack> STREAM_CODEC = StreamCodec.composite(
            ValueTypes.DURATION.streamCodec(),
            DamageOnAttack::itemDamage,
            ByteBufCodecs.BOOL,
            DamageOnAttack::requireMelee,
            ByteBufCodecs.BOOL,
            DamageOnAttack::requireKill,
            DamageOnAttack::new
    );
}
