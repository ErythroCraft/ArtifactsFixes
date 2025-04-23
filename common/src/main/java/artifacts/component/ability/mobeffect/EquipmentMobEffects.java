package artifacts.component.ability.mobeffect;

import artifacts.component.ability.CompositeAbility;
import artifacts.component.ability.EntityCondition;
import artifacts.component.ability.TickingAbility;
import artifacts.component.ability.TickingCompositeAbility;
import artifacts.config.value.Value;
import artifacts.registry.ModDataComponents;
import artifacts.registry.ModMobEffects;
import com.mojang.serialization.Codec;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;

import java.util.List;
import java.util.Objects;
import java.util.Set;

public record EquipmentMobEffects(List<Entry> entries) implements TickingCompositeAbility<EquipmentMobEffects.Entry> {

    private static final Set<Holder<MobEffect>> CUSTOM_TOOLTIP_MOB_EFFECTS = Set.of(
            net.minecraft.world.effect.MobEffects.INVISIBILITY,
            ModMobEffects.MAGNETISM
    );

    public static final Codec<EquipmentMobEffects> CODEC =
            CompositeAbility.codec(Entry.CODEC, EquipmentMobEffects::new, EquipmentMobEffects::entries);

    public static final StreamCodec<RegistryFriendlyByteBuf, EquipmentMobEffects> STREAM_CODEC =
            CompositeAbility.streamCodec(Entry.STREAM_CODEC, EquipmentMobEffects::new, EquipmentMobEffects::entries);

    public record Entry(MobEffectProvider provider) implements TickingAbility {

        public static final Codec<Entry> CODEC = MobEffectProvider.codec(false).xmap(Entry::new, Entry::provider);
        public static final StreamCodec<RegistryFriendlyByteBuf, Entry> STREAM_CODEC = MobEffectProvider.STREAM_CODEC.map(Entry::new, Entry::provider);

        @Override
        public void onUnequip(LivingEntity entity) {
            MobEffectInstance instance = entity.getEffect(provider.mobEffect());
            if (instance != null
                    && instance.getAmplifier() == provider.getAmplifier()
                    && instance.isVisible() == provider.spawnParticles().get()
                    && instance.showIcon() == provider.showIcon().get()
                    && instance.endsWithin(provider.getDuration(1) * 20 + 19)
            ) {
                entity.removeEffect(provider.mobEffect());
            }
        }

        @Override
        public void wornTick(LivingEntity entity, boolean isOnCooldown, boolean isDisabled) {
            if (!isDisabled && !isOnCooldown) {
                if (provider.canApply(entity)) {
                    entity.addEffect(provider.createEffect());
                }
            }
        }

        @Override
        public boolean isNonCosmetic() {
            return provider().isNonCosmetic();
        }

        @Override
        public void addToTooltip(TooltipWriter writer) {
            if (CUSTOM_TOOLTIP_MOB_EFFECTS.contains(provider.mobEffect())) {
                ResourceLocation id = BuiltInRegistries.MOB_EFFECT.getKey(provider.mobEffect().value());
                writer.add(Objects.requireNonNull(id).getPath());
            }
            if (provider.mobEffect().value() == net.minecraft.world.effect.MobEffects.NIGHT_VISION.value()) {
                Value<Double> nightVisionStrength = writer.stack().get(ModDataComponents.REDUCED_NIGHT_VISION.get());
                if (nightVisionStrength != null && nightVisionStrength.get() < 0.5) {
                    writer.add("night_vision.partial");
                } else {
                    writer.add("night_vision.full");
                }
            }
            if (provider.mobEffect().value() == net.minecraft.world.effect.MobEffects.WATER_BREATHING.value()) {
                if (provider.condition() == EntityCondition.ALWAYS) {
                    writer.add("water_breathing.infinite");
                } else {
                    writer.add("water_breathing.limited");
                }
            }
        }
    }
}
