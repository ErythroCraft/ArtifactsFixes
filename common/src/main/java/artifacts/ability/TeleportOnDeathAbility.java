package artifacts.ability;

import artifacts.config.value.Value;
import artifacts.config.value.ValueTypes;
import artifacts.registry.ModDataComponents;
import artifacts.util.AbilityHelper;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.Vec3;

public record TeleportOnDeathAbility(Value<Double> teleportationChance, Value<Integer> healthRestored, Value<Integer> cooldown, Value<Boolean> consumedOnUse)
        implements EquipmentAbility, AbilityWithTooltip {

    public static final Codec<TeleportOnDeathAbility> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ValueTypes.FRACTION.codec().optionalFieldOf("chance", Value.of(1D)).forGetter(TeleportOnDeathAbility::teleportationChance),
            ValueTypes.NON_NEGATIVE_INT.codec().optionalFieldOf("health_restored", Value.of(10)).forGetter(TeleportOnDeathAbility::healthRestored),
            ValueTypes.cooldownField().forGetter(TeleportOnDeathAbility::cooldown),
            ValueTypes.BOOLEAN.codec().optionalFieldOf("consume", Value.of(true)).forGetter(TeleportOnDeathAbility::consumedOnUse)
    ).apply(instance, TeleportOnDeathAbility::new));

    public static final StreamCodec<ByteBuf, TeleportOnDeathAbility> STREAM_CODEC = StreamCodec.composite(
            ValueTypes.FRACTION.streamCodec(),
            TeleportOnDeathAbility::teleportationChance,
            ValueTypes.NON_NEGATIVE_INT.streamCodec(),
            TeleportOnDeathAbility::healthRestored,
            ValueTypes.DURATION.streamCodec(),
            TeleportOnDeathAbility::cooldown,
            ValueTypes.BOOLEAN.streamCodec(),
            TeleportOnDeathAbility::consumedOnUse,
            TeleportOnDeathAbility::new
    );

    public static ItemStack findTotem(LivingEntity entity) {
        for (InteractionHand hand : InteractionHand.values()) {
            ItemStack handItem = entity.getItemInHand(hand);
            TeleportOnDeathAbility ability = handItem.get(ModDataComponents.TELEPORT_ON_DEATH.get());
            if (!handItem.has(ModDataComponents.DISABLED_BY_TOGGLE.get())
                    && ability != null && ability.isNonCosmetic()
                    && !(entity instanceof Player player && player.getCooldowns().isOnCooldown(handItem.getItem()))
            ) {
                return handItem;
            }
        }

        return AbilityHelper.reduceAbilities(
                ModDataComponents.TELEPORT_ON_DEATH.get(), entity, true, true, ItemStack.EMPTY,
                (ability, totem, result) -> result.isEmpty() ? totem : result
        );
    }

    public static void teleport(LivingEntity entity, ServerLevel level) {
        double oldX = entity.getX();
        double oldY = entity.getY();
        double oldZ = entity.getZ();

        for (int i = 0; i < 32; ++i) {
            double newX = entity.getX() + (entity.getRandom().nextDouble() - 0.5) * 32;
            double newY = Mth.clamp(entity.getY() + entity.getRandom().nextInt(16) - 8, level.getMinBuildHeight(), level.getMinBuildHeight() + level.getLogicalHeight() - 1);
            double newZ = entity.getZ() + (entity.getRandom().nextDouble() - 0.5) * 32;

            Vec3 oldPos = entity.position();
            if (oldPos.distanceToSqr(newX, newY, newZ) < 16 * 16) {
                continue;
            }

            if (entity.isPassenger()) {
                entity.stopRiding();
            }

            if (entity.randomTeleport(newX, newY, newZ, true)) {
                entity.level().gameEvent(GameEvent.TELEPORT, oldPos, GameEvent.Context.of(entity));
                entity.level().playSound(null, oldX, oldY, oldZ, SoundEvents.CHORUS_FRUIT_TELEPORT, SoundSource.PLAYERS, 1, 1);
                entity.level().playSound(null, newX, newY, newZ, SoundEvents.CHORUS_FRUIT_TELEPORT, SoundSource.PLAYERS, 1, 1);
                break;
            }
        }
    }

    @Override
    public boolean isNonCosmetic() {
        return !Mth.equal(teleportationChance().get(), 0);
    }

    @Override
    public void addToTooltip(TooltipWriter writer) {if (Mth.equal(teleportationChance().get(), 0)) {
        writer.add("constant");
    } else {
        writer.add("chance");
    }
        if (!consumedOnUse().get()) {
            writer.add("not_consumed");
        }
    }
}
