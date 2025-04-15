package artifacts.component.ability.mobeffect;

import artifacts.component.ability.EquipmentAbility;
import artifacts.config.value.Value;
import artifacts.config.value.ValueTypes;
import artifacts.equipment.EquipmentHelper;
import artifacts.registry.ModDataComponents;
import artifacts.util.DamageSourceHelper;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

import java.util.List;
import java.util.Objects;
import java.util.Set;

public record AttacksInflictMobEffectAbility(List<Entry> effects)
        implements EquipmentAbility {

    private static final Set<Holder<MobEffect>> CUSTOM_TOOLTIP_MOB_EFFECTS = Set.of(
            MobEffects.WITHER
    );

    public static final Codec<AttacksInflictMobEffectAbility> CODEC = Entry.CODEC.listOf().xmap(
            AttacksInflictMobEffectAbility::new, AttacksInflictMobEffectAbility::effects
    );

    public static final StreamCodec<RegistryFriendlyByteBuf, AttacksInflictMobEffectAbility> STREAM_CODEC = ByteBufCodecs.<RegistryFriendlyByteBuf, Entry>list()
            .apply(Entry.STREAM_CODEC).map(AttacksInflictMobEffectAbility::new, AttacksInflictMobEffectAbility::effects);

    public static void onLivingHurt(LivingEntity entity, DamageSource damageSource) {
        LivingEntity attacker = DamageSourceHelper.getAttacker(damageSource);
        if (attacker != null && DamageSourceHelper.isMeleeAttack(damageSource) && !entity.level().isClientSide()) {
            EquipmentHelper.iterateAbilities(ModDataComponents.ATTACKS_INFLICT_MOB_EFFECT.get(), attacker, true, true, (ability, stack) -> {
                for (Entry effect : ability.effects) {
                    if (effect.shouldApply(entity)) {
                        entity.addEffect(effect.provider().createEffect(), attacker);
                        if (attacker instanceof Player player) {
                            player.getCooldowns().addCooldown(stack.getItem(), effect.cooldown().get() * 20);
                        }
                    }
                }
            });
        }
    }

    @Override
    public boolean isNonCosmetic() {
        for (Entry effect : effects) {
            if (effect.isNonCosmetic()) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void addToTooltip(TooltipWriter writer) {
        for (Entry effect : effects) {
            for (Holder<MobEffect> mobEffect : CUSTOM_TOOLTIP_MOB_EFFECTS) {
                if (mobEffect.isBound() && mobEffect.value() == effect.provider.mobEffect().value() && effect.isNonCosmetic()) {
                    String name = Objects.requireNonNull(BuiltInRegistries.MOB_EFFECT.getKey(mobEffect.value())).getPath();
                    if (Mth.equal(effect.chance().get(), 1)) {
                        writer.add(name + ".constant");
                    } else {
                        writer.add(name + ".chance");
                    }
                    return;
                }
            }
        }
    }

    public record Entry(MobEffectProvider provider, Value<Double> chance, Value<Integer> cooldown) {

        public static final Codec<Entry> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                MobEffectProvider.codec(true).fieldOf("effect").forGetter(Entry::provider),
                ValueTypes.FRACTION.codec().optionalFieldOf("chance", Value.of(1D)).forGetter(Entry::chance),
                ValueTypes.DURATION.codec().optionalFieldOf("cooldown", Value.of(1)).forGetter(Entry::cooldown)
        ).apply(instance, Entry::new));

        public static final StreamCodec<RegistryFriendlyByteBuf, Entry> STREAM_CODEC = StreamCodec.composite(
                MobEffectProvider.STREAM_CODEC,
                Entry::provider,
                ValueTypes.FRACTION.streamCodec(),
                Entry::chance,
                ValueTypes.DURATION.streamCodec(),
                Entry::cooldown,
                Entry::new
        );

        public boolean shouldApply(LivingEntity entity) {
            return chance().get() > entity.getRandom().nextDouble();
        }

        public boolean isNonCosmetic() {
            return provider().isNonCosmetic() && chance().get() > 0;
        }
    }
}
