package artifacts.ability;

import artifacts.config.value.Value;
import artifacts.config.value.ValueTypes;
import artifacts.registry.ModDataComponents;
import artifacts.util.AbilityHelper;
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

import java.util.Optional;

public record ApplyCooldownAfterDamageAbility(Value<Integer> cooldown, Optional<TagKey<DamageType>> tag) implements TooltiplessAbility {

    public static final Codec<ApplyCooldownAfterDamageAbility> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ValueTypes.DURATION.codec().fieldOf("cooldown").forGetter(ApplyCooldownAfterDamageAbility::cooldown),
            TagKey.codec(Registries.DAMAGE_TYPE).optionalFieldOf("tag").forGetter(ApplyCooldownAfterDamageAbility::tag)
    ).apply(instance, ApplyCooldownAfterDamageAbility::new));

    public static final StreamCodec<ByteBuf, ApplyCooldownAfterDamageAbility> STREAM_CODEC = StreamCodec.composite(
            ValueTypes.DURATION.streamCodec(),
            ApplyCooldownAfterDamageAbility::cooldown,
            ByteBufCodecs.optional(ModCodecs.tagKeyStreamCodec(Registries.DAMAGE_TYPE)),
            ApplyCooldownAfterDamageAbility::tag,
            ApplyCooldownAfterDamageAbility::new
    );

    public static void onLivingDamaged(LivingEntity entity, DamageSource damageSource) {
        AbilityHelper.applyCooldowns(ModDataComponents.APPLY_COOLDOWN_AFTER_DAMAGE.get(), entity, ability -> {
            if (ability.tag().isEmpty() || damageSource.is(ability.tag().get())) {
                return ability.cooldown().get();
            }
            return 0;
        });
    }

    @Override
    public boolean isNonCosmetic() {
        return cooldown.get() > 0;
    }
}
