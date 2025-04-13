package artifacts.registry;

import artifacts.component.HurtSound;
import artifacts.component.ToggleIdentifier;
import artifacts.component.ability.*;
import artifacts.component.ability.mobeffect.*;
import artifacts.component.ability.retaliation.SetAttackersOnFireAbility;
import artifacts.component.ability.retaliation.StrikeAttackersWithLightningAbility;
import artifacts.component.ability.retaliation.ThornsAbility;
import artifacts.platform.PlatformServices;
import com.mojang.serialization.Codec;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Unit;
import net.minecraft.world.item.component.ItemLore;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

public class ModDataComponents {

    public static final Register<DataComponentType<?>> DATA_COMPONENT_TYPES = PlatformServices.platformHelper.createRegister(Registries.DATA_COMPONENT_TYPE);

    public static final Set<Supplier<? extends DataComponentType<? extends TickingAbility>>> TICKING_COMPONENTS = new LinkedHashSet<>();
    public static final List<Supplier<? extends DataComponentType<? extends EquipmentAbility>>> TOOLTIP_ORDER = new ArrayList<>();
    public static final Set<Supplier<? extends DataComponentType<?>>> APPLIES_COOLDOWN = new LinkedHashSet<>();

    public static final Supplier<DataComponentType<ToggleIdentifier>> TOGGLE_KEY = registerSynced("toggle_key", ToggleIdentifier.CODEC, ToggleIdentifier.STREAM_CODEC);
    public static final Supplier<DataComponentType<Unit>> DISABLED_BY_TOGGLE = registerSynced("disabled_by_toggle", Codec.unit(Unit.INSTANCE), StreamCodec.unit(Unit.INSTANCE));
    public static final Supplier<DataComponentType<SoundEvent>> EQUIP_SOUND = registerSynced("equip_sound",
            ResourceLocation.CODEC.xmap(SoundEvent::createVariableRangeEvent, SoundEvent::getLocation),
            ResourceLocation.STREAM_CODEC.map(SoundEvent::createVariableRangeEvent, SoundEvent::getLocation)
    );
    public static final Supplier<DataComponentType<ItemLore>> ABILITY_LORE = registerCached("ability_lore", ItemLore.CODEC, ItemLore.STREAM_CODEC);

