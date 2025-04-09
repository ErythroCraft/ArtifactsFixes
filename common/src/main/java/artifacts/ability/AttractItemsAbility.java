package artifacts.ability;

import artifacts.config.value.Value;
import artifacts.config.value.ValueTypes;
import artifacts.registry.ModAbilities;
import artifacts.util.AbilityHelper;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public record AttractItemsAbility(Value<Boolean> enabled) implements ArtifactAbility {

    public static final MapCodec<AttractItemsAbility> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            ValueTypes.enabledField().forGetter(AttractItemsAbility::enabled)
    ).apply(instance, AttractItemsAbility::new));

    public static final StreamCodec<ByteBuf, AttractItemsAbility> STREAM_CODEC = StreamCodec.composite(
            ValueTypes.BOOLEAN.streamCodec(),
            AttractItemsAbility::enabled,
            AttractItemsAbility::new
    );

    @SuppressWarnings("unused")
    public static void onItemToss(Player player, ItemEntity entity) {
        if (AbilityHelper.hasAbilityActive(ModAbilities.ATTRACT_ITEMS.value(), player, true)) {
            AbilityHelper.addCooldown(ModAbilities.ATTRACT_ITEMS.value(), player, 5 * 20);
        }
    }
    
    @Override
    public Type<?> getType() {
        return ModAbilities.ATTRACT_ITEMS.value();
    }

    @Override
    public boolean isNonCosmetic() {
        return enabled().get();
    }

    @Override
    public boolean shouldTick() {
        return true;
    }

    @Override
    public void wornTick(LivingEntity entity, boolean isOnCooldown, boolean isActive) {
        if (!isActive || isOnCooldown) {
            return;
        }
        Vec3 pos = entity.position().add(0, 0.75, 0);

        int range = 5;
        List<ItemEntity> items = entity.level().getEntitiesOfClass(ItemEntity.class, new AABB(pos.x - range, pos.y - range, pos.z - range, pos.x + range, pos.y + range, pos.z + range));
        int amountPulled = 0;
        for (ItemEntity item : items) {
            if (item.isAlive() && !item.hasPickUpDelay()) {
                if (amountPulled++ > 50) {
                    break;
                }

                Vec3 motion = pos.subtract(item.position().add(0, item.getBbHeight() / 2, 0));
                if (Math.sqrt(motion.x * motion.x + motion.y * motion.y + motion.z * motion.z) > 1) {
                    motion = motion.normalize();
                }
                item.setDeltaMovement(motion.scale(0.6));
            }
        }
    }
}
