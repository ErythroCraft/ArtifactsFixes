package artifacts.registry;

import artifacts.Artifacts;
import artifacts.component.HurtSound;
import artifacts.component.ToggleIdentifier;
import artifacts.component.ability.*;
import artifacts.component.ability.mobeffect.AttackEffect;
import artifacts.component.ability.mobeffect.MobEffectProvider;
import artifacts.component.ability.mobeffect.PostDamageEffect;
import artifacts.component.ability.mobeffect.PostEatingEffect;
import artifacts.component.ability.retaliation.*;
import artifacts.config.ItemConfigs;
import artifacts.config.value.Value;
import artifacts.item.ArtifactProperties;
import artifacts.item.consumeeffects.DamageItemConsumeEffect;
import artifacts.item.consumeeffects.HealConsumeEffect;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.feline.CatSoundVariants;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.food.Foods;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.*;
import net.minecraft.world.item.consume_effects.ClearAllStatusEffectsConsumeEffect;
import net.minecraft.world.item.consume_effects.TeleportRandomlyConsumeEffect;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.item.equipment.Equippable;

import java.util.List;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class ModItems {

    public static final Register<Item> ITEMS = Register.create(Registries.ITEM);
    public static final Register<CreativeModeTab> CREATIVE_MODE_TABS = Register.create(Registries.CREATIVE_MODE_TAB);

    public static final Holder<Item> MIMIC_SPAWN_EGG = register("mimic_spawn_egg", SpawnEggItem::new,
            () -> new Item.Properties().spawnEgg(ModEntityTypes.MIMIC.get())
    );

    // handheld
    public static final Holder<Item> UMBRELLA = ModItems.<ItemConfigs.Umbrella>
            register("umbrella", (builder, config) -> builder
            .component(ModDataComponents.HANDHELD_GLIDER.get(), config.isGlider)
            .component(DataComponents.EQUIPPABLE, Equippable.builder(EquipmentSlot.OFFHAND).setSwappable(false).build())
            .component(ModDataComponents.BLOCKS_ATTACKS.get(), config.isShield)
            .delayedComponent(DataComponents.BLOCKS_ATTACKS, config.isShield,
                    context -> new BlocksAttacks(
                            0.25F,
                            1,
                            List.of(new BlocksAttacks.DamageReduction(90, Optional.empty(), 0, 1)),
                            new BlocksAttacks.ItemDamageFunction(
                                    3,
                                    config.durability.damagePerBlockedAttackBase.get(),
                                    config.durability.damagePerBlockedAttackFactor.get().floatValue()
                            ),
                            Optional.of(context.getOrThrow(DamageTypeTags.BYPASSES_SHIELD)),
                            Optional.of(SoundEvents.SHIELD_BLOCK),
                            Optional.of(SoundEvents.SHIELD_BREAK)
                    )
            )
            .component(DataComponents.SWING_ANIMATION, new SwingAnimation(SwingAnimationType.STAB, (int) (0.75F * 20)))
            .component(
                    DataComponents.PIERCING_WEAPON,
                    new PiercingWeapon(
                            true,
                            false,
                            Optional.of(SoundEvents.SPEAR_WOOD_ATTACK),
                            Optional.of(SoundEvents.SPEAR_WOOD_HIT)
                    )
            )
            .properties(p -> p.delayedHolderComponent(DataComponents.DAMAGE_TYPE, DamageTypes.SPEAR))
            .component(
                    DataComponents.ATTACK_RANGE,
                    new AttackRange(0, 3.5F, 0, 5.5F, 0.25F, 0.5F))
            .component(DataComponents.MINIMUM_ATTACK_CHARGE, 1F)
            .delayedComponent(DataComponents.WEAPON, _ -> new Weapon(config.durability.damagePerAttack.get()))
            .component(DataComponents.BREAK_SOUND, SoundEvents.SHIELD_BREAK)
            .durability(config.durability)
            .properties(p -> p.attributes(
                    ItemAttributeModifiers.builder()
                            .add(
                                    Attributes.ATTACK_DAMAGE,
                                    new AttributeModifier(
                                            Item.BASE_ATTACK_DAMAGE_ID,
                                            ToolMaterial.STONE.attackDamageBonus(),
                                            AttributeModifier.Operation.ADD_VALUE
                                    ),
                                    EquipmentSlotGroup.MAINHAND
                            ).add(
                                    Attributes.ATTACK_SPEED,
                                    new AttributeModifier(
                                            Item.BASE_ATTACK_SPEED_ID,
                                            (1 / 0.75F) - 4,
                                            AttributeModifier.Operation.ADD_VALUE
                                    ),
                                    EquipmentSlotGroup.MAINHAND
                            ).build()
                    )
            )
    );
    public static final Holder<Item> EVERLASTING_BEEF = ModItems.<ItemConfigs.EverlastingBeef>
            register("everlasting_beef", (builder, config) -> builder
            .durability(config.durability)
            .delayedComponent(DataComponents.FOOD, config.enabled, _ -> Foods.BEEF)
            .delayedComponent(
                    DataComponents.CONSUMABLE,
                    config.enabled,
                    _ -> Consumables.defaultFood().onConsume(
                            new DamageItemConsumeEffect(
                                    config.durability.damageWhenConsumed
                            )
                    ).build()
            )
            .delayedComponent(
                    DataComponents.USE_COOLDOWN,
                    config.enabled,
                    _ -> new UseCooldown(config.cooldown.get())
            )
            .component(ModDataComponents.INFINITE_CONSUMABLE.get(), config.enabled)
    );
    public static final Holder<Item> ETERNAL_STEAK = ModItems.<ItemConfigs.EternalSteak>
            register("eternal_steak", (builder, config) -> builder
            .durability(config.durability)
            .delayedComponent(DataComponents.FOOD, config.enabled, _ -> Foods.COOKED_BEEF)
            .delayedComponent(
                    DataComponents.CONSUMABLE,
                    config.enabled,
                    _ -> Consumables.defaultFood().onConsume(
                            new DamageItemConsumeEffect(config.durability.damageWhenConsumed)
                    ).build()
            )
            .delayedComponent(
                    DataComponents.USE_COOLDOWN,
                    config.enabled,
                    _ -> new UseCooldown(config.cooldown.get())
            )
            .component(ModDataComponents.INFINITE_CONSUMABLE.get(), config.enabled)
    );

    // head
    // TODO: add durability config options
    public static final Holder<Item> PLASTIC_DRINKING_HAT = ModItems.<ItemConfigs.DrinkingHat>
            register("plastic_drinking_hat", (builder, config) -> builder
            .equipable(SoundEvents.BOTTLE_FILL)
            .modifiesAttributeBase(ModAttributes.DRINKING_SPEED, config.drinkingSpeedBonus)
            .modifiesAttributeBase(ModAttributes.EATING_SPEED, config.eatingSpeedBonus)
    );
    // TODO: add durability config options
    public static final Holder<Item> NOVELTY_DRINKING_HAT = ModItems.<ItemConfigs.DrinkingHat>
            register("novelty_drinking_hat", (builder, config) -> builder
            .equipable(SoundEvents.BOTTLE_FILL)
            .component(
                    ModDataComponents.ABILITY_LORE.get(),
                    new ItemLore(List.of(Component.translatable("artifacts.tooltip.item.novelty_drinking_hat").withStyle(ChatFormatting.GRAY)))
            )
            .modifiesAttributeBase(ModAttributes.DRINKING_SPEED, config.drinkingSpeedBonus)
            .modifiesAttributeBase(ModAttributes.EATING_SPEED, config.eatingSpeedBonus)
    );
    // TODO: add durability config options
    public static final Holder<Item> SNORKEL = ModItems.<ItemConfigs.Snorkel>
            register("snorkel", (builder, config) -> builder
            .equipable()
            .mobEffect(
                    MobEffects.WATER_BREATHING,
                    Value.of(1),
                    config.waterBreathingDuration,
                    () -> config.isInfinite.get() ? EntityCondition.ALWAYS : EntityCondition.ABOVE_WATER
            )
    );
    // TODO: add durability config options
    public static final Holder<Item> NIGHT_VISION_GOGGLES = ModItems.<ItemConfigs.NightVisionGoggles>
            register("night_vision_goggles", (builder, config) -> builder
            .equipable()
            .mobEffect(MobEffects.NIGHT_VISION, Value.of(1), Value.of(10), () -> EntityCondition.ALWAYS)
            .component(ModDataComponents.REDUCED_NIGHT_VISION.get(), config.strength)
            .component(ModDataComponents.TOGGLE_KEY.get(), ToggleIdentifier.NIGHT_VISION_GOGGLES)
    );
    // TODO: add durability config options
    public static final Holder<Item> VILLAGER_HAT = ModItems.<ItemConfigs.VillagerHat>
            register("villager_hat", (builder, config) -> builder
            .equipable()
            .increasesAttribute(ModAttributes.VILLAGER_REPUTATION, config.reputationBonus)
    );
    // TODO: add durability config options
    public static final Holder<Item> SUPERSTITIOUS_HAT = ModItems.<ItemConfigs.SuperstitiousHat>
            register("superstitious_hat", (builder, config) -> builder
            .equipable()
            .increasesEnchantment(Enchantments.LOOTING, config.lootingLevelBonus)
    );
    // TODO: add durability config options
    public static final Holder<Item> COWBOY_HAT = ModItems.<ItemConfigs.CowboyHat>
            register("cowboy_hat", (builder, config) -> builder
            .equipable(SoundEvents.ARMOR_EQUIP_LEATHER)
            .modifiesAttributeBase(ModAttributes.MOUNT_SPEED, config.mountSpeedBonus)
    );
    // TODO: add durability config options
    public static final Holder<Item> ANGLERS_HAT = ModItems.<ItemConfigs.AnglersHat>
            register("anglers_hat", (builder, config) -> builder
            .equipable(SoundEvents.ARMOR_EQUIP_LEATHER)
            .increasesEnchantment(Enchantments.LUCK_OF_THE_SEA, config.luckOfTheSeaLevelBonus)
            .increasesEnchantment(Enchantments.LURE, config.lureLevelBonus)
    );

    // necklace
    // TODO: add durability config options
    public static final Holder<Item> LUCKY_SCARF = ModItems.<ItemConfigs.LuckyScarf>
            register("lucky_scarf", (builder, config) -> builder
            .equipable()
            .increasesEnchantment(Enchantments.FORTUNE, config.fortuneLevelBonus)
    );
    // TODO: add durability config options
    public static final Holder<Item> SCARF_OF_INVISIBILITY = ModItems.<ItemConfigs.ScarfOfInvisibility>
            register("scarf_of_invisibility", (builder, config) -> builder
            .equipable()
            .mobEffect(
                    MobEffects.INVISIBILITY,
                    Value.of(1),
                    Value.of(10),
                    () -> config.enabled.get() ? EntityCondition.ALWAYS : EntityCondition.NEVER
            )
            .component(ModDataComponents.TOGGLE_KEY.get(), ToggleIdentifier.SCARF_OF_INVISIBILITY)
            .component(ModDataComponents.HIDE_WHEN_INVISIBLE.get(), config.hideWhenInvisible)
    );
    public static final Holder<Item> CROSS_NECKLACE = ModItems.<ItemConfigs.CrossNecklace>
            register("cross_necklace", (builder, config) -> builder
            .equipable(SoundEvents.ARMOR_EQUIP_DIAMOND)
            .durability(config.durability)
            .component(ModDataComponents.PIGLIN_LOVED.get())
            .cooldownOnHurt(config.cooldown)
            .damageOnHurt(config.durability.damagePerActivation)
            .addAttributeModifier(
                    ModAttributes.INVINCIBILITY_TICKS,
                    config.bonusInvincibilityTicks,
                    AttributeModifier.Operation.ADD_VALUE,
                    () -> true, false
            )
    );
    public static final Holder<Item> PANIC_NECKLACE = ModItems.<ItemConfigs.PanicNecklace>
            register("panic_necklace", (builder, config) -> builder
            .equipable(SoundEvents.ARMOR_EQUIP_DIAMOND)
            .durability(config.durability)
            .component(
                    ModDataComponents.POST_DAMAGE_EFFECTS.get(),
                    new PostDamageEffect(
                            new MobEffectProvider(
                                    MobEffects.SPEED,
                                    config.speedLevel,
                                    config.speedDuration,
                                    Value.of(true),
                                    Value.of(true),
                                    EntityCondition.ALWAYS
                            ),
                            Optional.empty()
                    )
            )
            .cooldownOnHurt(config.cooldown)
            .damageOnHurt(config.durability.damagePerActivation)
    );
    public static final Holder<Item> SHOCK_PENDANT = ModItems.<ItemConfigs.ShockPendant>
            register("shock_pendant", (builder, config) -> builder
            .equipable()
            .durability(config.durability)
            .component(
                    ModDataComponents.RETALIATION_EFFECTS.get(),
                    new RetaliationEffects(
                            Optional.empty(),
                            Optional.empty(),
                            Optional.of(new LightningEffect(config.activationParams()))
                    )
            )
            .component(ModDataComponents.DAMAGE_IMMUNITY.get(),
                    new DamageImmunity(
                            config.cancelLightningDamage,
                            DamageTypeTags.IS_LIGHTNING,
                            EntityCondition.ALWAYS
                    )
            )
    );
    public static final Holder<Item> FLAME_PENDANT = ModItems.<ItemConfigs.FlamePendant>
            register("flame_pendant", (builder, config) -> builder
            .equipable()
            .durability(config.durability)
            .component(
                    ModDataComponents.RETALIATION_EFFECTS.get(),
                    new RetaliationEffects(
                            Optional.empty(),
                            Optional.of(new FireEffect(config.activationParams(), config.fireDuration, config.grantFireResistance)),
                            Optional.empty()
                    )
            )
    );
    public static final Holder<Item> THORN_PENDANT = ModItems.<ItemConfigs.ThornPendant>
            register("thorn_pendant", (builder, config) -> builder
            .equipable()
            .durability(config.durability)
            .component(
                    ModDataComponents.RETALIATION_EFFECTS.get(),
                    new RetaliationEffects(
                            Optional.of(new ThornsEffect(config.activationParams(), config.minDamage, config.maxDamage)),
                            Optional.empty(),
                            Optional.empty()
                    )
            )
    );
    // TODO: add durability config options
    public static final Holder<Item> CHARM_OF_SINKING = ModItems.<ItemConfigs.CharmOfSinking>
            register("charm_of_sinking", (builder, config) -> builder
            .equipable()
            .component(ModDataComponents.SINKING.get(), config.enabled)
            .delayedComponent(ModDataComponents.DAMAGE_IMMUNITY.get(), _ -> new DamageImmunity(
                    config.enabled,
                    DamageTypeTags.IS_FALL,
                    config.underwaterFallDamage.get() ? EntityCondition.NEVER : EntityCondition.IN_WATER
            ))
            .addAttributeModifier(
                    Attributes.OXYGEN_BONUS,
                    config.oxygenBonus,
                    AttributeModifier.Operation.ADD_VALUE,
                    config.enabled,
                    true
            )
            .component(ModDataComponents.TOGGLE_KEY.get(), ToggleIdentifier.CHARM_OF_SINKING)
    );
    // TODO: add durability config options
    public static final Holder<Item> CHARM_OF_SHRINKING = ModItems.<ItemConfigs.CharmOfShrinking>
            register("charm_of_shrinking", (builder, config) -> builder
            .equipable()
            .modifiesAttributeTotal(Attributes.SCALE, config.scaleModifier)
            .component(ModDataComponents.TOGGLE_KEY.get(), ToggleIdentifier.CHARM_OF_SHRINKING)
    );

    // belt
    // TODO: add durability config options
    public static final Holder<Item> CLOUD_IN_A_BOTTLE = ModItems.<ItemConfigs.CloudInABottle>
            register("cloud_in_a_bottle", (builder, config) -> builder
            .equipable(SoundEvents.BOTTLE_FILL_DRAGONBREATH)
            .component(ModDataComponents.DOUBLE_JUMP.get(), new DoubleJump(
                    config.enabled,
                    config.fallDamageMultiplier,
                    config.sprintJumpHorizontalVelocity,
                    config.sprintJumpVerticalVelocity
            ))
            .increasesAttribute(Attributes.SAFE_FALL_DISTANCE, config.safeFallDistanceBonus)
    );
    public static final Holder<Item> OBSIDIAN_SKULL = ModItems.<ItemConfigs.ObsidianSkull>
            register("obsidian_skull", (builder, config) -> builder
            .equipable(SoundEvents.ARMOR_EQUIP_IRON)
            .durability(config.durability)
            .component(
                    ModDataComponents.POST_DAMAGE_EFFECTS.get(),
                    new PostDamageEffect(
                            new MobEffectProvider(
                                    MobEffects.FIRE_RESISTANCE,
                                    Value.of(1),
                                    config.fireResistanceDuration,
                                    Value.of(true),
                                    Value.of(true),
                                    EntityCondition.ALWAYS
                            ),
                            Optional.of(DamageTypeTags.IS_FIRE)
                    )
            )
            .cooldownOnHurt(config.cooldown, DamageTypeTags.IS_FIRE)
            .damageOnHurt(config.durability.damagePerActivation, DamageTypeTags.IS_FIRE)
    );
    public static final Holder<Item> ANTIDOTE_VESSEL = ModItems.<ItemConfigs.AntidoteVessel>
            register("antidote_vessel", (builder, config) -> builder
            .equipable(SoundEvents.BOTTLE_FILL)
            .durability(config.durability)
            .component(ModDataComponents.PIGLIN_LOVED.get())
            .component(ModDataComponents.CURE_EFFECTS.get(), new CureEffects(
                    config.enabled,
                    config.maxEffectDuration,
                    config.durability.damagePerActivation
            ))
    );
    // TODO: add durability config options
    public static final Holder<Item> UNIVERSAL_ATTRACTOR = ModItems.<ItemConfigs.UniversalAttractor>
            register("universal_attractor", (builder, config) -> builder
            .equipable()
            .component(ModDataComponents.PIGLIN_LOVED.get())
            .mobEffect(ModMobEffects.MAGNETISM, config.magnetismLevel, Value.of(10), () -> EntityCondition.ALWAYS)
            .component(ModDataComponents.TOGGLE_KEY.get(), ToggleIdentifier.UNIVERSAL_ATTRACTOR)
    );
    // TODO: add durability config options
    public static final Holder<Item> CRYSTAL_HEART = ModItems.<ItemConfigs.CrystalHeart>
            register("crystal_heart", (builder, config) -> builder
            .equipable(SoundEvents.ARMOR_EQUIP_DIAMOND)
            .increasesAttribute(Attributes.MAX_HEALTH, config.healthBonus)
    );
    // TODO: add durability config options
    public static final Holder<Item> HELIUM_FLAMINGO = ModItems.<ItemConfigs.HeliumFlamingo>
            register("helium_flamingo", (builder, config) -> builder
            .equipable(ModSoundEvents.POP)
            .component(ModDataComponents.SWIM_IN_AIR.get(), new SwimInAir(
                    config.flightDuration,
                    config.rechargeDuration,
                    config.cooldown
            ))
    );
    public static final Holder<Item> CHORUS_TOTEM = ModItems.<ItemConfigs.ChorusTotem>
            register("chorus_totem", (builder, config) -> builder
            .equipable()
            .component(ModDataComponents.EQUIPABLE_TOTEM.get(), new EquipableTotem(
                    config.enabled
            ))
            .delayedComponent(
                    DataComponents.DEATH_PROTECTION,
                    config.enabled,
                    _ -> new DeathProtection(List.of(
                            new ClearAllStatusEffectsConsumeEffect(),
                            new TeleportRandomlyConsumeEffect(32),
                            new HealConsumeEffect(config.healthRestored))
                    )
            )
    );
    // TODO: add durability config options
    public static final Holder<Item> WARP_DRIVE = ModItems.<ItemConfigs.WarpDrive>
            register("warp_drive", (builder, config) -> builder
            .equipable()
            .component(ModDataComponents.ENDER_PEARL_HUNGER_COST.get(), new EnderPearlHungerCost(
                    config.enabled,
                    config.hungerCost,
                    config.cooldown
            ))
            .component(ModDataComponents.ENDER_PEARL_DAMAGE_IMMUNITY.get(), config.nullifyEnderPearlDamage)
    );

    // hands
    // TODO: add durability config options
    public static final Holder<Item> DIGGING_CLAWS = ModItems.<ItemConfigs.DiggingClaws>
            register("digging_claws", (builder, config) -> builder
            .equipable(SoundEvents.ARMOR_EQUIP_NETHERITE)
            .modifiesAttributeBase(Attributes.BLOCK_BREAK_SPEED, config.blockBreakSpeedBonus)
            .component(ModDataComponents.TOOL_TIER_UPGRADE.get(), new ToolTierUpgrade(config.toolTier))
    );
    // TODO: add durability config options
    public static final Holder<Item> FERAL_CLAWS = ModItems.<ItemConfigs.FeralClaws>
            register("feral_claws", (builder, config) -> builder
            .equipable(SoundEvents.ARMOR_EQUIP_NETHERITE)
            .modifiesAttributeBase(Attributes.ATTACK_SPEED, config.attackSpeedBonus)
    );
    // TODO: add durability config options
    public static final Holder<Item> POWER_GLOVE = ModItems.<ItemConfigs.PowerGlove>
            register("power_glove", (builder, config) -> builder
            .equipable()
            .increasesAttribute(Attributes.ATTACK_DAMAGE, config.attackDamageBonus)
    );
    // TODO: add durability config options
    public static final Holder<Item> FIRE_GAUNTLET = ModItems.<ItemConfigs.FireGauntlet>
            register("fire_gauntlet", (builder, config) -> builder
            .equipable(SoundEvents.ARMOR_EQUIP_IRON)
            .increasesAttribute(ModAttributes.ATTACK_BURNING_DURATION, config.fireDuration)
    );
    // TODO: add durability config options
    public static final Holder<Item> POCKET_PISTON = ModItems.<ItemConfigs.PocketPiston>
            register("pocket_piston", (builder, config) -> builder
            .equipable(SoundEvents.PISTON_EXTEND)
            .increasesAttribute(Attributes.ATTACK_KNOCKBACK, config.attackKnockbackBonus)
    );
    // TODO: add durability config options
    public static final Holder<Item> VAMPIRIC_GLOVE = ModItems.<ItemConfigs.VampiricGlove>
            register("vampiric_glove", (builder, config) -> builder
            .equipable()
            .component(
                    ModDataComponents.DAMAGE_ABSORPTION.get(),
                    new DamageAbsorption(config.absorptionRatio, config.absorptionChance, config.maxHealingPerHit)
            )
    );
    // TODO: add durability config options
    public static final Holder<Item> GOLDEN_HOOK = ModItems.<ItemConfigs.GoldenHook>
            register("golden_hook", (builder, config) -> builder
            .equipable()
            .modifiesAttributeBase(ModAttributes.ENTITY_EXPERIENCE, config.entityExperienceBonus)
            .component(ModDataComponents.PIGLIN_LOVED.get())
    );
    // TODO: fix not being able to eat
    public static final Holder<Item> ONION_RING = ModItems.<ItemConfigs.OnionRing>
            register("onion_ring", (builder, config) -> builder
            .equipable()
            .durability(config.durability)
            .properties(properties -> properties.food(new FoodProperties.Builder().nutrition(2).build()))
            .component(
                    ModDataComponents.POST_EATING_EFFECTS.get(),
                    new PostEatingEffect(
                            new MobEffectProvider(
                                    MobEffects.HASTE,
                                    config.hasteLevel,
                                    config.hasteDurationPerFoodPoint,
                                    Value.of(true),
                                    Value.of(true),
                                    EntityCondition.ALWAYS
                            ),
                            config.durability.damagePerActivation
                    )
            )
    );
    // TODO: add durability config options
    public static final Holder<Item> PICKAXE_HEATER = ModItems.<ItemConfigs.PickaxeHeater>
            register("pickaxe_heater", (builder, config) -> builder
            .equipable(SoundEvents.ARMOR_EQUIP_IRON)
            .component(ModDataComponents.AUTO_SMELT.get(), config.enabled)
    );
    // TODO: add durability config options
    public static final Holder<Item> WITHERED_BRACELET = ModItems.<ItemConfigs.WitheredBracelet>
            register("withered_bracelet", (builder, config) -> builder
            .equipable()
            .component(
                    ModDataComponents.ATTACK_EFFECTS.get(),
                    new AttackEffect(
                            new MobEffectProvider(
                                    MobEffects.WITHER,
                                    config.witherLevel,
                                    config.witherDuration,
                                    Value.of(true),
                                    Value.of(true),
                                    EntityCondition.ALWAYS
                            ),
                            config.witherChance,
                            config.cooldown
                    )
            )
    );

    // feet
    // TODO: add durability config options
    public static final Holder<Item> AQUA_DASHERS = ModItems.<ItemConfigs.AquaDashers>
            register("aqua_dashers", (builder, config) -> builder
            .equipable()
            .component(
                    ModDataComponents.FLUID_COLLISION.get(),
                    new FluidCollision(config.enabled, Optional.empty(), EntityCondition.SPRINTING)
            )
    );
    // TODO: add durability config options
    public static final Holder<Item> BUNNY_HOPPERS = ModItems.<ItemConfigs.BunnyHoppers>
            register("bunny_hoppers", (builder, config) -> builder
            .equipable()
            .modifiesAttributeBase(Attributes.JUMP_STRENGTH, config.jumpStrengthBonus)
            .modifiesAttributeBase(Attributes.FALL_DAMAGE_MULTIPLIER, config.fallDamageMultiplier)
            .increasesAttribute(Attributes.SAFE_FALL_DISTANCE, config.safeFallDistanceBonus)
            .component(
                    ModDataComponents.HURT_SOUND.get(),
                    new HurtSound(
                            BuiltInRegistries.SOUND_EVENT.wrapAsHolder(SoundEvents.RABBIT_HURT),
                            config.modifyHurtSounds
                    )
            )
    );
    // TODO: add durability config options
    public static final Holder<Item> KITTY_SLIPPERS = ModItems.<ItemConfigs.KittySlippers>
            register("kitty_slippers", (builder, config) -> builder
            .equipable(SoundEvents.CAT_SOUNDS.get(CatSoundVariants.SoundSet.CLASSIC).adultSounds().ambientSound())
            .component(ModDataComponents.CREEPER_REPELLENT.get(), config.repelCreepers)
            .component(ModDataComponents.PHANTOM_REPELLENT.get(), config.repelPhantoms)
            .component(
                    ModDataComponents.HURT_SOUND.get(),
                    new HurtSound(
                            SoundEvents.CAT_SOUNDS.get(CatSoundVariants.SoundSet.CLASSIC).adultSounds().hurtSound(),
                            config.modifyHurtSounds
                    )
            )
    );
    // TODO: add durability config options
    public static final Holder<Item> RUNNING_SHOES = ModItems.<ItemConfigs.RunningShoes>
            register("running_shoes", (builder, config) -> builder
            .equipable()
            .modifiesAttributeBase(ModAttributes.SPRINTING_SPEED, config.sprintingSpeedBonus)
            .increasesAttribute(ModAttributes.SPRINTING_STEP_HEIGHT, config.sprintingStepHeightBonus)
    );
    // TODO: add durability config options
    public static final Holder<Item> SNOWSHOES = ModItems.<ItemConfigs.Snowshoes>
            register("snowshoes", (builder, config) -> builder
            .equipable()
            .component(ModDataComponents.WALK_ON_POWDER_SNOW.get(), config.allowWalkingOnPowderedSnow)
            .modifiesAttributeBase(ModAttributes.MOVEMENT_SPEED_ON_SNOW, config.movementSpeedOnSnowBonus)
    );
    public static final Holder<Item> STEADFAST_SPIKES = ModItems.<ItemConfigs.SteadfastSpikes>
            register("steadfast_spikes", (builder, config) -> builder
            .equipable()
            .durability(config.durability)
            .increasesAttribute(Attributes.KNOCKBACK_RESISTANCE, config.knockbackResistance)
            .increasesAttribute(ModAttributes.SLIP_RESISTANCE, config.slipperinessReduction)
            .damageOnHurt(config.durability.damageWhenAttacked, ModTags.IS_MELEE)
    );
    // TODO: add durability config options
    public static final Holder<Item> FLIPPERS = ModItems.<ItemConfigs.Flippers>
            register("flippers", (builder, config) -> builder
            .equipable()
            .modifiesAttributeBase(ModAttributes.SWIM_SPEED, config.swimSpeedBonus)
    );
    // TODO: add durability config options
    public static final Holder<Item> ROOTED_BOOTS = ModItems.<ItemConfigs.RootedBoots>
            register("rooted_boots", (builder, config) -> builder
            .equipable(SoundEvents.ARMOR_EQUIP_LEATHER)
            .component(
                    ModDataComponents.REPLENISH_HUNGER_ON_GRASS.get(),
                    new ReplenishHungerOnGrass(config.enabled, config.hungerReplenishingDuration)
            )
            .component(ModDataComponents.POST_EATING_PLANT_GROWTH.get(), config.growPlantsAfterEating)
    );
    // TODO: add durability config options
    public static final Holder<Item> STRIDER_SHOES = ModItems.<ItemConfigs.StriderShoes>
            register("strider_shoes", (builder, config) -> builder
            .equipable(SoundEvents.ARMOR_EQUIP_LEATHER)
            .component(
                    ModDataComponents.FLUID_COLLISION.get(),
                    new FluidCollision(config.enabled, Optional.of(FluidTags.LAVA), EntityCondition.SNEAKING)
            ).component(
                    ModDataComponents.DAMAGE_IMMUNITY.get(),
                    new DamageImmunity(config.cancelHotFloorDamage, DamageTypeTags.BURN_FROM_STEPPING, EntityCondition.ALWAYS)
            )
    );

    // curio
    // TODO: add durability config options
    public static final Holder<Item> WHOOPEE_CUSHION = ModItems.<ItemConfigs.WhoopeeCushion>
            register("whoopee_cushion", (builder, config) -> builder
            .equipable(ModSoundEvents.FART)
            .increasesAttribute(ModAttributes.FLATULENCE, config.fartChance)
    );

    private static <CONFIG> Holder<Item> register(String name, BiConsumer<ArtifactProperties, CONFIG> consumer) {
        return register(name, Item::new, () -> {
            ArtifactProperties builder = new ArtifactProperties(name);
            // this kinda stinks, but it beats having to write a novel every time or using the wrong item's config by accident
            CONFIG config = Artifacts.CONFIG.items.get(Artifacts.key(Registries.ITEM, name));
            consumer.accept(builder, config);
            return builder.build();
        });
    }

    private static Holder<Item> register(String name, Function<Item.Properties, ? extends Item> factory, Supplier<Item.Properties> properties) {
        return ITEMS.register(name, () -> factory.apply(properties.get().setId(Artifacts.key(Registries.ITEM, name))));
    }
}