    public static final Supplier<DataComponentType<ApplyCooldownAfterDamageAbility>> APPLY_COOLDOWN_AFTER_DAMAGE =
            registerSynced("apply_cooldown_after_damage", ApplyCooldownAfterDamageAbility.CODEC, ApplyCooldownAfterDamageAbility.STREAM_CODEC);
    public static final Supplier<DataComponentType<ApplyMobEffectAfterDamageAbility>> APPLY_MOB_EFFECT_AFTER_DAMAGE =
            registerSynced("apply_mob_effect_after_damage", ApplyMobEffectAfterDamageAbility.CODEC, ApplyMobEffectAfterDamageAbility.STREAM_CODEC);
    public static final Supplier<DataComponentType<ApplyMobEffectAfterEatingAbility>> APPLY_MOB_EFFECT_AFTER_EATING =
            registerSynced("apply_mob_effect_after_eating", ApplyMobEffectAfterEatingAbility.CODEC, ApplyMobEffectAfterEatingAbility.STREAM_CODEC);
    public static final Supplier<DataComponentType<AttacksAbsorbDamageAbility>> ATTACKS_ABSORB_DAMAGE =
            registerSynced("attacks_absorb_damage", AttacksAbsorbDamageAbility.CODEC, AttacksAbsorbDamageAbility.STREAM_CODEC);
    public static final Supplier<DataComponentType<AttacksInflictMobEffectAbility>> ATTACKS_INFLICT_MOB_EFFECT =
            registerSynced("attacks_inflict_mob_effect", AttacksInflictMobEffectAbility.CODEC, AttacksInflictMobEffectAbility.STREAM_CODEC);
    public static final Supplier<DataComponentType<AttributeModifierAbility>> ATTRIBUTE_MODIFIER =
             registerCached("attribute_modifier", AttributeModifierAbility.CODEC, AttributeModifierAbility.STREAM_CODEC);
    public static final Supplier<DataComponentType<DamageImmunityAbility>> DAMAGE_IMMUNITY =
            registerSynced("damage_immunity", DamageImmunityAbility.CODEC, DamageImmunityAbility.STREAM_CODEC);
    public static final Supplier<DataComponentType<DoubleJumpAbility>> DOUBLE_JUMP =
            registerSynced("double_jump", DoubleJumpAbility.CODEC, DoubleJumpAbility.STREAM_CODEC);
    public static final Supplier<DataComponentType<EnderPearlsCostHungerAbility>> ENDER_PEARLS_COST_HUNGER =
            registerSynced("ender_pearls_cost_hunger", EnderPearlsCostHungerAbility.CODEC, EnderPearlsCostHungerAbility.STREAM_CODEC);
    public static final Supplier<DataComponentType<SimpleAbility>> GROW_PLANTS_AFTER_EATING =
            registerSimpleAbility("grow_plants_after_eating");
    public static final Supplier<DataComponentType<IncreaseEnchantmentLevelAbility>> INCREASE_ENCHANTMENT_LEVEL =
            registerSynced("increase_enchantment_level", IncreaseEnchantmentLevelAbility.CODEC, IncreaseEnchantmentLevelAbility.STREAM_CODEC);
    public static final Supplier<DataComponentType<LimitedWaterBreathingAbility>> LIMITED_WATER_BREATHING =
            registerSynced("limited_water_breathing", LimitedWaterBreathingAbility.CODEC, LimitedWaterBreathingAbility.STREAM_CODEC);
    public static final Supplier<DataComponentType<Unit>> MAKE_PIGLINS_NEUTRAL =
            registerSynced("make_piglins_neutral", Unit.CODEC, StreamCodec.unit(Unit.INSTANCE));
    public static final Supplier<DataComponentType<PermanentMobEffectAbility>> MOB_EFFECT =
            registerSynced("mob_effect", PermanentMobEffectAbility.CODEC, PermanentMobEffectAbility.STREAM_CODEC);
    public static final Supplier<DataComponentType<HurtSound>> MODIFY_HURT_SOUND =
            registerSynced("modify_hurt_sound", HurtSound.CODEC, HurtSound.STREAM_CODEC);
    public static final Supplier<DataComponentType<NightVisionAbility>> NIGHT_VISION =
            registerSynced("night_vision", NightVisionAbility.CODEC, NightVisionAbility.STREAM_CODEC);
    public static final Supplier<DataComponentType<SimpleAbility>> NULLIFY_ENDER_PEARL_DAMAGE =
            registerSimpleAbility("nullify_ender_pearl_damage");
    public static final Supplier<DataComponentType<RemoveBadEffectsAbility>> REMOVE_BAD_EFFECTS =
            registerSynced("remove_bad_effects", RemoveBadEffectsAbility.CODEC, RemoveBadEffectsAbility.STREAM_CODEC);
    public static final Supplier<DataComponentType<ReplenishHungerOnGrassAbility>> REPLENISH_HUNGER_ON_GRASS =
            registerSynced("replenish_hunger_on_grass", ReplenishHungerOnGrassAbility.CODEC, ReplenishHungerOnGrassAbility.STREAM_CODEC);
    public static final Supplier<DataComponentType<SimpleAbility>> SCARE_CREEPERS =
            registerSimpleAbility("scare_creepers");
    public static final Supplier<DataComponentType<SetAttackersOnFireAbility>> SET_ATTACKERS_ON_FIRE =
            registerSynced("set_attackers_on_fire", SetAttackersOnFireAbility.CODEC, SetAttackersOnFireAbility.STREAM_CODEC);
    public static final Supplier<DataComponentType<SimpleAbility>> SINKING =
            registerSimpleAbility("sinking");
    public static final Supplier<DataComponentType<SimpleAbility>> SMELT_ORES =
            registerSimpleAbility("smelt_ores");
    public static final Supplier<DataComponentType<CollideWithFluidsAbility>> COLLIDE_WITH_FLUIDS =
            registerSynced("collide_with_fluids", CollideWithFluidsAbility.CODEC, CollideWithFluidsAbility.STREAM_CODEC);
    public static final Supplier<DataComponentType<StrikeAttackersWithLightningAbility>> STRIKE_ATTACKERS_WITH_LIGHTNING =
            registerSynced("strike_attackers_with_lightning", StrikeAttackersWithLightningAbility.CODEC, StrikeAttackersWithLightningAbility.STREAM_CODEC);
    public static final Supplier<DataComponentType<SwimInAirAbility>> SWIM_IN_AIR =
            registerSynced("swim_in_air", SwimInAirAbility.CODEC, SwimInAirAbility.STREAM_CODEC);
    public static final Supplier<DataComponentType<TeleportOnDeathAbility>> TELEPORT_ON_DEATH =
            registerSynced("teleport_on_death", TeleportOnDeathAbility.CODEC, TeleportOnDeathAbility.STREAM_CODEC);
    public static final Supplier<DataComponentType<ThornsAbility>> THORNS =
            registerSynced("thorns", ThornsAbility.CODEC, ThornsAbility.STREAM_CODEC);
    public static final Supplier<DataComponentType<UpgradeToolTierAbility>> UPGRADE_TOOL_TIER =
            registerSynced("upgrade_tool_tier", UpgradeToolTierAbility.CODEC, UpgradeToolTierAbility.STREAM_CODEC);
    public static final Supplier<DataComponentType<SimpleAbility>> WALK_ON_POWDER_SNOW =
            registerSimpleAbility("walk_on_powdered_snow");

