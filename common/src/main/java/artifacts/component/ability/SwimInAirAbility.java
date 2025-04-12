package artifacts.component.ability;

import artifacts.component.SwimData;
import artifacts.config.value.Value;
import artifacts.config.value.ValueTypes;
import artifacts.equipment.EquipmentHelper;
import artifacts.platform.PlatformServices;
import artifacts.registry.ModDataComponents;
import artifacts.registry.ModKeyMappings;
import artifacts.registry.ModSoundEvents;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

// TODO add short cooldown after stopping swimming, remove full recharge requirement
// TODO allow cancelling flight
public record SwimInAirAbility(Value<Integer> flightDuration, Value<Integer> rechargeDuration)
        implements EquipmentAbility {

    public static final Codec<SwimInAirAbility> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ValueTypes.DURATION.codec().fieldOf("flight_duration").forGetter(SwimInAirAbility::flightDuration),
            ValueTypes.DURATION.codec().fieldOf("recharge_duration").forGetter(SwimInAirAbility::rechargeDuration)
    ).apply(instance, SwimInAirAbility::new));

    public static final StreamCodec<ByteBuf, SwimInAirAbility> STREAM_CODEC = StreamCodec.composite(
            ValueTypes.DURATION.streamCodec(),
            SwimInAirAbility::flightDuration,
            ValueTypes.DURATION.streamCodec(),
            SwimInAirAbility::rechargeDuration,
            SwimInAirAbility::new
    );

    public static void onHeliumFlamingoTick(Player player) {
        SwimData swimData = PlatformServices.platformHelper.getSwimData(player);
        if (swimData == null) {
            return;
        }
        int maxFlightTime = getFlightDuration(player);
        boolean shouldSink = EquipmentHelper.hasAbilityActive(ModDataComponents.SINKING.get(), player, true);
        boolean canFly = maxFlightTime > 0;
        if (swimData.isSwimming()) {
            if (swimData.getSwimTime() > maxFlightTime
                    || player.isInWater() && !player.isSwimming() && !shouldSink
                    || (!player.isInWater() || shouldSink) && player.onGround()
            ) {
                swimData.setSwimming(player, false);
                if (!player.onGround() && !player.isInWater()) {
                    player.playSound(ModSoundEvents.POP.value());
                }
            }

            if (canFly && !PlatformServices.platformHelper.isEyeInWater(player)) {
                if (!player.getAbilities().invulnerable) {
                    swimData.setSwimTime(swimData.getSwimTime() + 1);
                }
            }
        } else if (swimData.getSwimTime() < 0) {
            int rechargeTime = getRechargeDuration(player);
            swimData.setSwimTime(
                    swimData.getSwimTime() < -rechargeTime ? -rechargeTime : swimData.getSwimTime() + 1
            );
        }
    }

    public static int getFlightDuration(LivingEntity entity) {
        return EquipmentHelper.maxInt(ModDataComponents.SWIM_IN_AIR.get(), entity, ability -> ability.flightDuration().get() * 20, false);
    }

    public static int getRechargeDuration(LivingEntity entity) {
        return Math.max(20, EquipmentHelper.maxInt(ModDataComponents.SWIM_IN_AIR.get(), entity, ability -> ability.rechargeDuration().get() * 20, false));
    }

    @Override
    public boolean isNonCosmetic() {
        return flightDuration().get() > 0;
    }

    @Override
    public void addToTooltip(TooltipWriter writer) {
        writer.add("swimming");
        writer.add("keymapping", ModKeyMappings.getHeliumFlamingoKey().getTranslatedKeyMessage());
    }
}
