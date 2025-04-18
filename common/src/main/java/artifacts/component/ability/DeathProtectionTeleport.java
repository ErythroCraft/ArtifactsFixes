package artifacts.component.ability;

import artifacts.config.value.Value;
import artifacts.config.value.ValueTypes;
import artifacts.equipment.EquipmentHelper;
import artifacts.registry.ModDataComponents;
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

public record DeathProtectionTeleport(Value<Double> teleportationChance, Value<Integer> healthRestored, Value<Integer> cooldown, Value<Boolean> consumedOnUse)
        implements EquipmentAbility {

    public static final Codec<DeathProtectionTeleport> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ValueTypes.FRACTION.codec().optionalFieldOf("chance", Value.of(1D)).forGetter(DeathProtectionTeleport::teleportationChance),
            ValueTypes.NON_NEGATIVE_INT.codec().optionalFieldOf("health_restored", Value.of(10)).forGetter(DeathProtectionTeleport::healthRestored),
            ValueTypes.cooldownField().forGetter(DeathProtectionTeleport::cooldown),
            ValueTypes.BOOLEAN.codec().optionalFieldOf("consume", Value.of(true)).forGetter(DeathProtectionTeleport::consumedOnUse)
    ).apply(instance, DeathProtectionTeleport::new));

    public static final StreamCodec<ByteBuf, DeathProtectionTeleport> STREAM_CODEC = StreamCodec.composite(
            ValueTypes.FRACTION.streamCodec(),
            DeathProtectionTeleport::teleportationChance,
            ValueTypes.NON_NEGATIVE_INT.streamCodec(),
            DeathProtectionTeleport::healthRestored,
            ValueTypes.DURATION.streamCodec(),
            DeathProtectionTeleport::cooldown,
            ValueTypes.BOOLEAN.streamCodec(),
            DeathProtectionTeleport::consumedOnUse,
            DeathProtectionTeleport::new
    );

    public static ItemStack findTotem(LivingEntity entity) {
        for (InteractionHand hand : InteractionHand.values()) {
            ItemStack handItem = entity.getItemInHand(hand);
            DeathProtectionTeleport ability = handItem.get(ModDataComponents.DEATH_PROTECTION_TELEPORT.get());
            if (!handItem.has(ModDataComponents.DISABLED_BY_TOGGLE.get())
                    && ability != null && ability.isNonCosmetic()
                    && !(entity instanceof Player player && player.getCooldowns().isOnCooldown(handItem.getItem()))
            ) {
                return handItem;
            }
        }

        return EquipmentHelper.reduceAbilities(
                ModDataComponents.DEATH_PROTECTION_TELEPORT.get(), entity, true, true, ItemStack.EMPTY,
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
    public void addToTooltip(TooltipWriter writer) {
        if (Mth.equal(teleportationChance().get(), 1)) {
            writer.add("constant");
        } else {
            writer.add("chance", Math.round(teleportationChance().get() * 100));
        }
        if (!consumedOnUse().get()) {
            writer.add("not_consumed");
        }
    }
}
