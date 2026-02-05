package artifacts.component.ability;

import artifacts.config.value.Value;
import artifacts.config.value.ValueTypes;
import artifacts.equipment.EquipmentHelper;
import artifacts.registry.ModDataComponents;
import artifacts.util.ModCodecs;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

import java.util.Optional;

public record PostDamageCooldown(Value<Integer> cooldown, Optional<TagKey<DamageType>> tag) implements EquipmentAbility {

    public static final Codec<PostDamageCooldown> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ValueTypes.DURATION.codec().fieldOf("cooldown").forGetter(PostDamageCooldown::cooldown),
            TagKey.codec(Registries.DAMAGE_TYPE).optionalFieldOf("tag").forGetter(PostDamageCooldown::tag)
    ).apply(instance, PostDamageCooldown::new));

    public static final StreamCodec<ByteBuf, PostDamageCooldown> STREAM_CODEC = StreamCodec.composite(
            ValueTypes.DURATION.streamCodec(),
            PostDamageCooldown::cooldown,
            ByteBufCodecs.optional(ModCodecs.tagKeyStreamCodec(Registries.DAMAGE_TYPE)),
            PostDamageCooldown::tag,
            PostDamageCooldown::new
    );

    public static void onLivingDamaged(LivingEntity entity, DamageSource damageSource) {
        if (entity instanceof Player player && !player.level().isClientSide()) {
            EquipmentHelper.iterateAbilities(ModDataComponents.POST_DAMAGE_COOLDOWN.get(), entity, true, true, (ability, stack) -> {
                if (ability.tag().isEmpty() || damageSource.is(ability.tag().get())) {
                    int c = ability.cooldown().get() * 20;
                    if (c > 0) {
                        player.getCooldowns().addCooldown(stack, c);
                    }
                }
            });
        }
    }

    @Override
    public boolean isNonCosmetic() {
        return cooldown.get() > 0;
    }
}
