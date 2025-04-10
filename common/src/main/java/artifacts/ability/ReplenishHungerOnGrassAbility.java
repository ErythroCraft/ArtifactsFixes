package artifacts.ability;

import artifacts.ability.mobeffect.ApplyMobEffectAfterEatingAbility;
import artifacts.config.value.Value;
import artifacts.config.value.ValueTypes;
import artifacts.network.PlaySoundAtPlayerPacket;
import artifacts.registry.ModTags;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.LivingEntity;

public record ReplenishHungerOnGrassAbility(Value<Boolean> enabled, Value<Integer> replenishingDuration) implements ArtifactAbility {

    public static final Codec<ReplenishHungerOnGrassAbility> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ValueTypes.enabledField().forGetter(ReplenishHungerOnGrassAbility::enabled),
            ValueTypes.DURATION.codec().fieldOf("duration").forGetter(ReplenishHungerOnGrassAbility::replenishingDuration)
    ).apply(instance, ReplenishHungerOnGrassAbility::new));

    public static final StreamCodec<ByteBuf, ReplenishHungerOnGrassAbility> STREAM_CODEC = StreamCodec.composite(
            ValueTypes.BOOLEAN.streamCodec(),
            ReplenishHungerOnGrassAbility::enabled,
            ValueTypes.DURATION.streamCodec(),
            ReplenishHungerOnGrassAbility::replenishingDuration,
            ReplenishHungerOnGrassAbility::new
    );

    @Override
    public boolean isNonCosmetic() {
        return enabled().get();
    }

    @Override
    public boolean isTickingAbility() {
        return true;
    }

    @Override
    public void wornTick(LivingEntity entity, boolean isOnCooldown, boolean isDisabled) {
        if (!isDisabled && !isOnCooldown && isNonCosmetic() && entity instanceof ServerPlayer player
                && player.onGround()
                && player.getFoodData().needsFood()
                && entity.tickCount % (Math.max(1, replenishingDuration().get()) * 20) == 0
                && entity.getBlockStateOn().is(ModTags.ROOTED_BOOTS_GRASS)
        ) {
            player.getFoodData().eat(1, 0.5F);
            ApplyMobEffectAfterEatingAbility.applyEffects(entity, 1);
            PlaySoundAtPlayerPacket.sendSound(player, BuiltInRegistries.SOUND_EVENT.wrapAsHolder(SoundEvents.GENERIC_EAT), 0.5F, 0.8F + entity.getRandom().nextFloat() * 0.4F);
        }
    }
}
