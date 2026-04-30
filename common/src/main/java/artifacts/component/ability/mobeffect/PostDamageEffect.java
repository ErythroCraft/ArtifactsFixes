package artifacts.component.ability.mobeffect;

import artifacts.component.ability.EquipmentAbility;
import artifacts.config.value.Value;
import artifacts.config.value.ValueTypes;
import artifacts.equipment.EquipmentHelper;
import artifacts.registry.ModDataComponents;
import artifacts.util.ItemDamageUtil;
import artifacts.util.ModCodecs;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;

import java.util.Optional;

public record PostDamageEffect(
        MobEffectProvider provider,
        Optional<TagKey<DamageType>> tag,
        Value<Double> chance,
        Value<Integer> itemDamage
) implements EquipmentAbility {

    public static final Codec<PostDamageEffect> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            MobEffectProvider.codec(true).fieldOf("effect").forGetter(PostDamageEffect::provider),
            TagKey.codec(Registries.DAMAGE_TYPE).optionalFieldOf("tag").forGetter(PostDamageEffect::tag),
            ValueTypes.FRACTION.codec().optionalFieldOf("chance", Value.of(1D)).forGetter(PostDamageEffect::chance),
            ValueTypes.itemDamageField().forGetter(PostDamageEffect::itemDamage)
    ).apply(instance, PostDamageEffect::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, PostDamageEffect> STREAM_CODEC = StreamCodec.composite(
            MobEffectProvider.STREAM_CODEC,
            PostDamageEffect::provider,
            ByteBufCodecs.optional(ModCodecs.tagKeyStreamCodec(Registries.DAMAGE_TYPE)),
            PostDamageEffect::tag,
            ValueTypes.FRACTION.streamCodec(),
            PostDamageEffect::chance,
            ValueTypes.NON_NEGATIVE_INT.streamCodec(),
            PostDamageEffect::itemDamage,
            PostDamageEffect::new
    );

    public static void onLivingDamaged(LivingEntity entity, DamageSource damageSource) {
        if (!entity.level().isClientSide()) {
            EquipmentHelper.iterateAbilities(
                    ModDataComponents.POST_DAMAGE_EFFECTS.get(), entity,
                    true,
                    true,
                    (ability, slotAccess) -> {
                        for (PostDamageEffect entry : ability.entries()) {
                            if (entry.shouldApply(damageSource, entity)) {
                                entity.addEffect(entry.provider.createEffect());
                                ItemDamageUtil.hurtAndBreak(slotAccess, entry.itemDamage.get(), entity);
                            }
                        }
                    }
            );
        }
    }

    public boolean shouldApply(DamageSource damageSource, LivingEntity entity) {
        return provider.canApply(entity)
                && entity.getRandom().nextDouble() < chance.get()
                && (tag.isEmpty() || damageSource.is(tag.get()));
    }

    @Override
    public boolean isNonCosmetic() {
        return provider.isNonCosmetic() && chance.get() > 0;
    }

    @Override
    public void addToTooltip(TooltipWriter writer) {
        if (provider.mobEffect().equals(MobEffects.FIRE_RESISTANCE) && tag.isPresent() && tag.get().equals(DamageTypeTags.IS_FIRE) && chance.get() == 1) {
            writer.add("fire_resistance");
        } else if (provider.mobEffect().equals(MobEffects.SPEED) && tag.isEmpty() && chance.get() == 1) {
            writer.add("speed");
        }
    }
}
