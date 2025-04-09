package artifacts.registry;

import artifacts.Artifacts;
import artifacts.ability.*;
import artifacts.ability.mobeffect.*;
import artifacts.ability.retaliation.SetAttackersOnFireAbility;
import artifacts.ability.retaliation.StrikeAttackersWithLightningAbility;
import artifacts.ability.retaliation.ThornsAbility;
import artifacts.platform.PlatformServices;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;

import java.util.function.Supplier;

import static artifacts.ability.ArtifactAbility.Type;

public class ModAbilities {

    public static final ResourceKey<Registry<Type<?>>> REGISTRY_KEY = ResourceKey.createRegistryKey(Artifacts.id("ability_types"));
    public static final Register<Type<?>> ABILITIES = PlatformServices.platformHelper.createRegister(REGISTRY_KEY);

    public static Registry<Type<?>> getRegistry() {
        // noinspection unchecked
        return (Registry<Type<?>>) BuiltInRegistries.REGISTRY.get(REGISTRY_KEY.location());
    }

    public static final RegistryHolder<Type<?>, Type<ApplyCooldownAfterDamageAbility>> APPLY_COOLDOWN_AFTER_DAMAGE = register("apply_cooldown_after_damage", ApplyCooldownAfterDamageAbility.CODEC, ApplyCooldownAfterDamageAbility.STREAM_CODEC);
    public static final RegistryHolder<Type<?>, Type<ApplyMobEffectAfterDamageAbility>> APPLY_MOB_EFFECT_AFTER_DAMAGE = register("apply_mob_effect_after_damage", ApplyMobEffectAfterDamageAbility.CODEC, ApplyMobEffectAfterDamageAbility.STREAM_CODEC);
    public static final RegistryHolder<Type<?>, Type<ApplyMobEffectAfterEatingAbility>> APPLY_MOB_EFFECT_AFTER_EATING = register("apply_mob_effect_after_eating", ApplyMobEffectAfterEatingAbility.CODEC, ApplyMobEffectAfterEatingAbility.STREAM_CODEC);
    public static final RegistryHolder<Type<?>, Type<AttacksAbsorbDamageAbility>> ATTACKS_ABSORB_DAMAGE = register("attacks_absorb_damage", AttacksAbsorbDamageAbility.CODEC, AttacksAbsorbDamageAbility.STREAM_CODEC);
    public static final RegistryHolder<Type<?>, Type<AttacksInflictMobEffectAbility>> ATTACKS_INFLICT_MOB_EFFECT = register("attacks_inflict_mob_effect", AttacksInflictMobEffectAbility.CODEC, AttacksInflictMobEffectAbility.STREAM_CODEC);
    public static final RegistryHolder<Type<?>, Type<AttractItemsAbility>> ATTRACT_ITEMS = register("attract_items", AttractItemsAbility.CODEC, AttractItemsAbility.STREAM_CODEC);
    public static final RegistryHolder<Type<?>, Type<AttributeModifierAbility>> ATTRIBUTE_MODIFIER = register("attribute_modifier", AttributeModifierAbility.CODEC, AttributeModifierAbility.STREAM_CODEC);
    public static final RegistryHolder<Type<?>, Type<CustomTooltipAbility>> CUSTOM_TOOLTIP = register("custom_tooltip", CustomTooltipAbility.CODEC, CustomTooltipAbility.STREAM_CODEC);
    public static final RegistryHolder<Type<?>, Type<DamageImmunityAbility>> DAMAGE_IMMUNITY = register("damage_immunity", DamageImmunityAbility.CODEC, DamageImmunityAbility.STREAM_CODEC);
    public static final RegistryHolder<Type<?>, Type<DoubleJumpAbility>> DOUBLE_JUMP = register("double_jump", DoubleJumpAbility.CODEC, DoubleJumpAbility.STREAM_CODEC);
    public static final RegistryHolder<Type<?>, Type<EnderPearlsCostHungerAbility>> ENDER_PEARLS_COST_HUNGER = register("ender_pearls_cost_hunger", EnderPearlsCostHungerAbility.CODEC, EnderPearlsCostHungerAbility.STREAM_CODEC);
    public static final RegistryHolder<Type<?>, Type<SimpleAbility>> GROW_PLANTS_AFTER_EATING = register("grow_plants_after_eating", SimpleAbility::createType);
    public static final RegistryHolder<Type<?>, Type<IncreaseEnchantmentLevelAbility>> INCREASE_ENCHANTMENT_LEVEL = register("increase_enchantment_level", IncreaseEnchantmentLevelAbility.CODEC, IncreaseEnchantmentLevelAbility.STREAM_CODEC);
    public static final RegistryHolder<Type<?>, Type<LimitedWaterBreathingAbility>> LIMITED_WATER_BREATHING = register("limited_water_breathing", LimitedWaterBreathingAbility.CODEC, LimitedWaterBreathingAbility.STREAM_CODEC);
    public static final RegistryHolder<Type<?>, Type<MakePiglinsNeutralAbility>> MAKE_PIGLINS_NEUTRAL = register("make_piglins_neutral", MakePiglinsNeutralAbility.CODEC, MakePiglinsNeutralAbility.STREAM_CODEC);
    public static final RegistryHolder<Type<?>, Type<PermanentMobEffectAbility>> MOB_EFFECT = register("mob_effect", PermanentMobEffectAbility.CODEC, PermanentMobEffectAbility.STREAM_CODEC);
    public static final RegistryHolder<Type<?>, Type<ModifyHurtSoundAbility>> MODIFY_HURT_SOUND = register("modify_hurt_sound", ModifyHurtSoundAbility.CODEC, ModifyHurtSoundAbility.STREAM_CODEC);
    public static final RegistryHolder<Type<?>, Type<NightVisionAbility>> NIGHT_VISION = register("night_vision", NightVisionAbility.CODEC, NightVisionAbility.STREAM_CODEC);
    public static final RegistryHolder<Type<?>, Type<SimpleAbility>> NULLIFY_ENDER_PEARL_DAMAGE = register("nullify_ender_pearl_damage", SimpleAbility::createType);
    public static final RegistryHolder<Type<?>, Type<RemoveBadEffectsAbility>> REMOVE_BAD_EFFECTS = register("remove_bad_effects", RemoveBadEffectsAbility.CODEC, RemoveBadEffectsAbility.STREAM_CODEC);
    public static final RegistryHolder<Type<?>, Type<ReplenishHungerOnGrassAbility>> REPLENISH_HUNGER_ON_GRASS = register("replenish_hunger_on_grass", ReplenishHungerOnGrassAbility.CODEC, ReplenishHungerOnGrassAbility.STREAM_CODEC);
    public static final RegistryHolder<Type<?>, Type<SimpleAbility>> SCARE_CREEPERS = register("scare_creepers", SimpleAbility::createType);
    public static final RegistryHolder<Type<?>, Type<SetAttackersOnFireAbility>> SET_ATTACKERS_ON_FIRE = register("set_attackers_on_fire", SetAttackersOnFireAbility.CODEC, SetAttackersOnFireAbility.STREAM_CODEC);
    public static final RegistryHolder<Type<?>, Type<SimpleAbility>> SINKING = register("sinking", SimpleAbility::createType);
    public static final RegistryHolder<Type<?>, Type<SimpleAbility>> SMELT_ORES = register("smelt_ores", SimpleAbility::createType);
    public static final RegistryHolder<Type<?>, Type<CollideWithFluidsAbility>> SNEAK_ON_FLUIDS = register("sneak_on_fluids", CollideWithFluidsAbility::createType);
    public static final RegistryHolder<Type<?>, Type<CollideWithFluidsAbility>> SPRINT_ON_FLUIDS = register("sprint_on_fluids", CollideWithFluidsAbility::createType);
    public static final RegistryHolder<Type<?>, Type<StrikeAttackersWithLightningAbility>> STRIKE_ATTACKERS_WITH_LIGHTNING = register("strike_attackers_with_lightning", StrikeAttackersWithLightningAbility.CODEC, StrikeAttackersWithLightningAbility.STREAM_CODEC);
    public static final RegistryHolder<Type<?>, Type<SwimInAirAbility>> SWIM_IN_AIR = register("swim_in_air", SwimInAirAbility.CODEC, SwimInAirAbility.STREAM_CODEC);
    public static final RegistryHolder<Type<?>, Type<TeleportOnDeathAbility>> TELEPORT_ON_DEATH = register("teleport_on_death", TeleportOnDeathAbility.CODEC, TeleportOnDeathAbility.STREAM_CODEC);
    public static final RegistryHolder<Type<?>, Type<ThornsAbility>> THORNS = register("thorns", ThornsAbility.CODEC, ThornsAbility.STREAM_CODEC);
    public static final RegistryHolder<Type<?>, Type<UpgradeToolTierAbility>> UPGRADE_TOOL_TIER = register("upgrade_tool_tier", UpgradeToolTierAbility.CODEC, UpgradeToolTierAbility.STREAM_CODEC);
    public static final RegistryHolder<Type<?>, Type<SimpleAbility>> WALK_ON_POWDER_SNOW = register("walk_on_powdered_snow", SimpleAbility::createType);

    public static <T extends ArtifactAbility> RegistryHolder<Type<?>, Type<T>> register(String name, MapCodec<T> codec, StreamCodec<? super RegistryFriendlyByteBuf, T> streamCodec) {
        return register(name, () -> new Type<>(codec, streamCodec));
    }

    public static <T extends Type<?>> RegistryHolder<Type<?>, T> register(String name, Supplier<T> type) {
        return ABILITIES.register(name, type);
    }
}
