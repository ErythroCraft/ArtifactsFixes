package artifacts.registry;

import artifacts.component.DamageOnHurt;
import artifacts.component.Equipable;
import artifacts.component.HurtSound;
import artifacts.component.ToggleIdentifier;
import artifacts.component.ability.*;
import artifacts.component.ability.mobeffect.AttackEffect;
import artifacts.component.ability.mobeffect.EquipmentMobEffect;
import artifacts.component.ability.mobeffect.PostDamageEffect;
import artifacts.component.ability.mobeffect.PostEatingEffect;
import artifacts.component.ability.retaliation.RetaliationEffects;
import artifacts.config.value.Value;
import artifacts.config.value.ValueTypes;
import com.mojang.serialization.Codec;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.Unit;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemLore;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

public class ModDataComponents {

    public static final Register<DataComponentType<?>> DATA_COMPONENT_TYPES = Register.create(Registries.DATA_COMPONENT_TYPE);

    public static final Set<TickingAbility<?>> TICKING_ABILITIES = new LinkedHashSet<>();
    private static final Set<Supplier<? extends DataComponentType<?>>> APPLIES_COOLDOWN = new LinkedHashSet<>();

    /** Allows the items abilities to be toggled on and off */
    public static final Supplier<DataComponentType<ToggleIdentifier>> TOGGLE_KEY =
            registerSynced("toggle_key", ToggleIdentifier.CODEC, ToggleIdentifier.STREAM_CODEC);
    /** Marker used by the toggle_key component */
    public static final Supplier<DataComponentType<Unit>> DISABLED_BY_TOGGLE =
            registerSynced("disabled_by_toggle", Unit.CODEC, Unit.STREAM_CODEC);
    /** Adds the missing dependency tooltip when none of Curios, Trinkets, or Accessories is installed */
    public static final Supplier<DataComponentType<Unit>> DEPENDENCY_CHECK_TOOLTIP =
            registerSynced("dependency_check_tooltip", Unit.CODEC, Unit.STREAM_CODEC);
    /** Adds the *Cosmetic* tooltip when the item has no (enabled) abilities */
    public static final Supplier<DataComponentType<Unit>> COSMETIC_TOOLTIP =
            registerSynced("cosmetic_tooltip", Unit.CODEC, Unit.STREAM_CODEC);
    // TODO: probably should rename this to `equippable` to match vanilla
    /** Allows the item to be equipped from use */
    public static final Supplier<DataComponentType<Equipable>> EQUIPABLE =
            registerSynced("equipable", Equipable.CODEC, Equipable.STREAM_CODEC);
    /** Item lore tooltip that displays above other ability tooltips */
    public static final Supplier<DataComponentType<ItemLore>> ABILITY_LORE =
            registerCached("ability_lore", ItemLore.CODEC, ItemLore.STREAM_CODEC);
    // TODO: do I still need this?
    /** Pacifies piglins when the item is worn (a tag might be better for this) */
    public static final Supplier<DataComponentType<Unit>> PIGLIN_LOVED =
            registerSynced("piglin_loved", Unit.CODEC, StreamCodec.unit(Unit.INSTANCE));
    /** Sound that plays when hurt while wearing the item */
    public static final Supplier<DataComponentType<HurtSound>> HURT_SOUND =
            registerSynced("hurt_sound", HurtSound.CODEC, HurtSound.STREAM_CODEC);
    /** Decreases the brightness of the night vision effect while worn */
    public static final Supplier<DataComponentType<Value<Double>>> REDUCED_NIGHT_VISION =
            registerSynced("reduced_night_vision", ValueTypes.FRACTION.codec(), ValueTypes.FRACTION.streamCodec());
    /** Hides the item's model when the entity wearing it is invisible */
    public static final Supplier<DataComponentType<Value<Boolean>>> HIDE_WHEN_INVISIBLE =
            registerSynced("hide_when_invisible", ValueTypes.enabledField().codec(), ValueTypes.BOOLEAN.streamCodec());
    /** Prevents the item from being destroyed when durability reaches 0 */
    public static final Supplier<DataComponentType<Value<Boolean>>> INDESTRUCTIBLE =
            registerSynced("indestructible", ValueTypes.enabledField().codec(), ValueTypes.BOOLEAN.streamCodec());
    // TODO: consider merging these into a single component
    /** Damage applied to the item when hurt, while worn and not on cooldown */
    public static final Supplier<DataComponentType<DamageOnHurt>> DAMAGE_ON_HURT =
            registerSynced("damage_on_hurt", DamageOnHurt.CODEC, DamageOnHurt.STREAM_CODEC);
    /** Damage applied to the item when melee attacking an entity, while worn and not on cooldown */
    public static final Supplier<DataComponentType<Value<Integer>>> DAMAGE_ON_ATTACK =
            registerSynced("damage_on_attack", ValueTypes.NON_NEGATIVE_INT.codec(), ValueTypes.NON_NEGATIVE_INT.streamCodec());
    /** Damage applied to the item when farting, while worn and not on cooldown */
    public static final Supplier<DataComponentType<Value<Integer>>> DAMAGE_ON_FART =
            registerSynced("damage_on_fart", ValueTypes.NON_NEGATIVE_INT.codec(), ValueTypes.NON_NEGATIVE_INT.streamCodec());
    /** Damage applied to the item after mining an ore block, while worn and not on cooldown */
    public static final Supplier<DataComponentType<Value<Integer>>> DAMAGE_ON_ORE_MINED =
            registerSynced("damage_on_ore_mined", ValueTypes.NON_NEGATIVE_INT.codec(), ValueTypes.NON_NEGATIVE_INT.streamCodec());