    static {
        TICKING_COMPONENTS.addAll(List.of(
                ATTRIBUTE_MODIFIER,
                REPLENISH_HUNGER_ON_GRASS,
                REMOVE_BAD_EFFECTS,
                MOB_EFFECT,
                LIMITED_WATER_BREATHING,
                NIGHT_VISION
        ));
        TOOLTIP_ORDER.addAll(List.of(
                APPLY_MOB_EFFECT_AFTER_DAMAGE,
                APPLY_MOB_EFFECT_AFTER_EATING,
                ATTACKS_ABSORB_DAMAGE,
                ATTACKS_INFLICT_MOB_EFFECT,
                ATTRIBUTE_MODIFIER,
                DAMAGE_IMMUNITY,
                DOUBLE_JUMP,
                ENDER_PEARLS_COST_HUNGER,
                GROW_PLANTS_AFTER_EATING,
                INCREASE_ENCHANTMENT_LEVEL,
                LIMITED_WATER_BREATHING,
                MOB_EFFECT,
                NIGHT_VISION,
                NULLIFY_ENDER_PEARL_DAMAGE,
                REMOVE_BAD_EFFECTS,
                REPLENISH_HUNGER_ON_GRASS,
                SCARE_CREEPERS,
                SET_ATTACKERS_ON_FIRE,
                SINKING,
                SMELT_ORES,
                COLLIDE_WITH_FLUIDS,
                STRIKE_ATTACKERS_WITH_LIGHTNING,
                SWIM_IN_AIR,
                TELEPORT_ON_DEATH,
                THORNS,
                UPGRADE_TOOL_TIER,
                WALK_ON_POWDER_SNOW
        ));
        APPLIES_COOLDOWN.addAll(Set.of(
                APPLY_COOLDOWN_AFTER_DAMAGE,
                STRIKE_ATTACKERS_WITH_LIGHTNING,
                THORNS,
                SET_ATTACKERS_ON_FIRE,
                TELEPORT_ON_DEATH
        ));
    }

    private static Supplier<DataComponentType<SimpleAbility>> registerSimpleAbility(String name) {
        return registerSynced(name, SimpleAbility.CODEC, SimpleAbility.STREAM_CODEC);
    }

    private static <T> Supplier<DataComponentType<T>> registerSynced(String name, Codec<T> codec, StreamCodec<? super RegistryFriendlyByteBuf, T> streamCodec) {
        return register(name, builder -> builder.persistent(codec).networkSynchronized(streamCodec));
    }

    private static <T> Supplier<DataComponentType<T>> registerCached(String name, Codec<T> codec, StreamCodec<? super RegistryFriendlyByteBuf, T> streamCodec) {
        return register(name, builder -> builder.persistent(codec).networkSynchronized(streamCodec).cacheEncoding());
    }

    private static <T> Supplier<DataComponentType<T>> register(String name, UnaryOperator<DataComponentType.Builder<T>> builder) {
        return DATA_COMPONENT_TYPES.register(name, () -> builder.apply(DataComponentType.builder()).build());
    }
}
