package artifacts.component.ability;

import artifacts.config.value.Value;
import artifacts.config.value.ValueTypes;
import artifacts.equipment.EquipmentHelper;
import artifacts.registry.ModAttributes;
import artifacts.registry.ModDataComponents;
import artifacts.registry.ModSoundEvents;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

public record DoubleJumpAbility(Value<Boolean> enabled, Value<Double> sprintHorizontalVelocity, Value<Double> sprintVerticalVelocity)
        implements EquipmentAbility {

    public static final Codec<DoubleJumpAbility> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ValueTypes.enabledField().forGetter(DoubleJumpAbility::enabled),
            ValueTypes.NON_NEGATIVE_DOUBLE.codec().optionalFieldOf("sprint_jump_horizontal_velocity", Value.of(0D)).forGetter(DoubleJumpAbility::sprintHorizontalVelocity),
            ValueTypes.NON_NEGATIVE_DOUBLE.codec().optionalFieldOf("sprint_jump_vertical_velocity", Value.of(0D)).forGetter(DoubleJumpAbility::sprintVerticalVelocity)
    ).apply(instance, DoubleJumpAbility::new));

    public static final StreamCodec<ByteBuf, DoubleJumpAbility> STREAM_CODEC = StreamCodec.composite(
            ValueTypes.BOOLEAN.streamCodec(),
            DoubleJumpAbility::enabled,
            ValueTypes.NON_NEGATIVE_DOUBLE.streamCodec(),
            DoubleJumpAbility::sprintHorizontalVelocity,
            ValueTypes.NON_NEGATIVE_DOUBLE.streamCodec(),
            DoubleJumpAbility::sprintVerticalVelocity,
            DoubleJumpAbility::new
    );


    public static void jump(Player player) {
        player.fallDistance = 0;

        double upwardsMotion = 0.5;
        if (player.hasEffect(MobEffects.JUMP)) {
            // noinspection ConstantConditions
            upwardsMotion += 0.1 * (player.getEffect(MobEffects.JUMP).getAmplifier() + 1);
        }
        if (player.isSprinting()) {
            upwardsMotion *= 1 + EquipmentHelper.maxDouble(
                    ModDataComponents.DOUBLE_JUMP.get(), player,
                    ability -> ability.sprintVerticalVelocity().get(), true
            );
        }

        Vec3 motion = player.getDeltaMovement();
        double motionMultiplier = 0;
        if (player.isSprinting()) {
            motionMultiplier = EquipmentHelper.maxDouble(
                    ModDataComponents.DOUBLE_JUMP.get(), player,
                    ability -> ability.sprintHorizontalVelocity().get(), true
            );
        }
        float direction = (float) (player.getYRot() * Math.PI / 180);
        player.setDeltaMovement(player.getDeltaMovement().add(
                -Mth.sin(direction) * motionMultiplier,
                upwardsMotion - motion.y,
                Mth.cos(direction) * motionMultiplier)
        );

        player.hasImpulse = true;

        player.awardStat(Stats.JUMP);
        if (player.isSprinting()) {
            player.causeFoodExhaustion(0.2F);
        } else {
            player.causeFoodExhaustion(0.05F);
        }

        if (!player.level().isClientSide()) {
            double chance = player.getAttributeValue(ModAttributes.FLATULENCE);
            if (player.getRandom().nextFloat() < chance) {
                player.level().playSound(null, player, ModSoundEvents.FART.value(), SoundSource.PLAYERS, 1, 0.9F + player.getRandom().nextFloat() * 0.2F);
            } else {
                player.level().playSound(null, player, SoundEvents.WOOL_FALL, SoundSource.PLAYERS, 1, 0.9F + player.getRandom().nextFloat() * 0.2F);
            }
        }
    }

    @Override
    public boolean isNonCosmetic() {
        return enabled().get();
    }
}