    // Abilities (= components that prevent the 'cosmetic' tooltip from showing up when EquipmentAbility#isNonCosmetic returns true)
    /** Cooldown applied to the item after receiving damage, possibly filtered by damage type tag */
    public static final Supplier<DataComponentType<PostDamageCooldown>> POST_DAMAGE_COOLDOWN =
            registerSynced("post_damage_cooldown", PostDamageCooldown.CODEC, PostDamageCooldown.STREAM_CODEC);
    /** Effects applied to the entity with the ability after receiving damage, possibly filtered by damage type tag */
    public static final Supplier<DataComponentType<CompositeAbility<PostDamageEffect>>> POST_DAMAGE_EFFECTS =
            registerComposite("post_damage_effects", PostDamageEffect.CODEC, PostDamageEffect.STREAM_CODEC);
    /** Effects applied to the player with the ability after restoring food points */
    public static final Supplier<DataComponentType<CompositeAbility<PostEatingEffect>>> POST_EATING_EFFECTS =
            registerComposite("post_eating_effects", PostEatingEffect.CODEC, PostEatingEffect.STREAM_CODEC);
    /** Damage absorbed from melee attacks */
    public static final Supplier<DataComponentType<DamageAbsorption>> DAMAGE_ABSORPTION =
            registerSynced("damage_absorption", DamageAbsorption.CODEC, DamageAbsorption.STREAM_CODEC);
    /** Effects applied to the attacked entity after an entity with this ability performs a melee attack */
    public static final Supplier<DataComponentType<CompositeAbility<AttackEffect>>> ATTACK_EFFECTS =
            registerComposite("attack_effects", AttackEffect.CODEC, AttackEffect.STREAM_CODEC);
    /** Attribute modifiers applied to the entity with the ability */
    public static final Supplier<DataComponentType<CompositeAbility<EquipmentAttributeModifier>>> ATTRIBUTE_MODIFIERS =
            registerComposite("attribute_modifiers", EquipmentAttributeModifier.CODEC, EquipmentAttributeModifier.STREAM_CODEC);
    /** Damage types the entity with the ability is immune to */
    public static final Supplier<DataComponentType<DamageImmunity>> DAMAGE_IMMUNITY =
            registerSynced("damage_immunity", DamageImmunity.CODEC, DamageImmunity.STREAM_CODEC);
    /** Allows the entity with the ability to double jump */
    public static final Supplier<DataComponentType<DoubleJump>> DOUBLE_JUMP =
            registerSynced("double_jump", DoubleJump.CODEC, DoubleJump.STREAM_CODEC);
    /** Allows the entity with the ability to throw ender pearls for free, consuming hunger and/or durability instead of the pearl */
    public static final Supplier<DataComponentType<EnderPearlHungerCost>> ENDER_PEARL_HUNGER_COST =
            registerSynced("ender_pearl_hunger_cost", EnderPearlHungerCost.CODEC, EnderPearlHungerCost.STREAM_CODEC);
    /** Grow plants under the entity with the ability after eating while standing on a block in the #rooted_boots_grass tag */
    public static final Supplier<DataComponentType<SimpleAbility>> POST_EATING_PLANT_GROWTH =
            registerSimpleAbility("post_eating_plant_growth");
    /** Increase the level of specific enchantments on the entity with the ability */
    public static final Supplier<DataComponentType<CompositeAbility<EnchantmentLevelModifier>>> ENCHANTMENT_LEVEL_MODIFIERS =
            registerComposite("enchantment_level_modifiers", EnchantmentLevelModifier.CODEC, EnchantmentLevelModifier.STREAM_CODEC);
    /** Apply mob effects to the entity with the ability, optionally under specific conditions */
    public static final Supplier<DataComponentType<CompositeAbility<EquipmentMobEffect>>> MOB_EFFECTS =
            registerComposite("mob_effects", EquipmentMobEffect.CODEC, EquipmentMobEffect.STREAM_CODEC);
    /** Prevents the entity with the ability from taking damage from ender pearls */
    public static final Supplier<DataComponentType<SimpleAbility>> ENDER_PEARL_DAMAGE_IMMUNITY =
            registerSimpleAbility("ender_pearl_damage_immunity");
    /** Sets a maximum duration on negative effects applied to the entity with the ability */
    public static final Supplier<DataComponentType<CureEffects>> CURE_EFFECTS =
            registerSynced("cure_effects", CureEffects.CODEC, CureEffects.STREAM_CODEC);
    /** Restores the entity's food points while standing on blocks in the #rooted_boots_grass tag */
    public static final Supplier<DataComponentType<ReplenishHungerOnGrass>> REPLENISH_HUNGER_ON_GRASS =
            registerSynced("replenish_hunger_on_grass", ReplenishHungerOnGrass.CODEC, ReplenishHungerOnGrass.STREAM_CODEC);
    /** Prevents creepers from targeting and attacking the entity with the ability */
    public static final Supplier<DataComponentType<SimpleAbility>> CREEPER_REPELLENT =
            registerSimpleAbility("creeper_repellent");
    /** Repels phantoms targeting the entity with the ability when they get to close */
    public static final Supplier<DataComponentType<SimpleAbility>> PHANTOM_REPELLENT =
            registerSimpleAbility("phantom_repellent");
    /** Removes collisions with fluids for the entity with the ability */
    public static final Supplier<DataComponentType<SimpleAbility>> SINKING =
            registerSimpleAbility("sinking");
    /** Smelt ores mined by the entity with the ability */
    public static final Supplier<DataComponentType<SimpleAbility>> AUTO_SMELT =
            registerSimpleAbility("auto_smelt");
    /** Enables collisions with fluids for the entity with the ability, possibly filtered by condition or fluid type tag */
    public static final Supplier<DataComponentType<FluidCollision>> FLUID_COLLISION =
            registerSynced("fluid_collision", FluidCollision.CODEC, FluidCollision.STREAM_CODEC);
    /** Enables swim-flying for the entity with the ability */
    public static final Supplier<DataComponentType<SwimInAir>> SWIM_IN_AIR =
            registerSynced("swim_in_air", SwimInAir.CODEC, SwimInAir.STREAM_CODEC);
    // TODO: probably should rename this to `equippable_totem` to match vanilla
    /**
     * Adds the chorus totem tooltip when not marked as cosmetic, and allows the death_protection component to apply when in an equipment slot.
     * The vanilla death_protection should be used for the actual protection effects.
     */
    public static final Supplier<DataComponentType<EquipableTotem>> EQUIPABLE_TOTEM =
            registerSynced("equipable_totem", EquipableTotem.CODEC, EquipableTotem.STREAM_CODEC);
    /** Effects applied to attacking entities when the entity with the ability is attacked */
    public static final Supplier<DataComponentType<RetaliationEffects>> RETALIATION_EFFECTS =
            registerSynced("retaliation_effects", RetaliationEffects.CODEC, RetaliationEffects.STREAM_CODEC);
    /** Allows the entity with the ability to mine blocks without a tool up to a specific tool tier */
    public static final Supplier<DataComponentType<ToolTierUpgrade>> TOOL_TIER_UPGRADE =
            registerSynced("tool_tier_upgrade", ToolTierUpgrade.CODEC, ToolTierUpgrade.STREAM_CODEC);
    /** Allows the entity with the ability to walk on powder snow */
    public static final Supplier<DataComponentType<SimpleAbility>> WALK_ON_POWDER_SNOW =
            registerSimpleAbility("walk_on_powder_snow");
    /**
     * Adds the 'Can be used as a shield' tooltip, and prevents the 'cosmetic' tooltip from showing up when enabled.
     * The vanilla blocks_attacks component should be used for the actual blocking logic.
     */
    public static final Supplier<DataComponentType<SimpleAbility>> BLOCKS_ATTACKS =
            registerSimpleAbility("blocks_attacks");
    /**
     * Prevents fall damage and slows the fall of entities holding the item.
     * When this component is present but not enabled, the item still behaves like an umbrella visually.
     */
    public static final Supplier<DataComponentType<SimpleAbility>> HANDHELD_GLIDER =
            registerSimpleAbility("handheld_glider");
    /**
     * Prevents the item from being consumed when eaten
     */
    public static final Supplier<DataComponentType<SimpleAbility>> INFINITE_CONSUMABLE =
            registerSimpleAbility("infinite_consumable");

