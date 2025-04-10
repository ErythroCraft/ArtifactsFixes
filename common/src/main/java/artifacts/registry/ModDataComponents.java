package artifacts.registry;

import artifacts.Artifacts;
import artifacts.ability.*;
import artifacts.ability.mobeffect.*;
import artifacts.ability.retaliation.SetAttackersOnFireAbility;
import artifacts.ability.retaliation.StrikeAttackersWithLightningAbility;
import artifacts.ability.retaliation.ThornsAbility;
import artifacts.platform.PlatformServices;
import com.mojang.serialization.Codec;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;

import java.util.function.Supplier;
import java.util.function.UnaryOperator;

public class ModDataComponents {

    public static final Register<DataComponentType<?>> DATA_COMPONENT_TYPES = PlatformServices.platformHelper.createRegister(Registries.DATA_COMPONENT_TYPE);

    public static final Supplier<DataComponentType<ApplyCooldownAfterDamageAbility>> APPLY_COOLDOWN_AFTER_DAMAGE =
            registerAbility("apply_cooldown_after_damage", ApplyCooldownAfterDamageAbility.CODEC, ApplyCooldownAfterDamageAbility.STREAM_CODEC);
    public static final Supplier<DataComponentType<ApplyMobEffectAfterDamageAbility>> APPLY_MOB_EFFECT_AFTER_DAMAGE =
            registerAbility("apply_mob_effect_after_damage", ApplyMobEffectAfterDamageAbility.CODEC, ApplyMobEffectAfterDamageAbility.STREAM_CODEC);
    public static final Supplier<DataComponentType<ApplyMobEffectAfterEatingAbility>> APPLY_MOB_EFFECT_AFTER_EATING =
            registerAbility("apply_mob_effect_after_eating", ApplyMobEffectAfterEatingAbility.CODEC, ApplyMobEffectAfterEatingAbility.STREAM_CODEC);
    public static final Supplier<DataComponentType<AttacksAbsorbDamageAbility>> ATTACKS_ABSORB_DAMAGE =
            registerAbility("attacks_absorb_damage", AttacksAbsorbDamageAbility.CODEC, AttacksAbsorbDamageAbility.STREAM_CODEC);
    public static final Supplier<DataComponentType<AttacksInflictMobEffectAbility>> ATTACKS_INFLICT_MOB_EFFECT =
            registerAbility("attacks_inflict_mob_effect", AttacksInflictMobEffectAbility.CODEC, AttacksInflictMobEffectAbility.STREAM_CODEC);
    public static final Supplier<DataComponentType<AttractItemsAbility>> ATTRACT_ITEMS =
            registerAbility("attract_items", AttractItemsAbility.CODEC, AttractItemsAbility.STREAM_CODEC);
    public static final Supplier<DataComponentType<AttributeModifierAbility>> ATTRIBUTE_MODIFIER =
            registerAbility("attribute_modifier", AttributeModifierAbility.CODEC, AttributeModifierAbility.STREAM_CODEC);
    public static final Supplier<DataComponentType<CustomTooltipAbility>> CUSTOM_TOOLTIP =
            registerAbility("custom_tooltip", CustomTooltipAbility.CODEC, CustomTooltipAbility.STREAM_CODEC);
    public static final Supplier<DataComponentType<DamageImmunityAbility>> DAMAGE_IMMUNITY =
            registerAbility("damage_immunity", DamageImmunityAbility.CODEC, DamageImmunityAbility.STREAM_CODEC);
    public static final Supplier<DataComponentType<DoubleJumpAbility>> DOUBLE_JUMP =
            registerAbility("double_jump", DoubleJumpAbility.CODEC, DoubleJumpAbility.STREAM_CODEC);
    public static final Supplier<DataComponentType<EnderPearlsCostHungerAbility>> ENDER_PEARLS_COST_HUNGER =
            registerAbility("ender_pearls_cost_hunger", EnderPearlsCostHungerAbility.CODEC, EnderPearlsCostHungerAbility.STREAM_CODEC);
    public static final Supplier<DataComponentType<SimpleAbility>> GROW_PLANTS_AFTER_EATING =
            registerSimpleAbility("grow_plants_after_eating");
    public static final Supplier<DataComponentType<IncreaseEnchantmentLevelAbility>> INCREASE_ENCHANTMENT_LEVEL =
            registerAbility("increase_enchantment_level", IncreaseEnchantmentLevelAbility.CODEC, IncreaseEnchantmentLevelAbility.STREAM_CODEC);
    public static final Supplier<DataComponentType<LimitedWaterBreathingAbility>> LIMITED_WATER_BREATHING =
            registerAbility("limited_water_breathing", LimitedWaterBreathingAbility.CODEC, LimitedWaterBreathingAbility.STREAM_CODEC);
    public static final Supplier<DataComponentType<MakePiglinsNeutralAbility>> MAKE_PIGLINS_NEUTRAL =
            registerAbility("make_piglins_neutral", MakePiglinsNeutralAbility.CODEC, MakePiglinsNeutralAbility.STREAM_CODEC);
    public static final Supplier<DataComponentType<PermanentMobEffectAbility>> MOB_EFFECT =
            registerAbility("mob_effect", PermanentMobEffectAbility.CODEC, PermanentMobEffectAbility.STREAM_CODEC);
    public static final Supplier<DataComponentType<ModifyHurtSoundAbility>> MODIFY_HURT_SOUND =
            registerAbility("modify_hurt_sound", ModifyHurtSoundAbility.CODEC, ModifyHurtSoundAbility.STREAM_CODEC);
    public static final Supplier<DataComponentType<NightVisionAbility>> NIGHT_VISION =
            registerAbility("night_vision", NightVisionAbility.CODEC, NightVisionAbility.STREAM_CODEC);
    public static final Supplier<DataComponentType<SimpleAbility>> NULLIFY_ENDER_PEARL_DAMAGE =
            registerSimpleAbility("nullify_ender_pearl_damage");
    public static final Supplier<DataComponentType<RemoveBadEffectsAbility>> REMOVE_BAD_EFFECTS =
            registerAbility("remove_bad_effects", RemoveBadEffectsAbility.CODEC, RemoveBadEffectsAbility.STREAM_CODEC);
    public static final Supplier<DataComponentType<ReplenishHungerOnGrassAbility>> REPLENISH_HUNGER_ON_GRASS =
            registerAbility("replenish_hunger_on_grass", ReplenishHungerOnGrassAbility.CODEC, ReplenishHungerOnGrassAbility.STREAM_CODEC);
    public static final Supplier<DataComponentType<SimpleAbility>> SCARE_CREEPERS =
            registerSimpleAbility("scare_creepers");
    public static final Supplier<DataComponentType<SetAttackersOnFireAbility>> SET_ATTACKERS_ON_FIRE =
            registerAbility("set_attackers_on_fire", SetAttackersOnFireAbility.CODEC, SetAttackersOnFireAbility.STREAM_CODEC);
    public static final Supplier<DataComponentType<SimpleAbility>> SINKING =
            registerSimpleAbility("sinking");
    public static final Supplier<DataComponentType<SimpleAbility>> SMELT_ORES =
            registerSimpleAbility("smelt_ores");
    public static final Supplier<DataComponentType<CollideWithFluidsAbility>> SNEAK_ON_FLUIDS =
            registerAbility("sneak_on_fluids",
                    CollideWithFluidsAbility.codec(tooltip("sneak_on_fluids", "lava")),
                    CollideWithFluidsAbility.streamCodec(tooltip("sneak_on_fluids", "lava"))
            );
    public static final Supplier<DataComponentType<CollideWithFluidsAbility>> SPRINT_ON_FLUIDS =
            registerAbility("sprint_on_fluids", CollideWithFluidsAbility.codec(null), CollideWithFluidsAbility.streamCodec(null));
    public static final Supplier<DataComponentType<StrikeAttackersWithLightningAbility>> STRIKE_ATTACKERS_WITH_LIGHTNING =
            registerAbility("strike_attackers_with_lightning", StrikeAttackersWithLightningAbility.CODEC, StrikeAttackersWithLightningAbility.STREAM_CODEC);
    public static final Supplier<DataComponentType<SwimInAirAbility>> SWIM_IN_AIR =
            registerAbility("swim_in_air", SwimInAirAbility.CODEC, SwimInAirAbility.STREAM_CODEC);
    public static final Supplier<DataComponentType<TeleportOnDeathAbility>> TELEPORT_ON_DEATH =
            registerAbility("teleport_on_death", TeleportOnDeathAbility.CODEC, TeleportOnDeathAbility.STREAM_CODEC);
    public static final Supplier<DataComponentType<ThornsAbility>> THORNS =
            registerAbility("thorns", ThornsAbility.CODEC, ThornsAbility.STREAM_CODEC);
    public static final Supplier<DataComponentType<UpgradeToolTierAbility>> UPGRADE_TOOL_TIER =
            registerAbility("upgrade_tool_tier", UpgradeToolTierAbility.CODEC, UpgradeToolTierAbility.STREAM_CODEC);
    public static final Supplier<DataComponentType<SimpleAbility>> WALK_ON_POWDER_SNOW =
            registerSimpleAbility("walk_on_powdered_snow");


    private static Component tooltip(String ability, String name) {
        return Component.translatable("%s.tooltip.ability.%s.%s".formatted(Artifacts.MOD_ID, ability, name));
    }

    private static Supplier<DataComponentType<SimpleAbility>> registerSimpleAbility(String name) {
        return registerAbility(name, SimpleAbility.CODEC, SimpleAbility.STREAM_CODEC);
    }

    private static <T extends ArtifactAbility> Supplier<DataComponentType<T>> registerAbility(String name, Codec<T> codec, StreamCodec<? super RegistryFriendlyByteBuf, T> streamCodec) {
        return register(name, builder -> builder.persistent(codec).networkSynchronized(streamCodec).cacheEncoding());
    }

    private static <T> Supplier<DataComponentType<T>> register(String name, UnaryOperator<DataComponentType.Builder<T>> builder) {
        return DATA_COMPONENT_TYPES.register(name, () -> builder.apply(DataComponentType.builder()).build());
    }
}
