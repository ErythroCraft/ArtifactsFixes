package artifacts.component.ability.retaliation;

import artifacts.config.value.Value;
import artifacts.config.value.ValueTypes;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;

public class LightningEffect extends RetaliationEffect {

    public static final Codec<LightningEffect> CODEC = RecordCodecBuilder.create(
            instance -> codecStart(instance).apply(instance, LightningEffect::new)
    );

    public static final StreamCodec<ByteBuf, LightningEffect> STREAM_CODEC = StreamCodec.composite(
            ValueTypes.FRACTION.streamCodec(),
            LightningEffect::strikeChance,
            ValueTypes.DURATION.streamCodec(),
            LightningEffect::cooldown,
            LightningEffect::new
    );

    public LightningEffect(Value<Double> strikeChance, Value<Integer> cooldown) {
        super("lightning", strikeChance, cooldown);
    }

    @Override
    protected void applyEffect(LivingEntity target, LivingEntity attacker) {
        if (attacker.level().canSeeSky(BlockPos.containing(attacker.position()))) {
            LightningBolt lightningBolt = EntityType.LIGHTNING_BOLT.create(attacker.level());
            if (lightningBolt != null) {
                lightningBolt.moveTo(Vec3.atBottomCenterOf(attacker.blockPosition()));
                lightningBolt.setCause(attacker instanceof ServerPlayer player ? player : null);
                attacker.level().addFreshEntity(lightningBolt);
            }
        }
    }
}