    static {
        TICKING_ABILITIES.addAll(List.of(
                new TickingAbility<>(ATTRIBUTE_MODIFIERS, new CompositeAbility.Ticker<>(new EquipmentAttributeModifier.Ticker())),
                new TickingAbility<>(REPLENISH_HUNGER_ON_GRASS, new ReplenishHungerOnGrass.Ticker()),
                new TickingAbility<>(CURE_EFFECTS, new CureEffects.Ticker()),
                new TickingAbility<>(MOB_EFFECTS, new CompositeAbility.Ticker<>(new EquipmentMobEffect.Ticker())),
                new TickingAbility<>(FLUID_COLLISION, new FluidCollision.Ticker())
        ));
        APPLIES_COOLDOWN.addAll(Set.of(
                POST_DAMAGE_COOLDOWN,
                RETALIATION_EFFECTS,
                SWIM_IN_AIR,
                ATTACK_EFFECTS,
                CURE_EFFECTS
        ));
    }

    // TODO: use a component or tag to mark items with cooldowns
    public static boolean hasAbilityWithCooldown(ItemStack stack) {
        for (Supplier<? extends DataComponentType<?>> componentType : APPLIES_COOLDOWN) {
            if (stack.has(componentType.get())) {
                return true;
            }
        }
        return false;
    }

    private static Supplier<DataComponentType<SimpleAbility>> registerSimpleAbility(String name) {
        return registerSynced(name, SimpleAbility.CODEC, SimpleAbility.STREAM_CODEC);
    }

    private static <ENTRY extends EquipmentAbility> Supplier<DataComponentType<CompositeAbility<ENTRY>>> registerComposite(String name, Codec<ENTRY> codec, StreamCodec<RegistryFriendlyByteBuf, ENTRY> streamCodec) {
        return registerSynced(name, CompositeAbility.codec(codec), CompositeAbility.streamCodec(streamCodec));
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

    public record TickingAbility<T extends EquipmentAbility>(
            Supplier<DataComponentType<T>> type,
            AbilityTicker<T> ticker
    ) { }
}
