package artifacts.registry;

import artifacts.Artifacts;
import artifacts.component.HurtSound;
import artifacts.component.ToggleIdentifier;
import artifacts.component.ability.*;
import artifacts.component.ability.mobeffect.AttackEffect;
import artifacts.component.ability.mobeffect.MobEffectProvider;
import artifacts.component.ability.mobeffect.PostDamageEffect;
import artifacts.component.ability.mobeffect.PostEatingEffect;
import artifacts.component.ability.retaliation.FireEffect;
import artifacts.component.ability.retaliation.LightningEffect;
import artifacts.component.ability.retaliation.RetaliationEffects;
import artifacts.component.ability.retaliation.ThornsEffect;
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
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class ModItems {

    public static final Register<Item> ITEMS = Register.create(Registries.ITEM);
    public static final Register<CreativeModeTab> CREATIVE_MODE_TABS = Register.create(Registries.CREATIVE_MODE_TAB);

    public static final Holder<Item> MIMIC_SPAWN_EGG = register("mimic_spawn_egg", SpawnEggItem::new,
            () -> new Item.Properties().spawnEgg(ModEntityTypes.MIMIC.get())
    );

    // handheld
    public static final Holder<Item> UMBRELLA = register("umbrella", properties -> properties
            .component(ModDataComponents.HANDHELD_GLIDER.get(), Artifacts.CONFIG.items.umbrella.isGlider)
            .component(DataComponents.EQUIPPABLE, Equippable.builder(EquipmentSlot.OFFHAND).setSwappable(false).build())
            .component(ModDataComponents.BLOCKS_ATTACKS.get(), Artifacts.CONFIG.items.umbrella.isShield)
            .delayedComponent(DataComponents.BLOCKS_ATTACKS, Artifacts.CONFIG.items.umbrella.isShield,
                    context -> new BlocksAttacks(
                            0.25F,
                            1,
                            List.of(new BlocksAttacks.DamageReduction(90, Optional.empty(), 0, 1)),
                            new BlocksAttacks.ItemDamageFunction(
                                    3,
                                    Artifacts.CONFIG.items.umbrella.durability.damagePerBlockedAttackBase.get(),
                                    Artifacts.CONFIG.items.umbrella.durability.damagePerBlockedAttackFactor.get().floatValue()
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
            .component(DataComponents.ATTACK_RANGE, new AttackRange(0, 3.5F, 0, 5.5F, 0.25F, 0.5F))
            .component(DataComponents.MINIMUM_ATTACK_CHARGE, 1F)
            .delayedComponent(DataComponents.WEAPON, _ -> new Weapon(Artifacts.CONFIG.items.umbrella.durability.damagePerAttack.get()))
            .component(DataComponents.BREAK_SOUND, SoundEvents.SHIELD_BREAK)
            .durability(Artifacts.CONFIG.items.umbrella.durability)
            .properties(p -> p.attributes(ItemAttributeModifiers.builder()
                    .add(Attributes.ATTACK_DAMAGE, new AttributeModifier(Item.BASE_ATTACK_DAMAGE_ID, ToolMaterial.STONE.attackDamageBonus(), AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND)
                    .add(Attributes.ATTACK_SPEED, new AttributeModifier(Item.BASE_ATTACK_SPEED_ID, (1 / 0.75F) - 4, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND)
                    .build()
            ))
    );
    public static final Holder<Item> EVERLASTING_BEEF = register("everlasting_beef", builder -> builder
            .durability(Artifacts.CONFIG.items.everlastingBeef.durability)
            .delayedComponent(DataComponents.FOOD, Artifacts.CONFIG.items.everlastingBeef.enabled, _ -> Foods.BEEF)
            .delayedComponent(DataComponents.CONSUMABLE, Artifacts.CONFIG.items.everlastingBeef.enabled,
                    _ -> Consumables.defaultFood().onConsume(new DamageItemConsumeEffect(
                            Artifacts.CONFIG.items.everlastingBeef.durability.damageWhenConsumed
                    )).build()
            )
            .delayedComponent(
                    DataComponents.USE_COOLDOWN,
                    Artifacts.CONFIG.items.everlastingBeef.enabled,
                    _ -> new UseCooldown(Artifacts.CONFIG.items.everlastingBeef.cooldown.get())
            )
            .component(ModDataComponents.INFINITE_CONSUMABLE.get(), Artifacts.CONFIG.items.everlastingBeef.enabled)
    );
    public static final Holder<Item> ETERNAL_STEAK = register("eternal_steak", builder -> builder
            .durability(Artifacts.CONFIG.items.eternalSteak.durability)
            .delayedComponent(DataComponents.FOOD, Artifacts.CONFIG.items.eternalSteak.enabled, _ -> Foods.COOKED_BEEF)
            .delayedComponent(DataComponents.CONSUMABLE, Artifacts.CONFIG.items.eternalSteak.enabled,
                    _ -> Consumables.defaultFood().onConsume(new DamageItemConsumeEffect(
                            Artifacts.CONFIG.items.eternalSteak.durability.damageWhenConsumed
                    )).build()
            )
            .delayedComponent(
                    DataComponents.USE_COOLDOWN,
                    Artifacts.CONFIG.items.eternalSteak.enabled,
                    _ -> new UseCooldown(Artifacts.CONFIG.items.eternalSteak.cooldown.get())
            )
            .component(ModDataComponents.INFINITE_CONSUMABLE.get(), Artifacts.CONFIG.items.eternalSteak.enabled)
    );

    // head
    public static final Holder<Item> PLASTIC_DRINKING_HAT = register("plastic_drinking_hat", builder -> builder
            .equipable(SoundEvents.BOTTLE_FILL)
            .addAttributeModifier(ModAttributes.DRINKING_SPEED, Artifacts.CONFIG.items.plasticDrinkingHat.drinkingSpeedBonus, AttributeModifier.Operation.ADD_MULTIPLIED_BASE)
            .addAttributeModifier(ModAttributes.EATING_SPEED, Artifacts.CONFIG.items.plasticDrinkingHat.eatingSpeedBonus, AttributeModifier.Operation.ADD_MULTIPLIED_BASE)
    );
    public static final Holder<Item> NOVELTY_DRINKING_HAT = register("novelty_drinking_hat", builder -> builder
            .equipable(SoundEvents.BOTTLE_FILL)
            .component(ModDataComponents.ABILITY_LORE.get(),
                    new ItemLore(List.of(Component.translatable("artifacts.tooltip.item.novelty_drinking_hat").withStyle(ChatFormatting.GRAY)))
            )
            .addAttributeModifier(ModAttributes.DRINKING_SPEED, Artifacts.CONFIG.items.noveltyDrinkingHat.drinkingSpeedBonus, AttributeModifier.Operation.ADD_MULTIPLIED_BASE)
            .addAttributeModifier(ModAttributes.EATING_SPEED, Artifacts.CONFIG.items.noveltyDrinkingHat.eatingSpeedBonus, AttributeModifier.Operation.ADD_MULTIPLIED_BASE)
    );
    public static final Holder<Item> SNORKEL = register("snorkel", builder -> builder
            .equipable()
            .mobEffect(MobEffects.WATER_BREATHING, Value.of(1), Artifacts.CONFIG.items.snorkel.waterBreathingDuration,
                    () -> Artifacts.CONFIG.items.snorkel.isInfinite.get() ? EntityCondition.ALWAYS : EntityCondition.ABOVE_WATER
            )
    );
    public static final Holder<Item> NIGHT_VISION_GOGGLES = register("night_vision_goggles", builder -> builder
            .equipable()
            .mobEffect(MobEffects.NIGHT_VISION, Value.of(1), Value.of(10), () -> EntityCondition.ALWAYS)
            .component(ModDataComponents.REDUCED_NIGHT_VISION.get(), Artifacts.CONFIG.items.nightVisionGoggles.strength)
            .component(ModDataComponents.TOGGLE_KEY.get(), ToggleIdentifier.NIGHT_VISION_GOGGLES)
    );
    public static final Holder<Item> VILLAGER_HAT = register("villager_hat", builder -> builder
            .equipable()
            .addAttributeModifier(ModAttributes.VILLAGER_REPUTATION, Artifacts.CONFIG.items.villagerHat.reputationBonus, AttributeModifier.Operation.ADD_VALUE)
    );
    public static final Holder<Item> SUPERSTITIOUS_HAT = register("superstitious_hat", builder -> builder
            .equipable()
            .increasesEnchantment(Enchantments.LOOTING, Artifacts.CONFIG.items.superstitiousHat.lootingLevelBonus)
    );
    public static final Holder<Item> COWBOY_HAT = register("cowboy_hat", builder -> builder
            .equipable(SoundEvents.ARMOR_EQUIP_LEATHER)
            .addAttributeModifier(ModAttributes.MOUNT_SPEED, Artifacts.CONFIG.items.cowboyHat.mountSpeedBonus, AttributeModifier.Operation.ADD_MULTIPLIED_BASE)
    );
    public static final Holder<Item> ANGLERS_HAT = register("anglers_hat", builder -> builder
            .equipable(SoundEvents.ARMOR_EQUIP_LEATHER)
            .increasesEnchantment(Enchantments.LUCK_OF_THE_SEA, Artifacts.CONFIG.items.anglersHat.luckOfTheSeaLevelBonus)
            .increasesEnchantment(Enchantments.LURE, Artifacts.CONFIG.items.anglersHat.lureLevelBonus)
    );

    // necklace
    public static final Holder<Item> LUCKY_SCARF = register("lucky_scarf", builder -> builder
            .equipable()
            .increasesEnchantment(Enchantments.FORTUNE, Artifacts.CONFIG.items.luckyScarf.fortuneLevelBonus)
    );
    public static final Holder<Item> SCARF_OF_INVISIBILITY = register("scarf_of_invisibility", builder -> builder
            .equipable()
            .mobEffect(MobEffects.INVISIBILITY, Value.of(1), Value.of(10),
                    () -> Artifacts.CONFIG.items.scarfOfInvisibility.enabled.get() ? EntityCondition.ALWAYS : EntityCondition.NEVER
            )
            .component(ModDataComponents.TOGGLE_KEY.get(), ToggleIdentifier.SCARF_OF_INVISIBILITY)
            .component(ModDataComponents.HIDE_WHEN_INVISIBLE.get(), Artifacts.CONFIG.items.scarfOfInvisibility.hideWhenInvisible)
    );
    public static final Holder<Item> CROSS_NECKLACE = register("cross_necklace", builder -> builder
            .equipable(SoundEvents.ARMOR_EQUIP_DIAMOND)
            .component(ModDataComponents.PIGLIN_LOVED.get())
            .component(ModDataComponents.POST_DAMAGE_COOLDOWN.get(), new PostDamageCooldown(Artifacts.CONFIG.items.crossNecklace.cooldown, Optional.empty()))
            .addAttributeModifier(ModAttributes.INVINCIBILITY_TICKS, Artifacts.CONFIG.items.crossNecklace.bonusInvincibilityTicks, AttributeModifier.Operation.ADD_VALUE, () -> true, false)
    );
    public static final Holder<Item> PANIC_NECKLACE = register("panic_necklace", builder -> builder
            .equipable(SoundEvents.ARMOR_EQUIP_DIAMOND)
            .component(ModDataComponents.POST_DAMAGE_EFFECTS.get(), new CompositeAbility<>(List.of(new PostDamageEffect(
                    new MobEffectProvider(MobEffects.SPEED, Artifacts.CONFIG.items.panicNecklace.speedLevel, Artifacts.CONFIG.items.panicNecklace.speedDuration, Value.of(true), Value.of(true), EntityCondition.ALWAYS),
                    Optional.empty(),
                    Value.of(1D)
            ))))
            .component(ModDataComponents.POST_DAMAGE_COOLDOWN.get(), new PostDamageCooldown(
                    Artifacts.CONFIG.items.panicNecklace.cooldown,
                    Optional.empty()
            ))
    );
    public static final Holder<Item> SHOCK_PENDANT = register("shock_pendant", builder -> builder
            .equipable()
            .component(ModDataComponents.RETALIATION_EFFECTS.get(), new RetaliationEffects(
                    Optional.empty(),
                    Optional.empty(),
                    Optional.of(new LightningEffect(
                            Artifacts.CONFIG.items.shockPendant.strikeChance,
                            Artifacts.CONFIG.items.shockPendant.cooldown
                    ))
            ))
            .component(ModDataComponents.DAMAGE_IMMUNITY.get(),
                    new DamageImmunity(
                            Artifacts.CONFIG.items.shockPendant.cancelLightningDamage,
                            DamageTypeTags.IS_LIGHTNING,
                            EntityCondition.ALWAYS
                    )
            )
    );
    public static final Holder<Item> FLAME_PENDANT = register("flame_pendant", builder -> builder
            .equipable()
            .component(ModDataComponents.RETALIATION_EFFECTS.get(), new RetaliationEffects(
                    Optional.empty(),
                    Optional.of(new FireEffect(
                            Artifacts.CONFIG.items.flamePendant.strikeChance,
                            Artifacts.CONFIG.items.flamePendant.cooldown,
                            Artifacts.CONFIG.items.flamePendant.fireDuration,
                            Artifacts.CONFIG.items.flamePendant.grantFireResistance
                    )),
                    Optional.empty()
            ))
    );
    public static final Holder<Item> THORN_PENDANT = register("thorn_pendant", builder -> builder
            .equipable()
            .component(ModDataComponents.RETALIATION_EFFECTS.get(), new RetaliationEffects(
                    Optional.of(new ThornsEffect(
                            Artifacts.CONFIG.items.thornPendant.strikeChance,
                            Artifacts.CONFIG.items.thornPendant.cooldown,
                            Artifacts.CONFIG.items.thornPendant.minDamage,
                            Artifacts.CONFIG.items.thornPendant.maxDamage
                    )),
                    Optional.empty(),
                    Optional.empty()
            ))
    );
    public static final Holder<Item> CHARM_OF_SINKING = register("charm_of_sinking", builder -> builder
            .equipable()
            .component(ModDataComponents.SINKING.get(), Artifacts.CONFIG.items.charmOfSinking.enabled)
            .delayedComponent(ModDataComponents.DAMAGE_IMMUNITY.get(), _ -> new DamageImmunity(
                    Artifacts.CONFIG.items.charmOfSinking.enabled,
                    DamageTypeTags.IS_FALL,
                    Artifacts.CONFIG.items.charmOfSinking.underwaterFallDamage.get() ? EntityCondition.NEVER : EntityCondition.IN_WATER
            ))
            .addAttributeModifier(
                    Attributes.OXYGEN_BONUS,
                    Artifacts.CONFIG.items.charmOfSinking.oxygenBonus,
                    AttributeModifier.Operation.ADD_VALUE,
                    Artifacts.CONFIG.items.charmOfSinking.enabled,
                    true
            )
            .component(ModDataComponents.TOGGLE_KEY.get(), ToggleIdentifier.CHARM_OF_SINKING)
    );
    public static final Holder<Item> CHARM_OF_SHRINKING = register("charm_of_shrinking", builder -> builder
            .equipable()
            .addAttributeModifier(Attributes.SCALE, Artifacts.CONFIG.items.charmOfShrinking.scaleModifier, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)
            .component(ModDataComponents.TOGGLE_KEY.get(), ToggleIdentifier.CHARM_OF_SHRINKING)
    );

    // belt
    public static final Holder<Item> CLOUD_IN_A_BOTTLE = register("cloud_in_a_bottle", builder -> builder
            .equipable(SoundEvents.BOTTLE_FILL_DRAGONBREATH)
            .component(ModDataComponents.DOUBLE_JUMP.get(), new DoubleJump(
                    Artifacts.CONFIG.items.cloudInABottle.enabled,
                    Artifacts.CONFIG.items.cloudInABottle.fallDamageMultiplier,
                    Artifacts.CONFIG.items.cloudInABottle.sprintJumpHorizontalVelocity,
                    Artifacts.CONFIG.items.cloudInABottle.sprintJumpVerticalVelocity
            ))
            .addAttributeModifier(Attributes.SAFE_FALL_DISTANCE, Artifacts.CONFIG.items.cloudInABottle.safeFallDistanceBonus, AttributeModifier.Operation.ADD_VALUE)
    );
    public static final Holder<Item> OBSIDIAN_SKULL = register("obsidian_skull", builder -> builder
            .equipable(SoundEvents.ARMOR_EQUIP_IRON)
            .component(ModDataComponents.POST_DAMAGE_EFFECTS.get(), new CompositeAbility<>(List.of(new PostDamageEffect(
                    new MobEffectProvider(MobEffects.FIRE_RESISTANCE, Value.of(1), Artifacts.CONFIG.items.obsidianSkull.fireResistanceDuration, Value.of(true), Value.of(true), EntityCondition.ALWAYS),
                    Optional.of(DamageTypeTags.IS_FIRE),
                    Value.of(1D)
            ))))
            .component(ModDataComponents.POST_DAMAGE_COOLDOWN.get(), new PostDamageCooldown(
                    Artifacts.CONFIG.items.obsidianSkull.cooldown,
                    Optional.of(DamageTypeTags.IS_FIRE)
            ))
    );
    public static final Holder<Item> ANTIDOTE_VESSEL = register("antidote_vessel", builder -> builder
            .equipable(SoundEvents.BOTTLE_FILL)
            .component(ModDataComponents.PIGLIN_LOVED.get())
            .component(ModDataComponents.CURE_EFFECTS.get(), new CureEffects(
                    Artifacts.CONFIG.items.antidoteVessel.enabled,
                    Artifacts.CONFIG.items.antidoteVessel.maxEffectDuration
            ))
    );
    public static final Holder<Item> UNIVERSAL_ATTRACTOR = register("universal_attractor", builder -> builder
            .equipable()
            .component(ModDataComponents.PIGLIN_LOVED.get())
            .mobEffect(ModMobEffects.MAGNETISM, Artifacts.CONFIG.items.universalAttractor.magnetismLevel, Value.of(10), () -> EntityCondition.ALWAYS)
            .component(ModDataComponents.TOGGLE_KEY.get(), ToggleIdentifier.UNIVERSAL_ATTRACTOR)
    );
    public static final Holder<Item> CRYSTAL_HEART = register("crystal_heart", builder -> builder
            .equipable(SoundEvents.ARMOR_EQUIP_DIAMOND)
            .addAttributeModifier(Attributes.MAX_HEALTH, Artifacts.CONFIG.items.crystalHeart.healthBonus, AttributeModifier.Operation.ADD_VALUE)
    );
    public static final Holder<Item> HELIUM_FLAMINGO = register("helium_flamingo", builder -> builder
            .equipable(ModSoundEvents.POP)
            .component(ModDataComponents.SWIM_IN_AIR.get(), new SwimInAir(
                    Artifacts.CONFIG.items.heliumFlamingo.flightDuration,
                    Artifacts.CONFIG.items.heliumFlamingo.rechargeDuration,
                    Artifacts.CONFIG.items.heliumFlamingo.cooldown
            ))
    );
    public static final Holder<Item> CHORUS_TOTEM = register("chorus_totem", builder -> builder
            .equipable()
            .component(ModDataComponents.EQUIPABLE_TOTEM.get(), new EquipableTotem(
                    Artifacts.CONFIG.items.chorusTotem.enabled
            ))
            .delayedComponent(
                    DataComponents.DEATH_PROTECTION,
                    Artifacts.CONFIG.items.chorusTotem.enabled,
                    _ -> new DeathProtection(List.of(
                            new ClearAllStatusEffectsConsumeEffect(),
                            new TeleportRandomlyConsumeEffect(32),
                            new HealConsumeEffect(Artifacts.CONFIG.items.chorusTotem.healthRestored))
                    )
            )
    );
    public static final Holder<Item> WARP_DRIVE = register("warp_drive", builder -> builder
            .equipable()
            .component(ModDataComponents.ENDER_PEARL_HUNGER_COST.get(), new EnderPearlHungerCost(
                    Artifacts.CONFIG.items.warpDrive.enabled,
                    Artifacts.CONFIG.items.warpDrive.hungerCost,
                    Artifacts.CONFIG.items.warpDrive.cooldown
            ))
            .component(ModDataComponents.ENDER_PEARL_DAMAGE_IMMUNITY.get(), Artifacts.CONFIG.items.warpDrive.nullifyEnderPearlDamage)
    );

    // hands
    public static final Holder<Item> DIGGING_CLAWS = register("digging_claws", builder -> builder
            .equipable(SoundEvents.ARMOR_EQUIP_NETHERITE)
            .addAttributeModifier(Attributes.BLOCK_BREAK_SPEED, Artifacts.CONFIG.items.diggingClaws.blockBreakSpeedBonus, AttributeModifier.Operation.ADD_MULTIPLIED_BASE)
            .component(ModDataComponents.TOOL_TIER_UPGRADE.get(), new ToolTierUpgrade(Artifacts.CONFIG.items.diggingClaws.toolTier))
    );
    public static final Holder<Item> FERAL_CLAWS = register("feral_claws", builder -> builder
            .equipable(SoundEvents.ARMOR_EQUIP_NETHERITE)
            .addAttributeModifier(Attributes.ATTACK_SPEED, Artifacts.CONFIG.items.feralClaws.attackSpeedBonus, AttributeModifier.Operation.ADD_MULTIPLIED_BASE)
    );
    public static final Holder<Item> POWER_GLOVE = register("power_glove", builder -> builder
            .equipable()
            .addAttributeModifier(Attributes.ATTACK_DAMAGE, Artifacts.CONFIG.items.powerGlove.attackDamageBonus, AttributeModifier.Operation.ADD_VALUE)
    );
    public static final Holder<Item> FIRE_GAUNTLET = register("fire_gauntlet", builder -> builder
            .equipable(SoundEvents.ARMOR_EQUIP_IRON)
            .addAttributeModifier(ModAttributes.ATTACK_BURNING_DURATION, Artifacts.CONFIG.items.fireGauntlet.fireDuration, AttributeModifier.Operation.ADD_VALUE)
    );
    public static final Holder<Item> POCKET_PISTON = register("pocket_piston", builder -> builder
            .equipable(SoundEvents.PISTON_EXTEND)
            .addAttributeModifier(Attributes.ATTACK_KNOCKBACK, Artifacts.CONFIG.items.pocketPiston.attackKnockbackBonus, AttributeModifier.Operation.ADD_VALUE)
    );
    public static final Holder<Item> VAMPIRIC_GLOVE = register("vampiric_glove", builder -> builder
            .equipable()
            .component(ModDataComponents.DAMAGE_ABSORPTION.get(), new DamageAbsorption(
                    Artifacts.CONFIG.items.vampiricGlove.absorptionRatio,
                    Artifacts.CONFIG.items.vampiricGlove.absorptionChance,
                    Artifacts.CONFIG.items.vampiricGlove.maxHealingPerHit
            ))
    );
    public static final Holder<Item> GOLDEN_HOOK = register("golden_hook", builder -> builder
            .equipable()
            .addAttributeModifier(ModAttributes.ENTITY_EXPERIENCE, Artifacts.CONFIG.items.goldenHook.entityExperienceBonus, AttributeModifier.Operation.ADD_MULTIPLIED_BASE)
            .component(ModDataComponents.PIGLIN_LOVED.get())
    );
    public static final Holder<Item> ONION_RING = register("onion_ring", builder -> builder
            .equipable()
            .properties(properties -> properties.food(new FoodProperties.Builder().nutrition(2).build()))
            .component(ModDataComponents.POST_EATING_EFFECTS.get(), new CompositeAbility<>(
                    List.of(new PostEatingEffect(new MobEffectProvider(
                            MobEffects.HASTE,
                            Artifacts.CONFIG.items.onionRing.hasteLevel,
                            Artifacts.CONFIG.items.onionRing.hasteDurationPerFoodPoint,
                            Value.of(true), Value.of(true), EntityCondition.ALWAYS
                    )))
            ))
    );
    public static final Holder<Item> PICKAXE_HEATER = register("pickaxe_heater", builder -> builder
            .equipable(SoundEvents.ARMOR_EQUIP_IRON)
            .component(ModDataComponents.AUTO_SMELT.get(), Artifacts.CONFIG.items.pickaxeHeater.enabled)
    );
    public static final Holder<Item> WITHERED_BRACELET = register("withered_bracelet", builder -> builder
            .equipable()
            .component(ModDataComponents.ATTACK_EFFECTS.get(), new CompositeAbility<>(List.of(
                    new AttackEffect(
                            new MobEffectProvider(
                                    MobEffects.WITHER,
                                    Artifacts.CONFIG.items.witheredBracelet.witherLevel,
                                    Artifacts.CONFIG.items.witheredBracelet.witherDuration,
                                    Value.of(true), Value.of(true), EntityCondition.ALWAYS
                            ),
                            Artifacts.CONFIG.items.witheredBracelet.witherChance,
                            Artifacts.CONFIG.items.witheredBracelet.cooldown
                    )
            )))
    );

    // feet
    public static final Holder<Item> AQUA_DASHERS = register("aqua_dashers", builder -> builder
            .equipable()
            .component(ModDataComponents.FLUID_COLLISION.get(), new FluidCollision(Artifacts.CONFIG.items.aquaDashers.enabled,
                    Optional.empty(), EntityCondition.SPRINTING)
            )
    );
    public static final Holder<Item> BUNNY_HOPPERS = register("bunny_hoppers", builder -> builder
            .equipable()
            .addAttributeModifier(Attributes.JUMP_STRENGTH, Artifacts.CONFIG.items.bunnyHoppers.jumpStrengthBonus, AttributeModifier.Operation.ADD_MULTIPLIED_BASE)
            .addAttributeModifier(Attributes.FALL_DAMAGE_MULTIPLIER, Artifacts.CONFIG.items.bunnyHoppers.fallDamageMultiplier, AttributeModifier.Operation.ADD_MULTIPLIED_BASE)
            .addAttributeModifier(Attributes.SAFE_FALL_DISTANCE, Artifacts.CONFIG.items.bunnyHoppers.safeFallDistanceBonus, AttributeModifier.Operation.ADD_VALUE)
            .component(ModDataComponents.HURT_SOUND.get(), new HurtSound(BuiltInRegistries.SOUND_EVENT.wrapAsHolder(SoundEvents.RABBIT_HURT), Artifacts.CONFIG.items.bunnyHoppers.modifyHurtSounds))
    );
    public static final Holder<Item> KITTY_SLIPPERS = register("kitty_slippers", builder -> builder
            .equipable(SoundEvents.CAT_SOUNDS.get(CatSoundVariants.SoundSet.CLASSIC).adultSounds().ambientSound())
            .component(ModDataComponents.CREEPER_REPELLENT.get(), Artifacts.CONFIG.items.kittySlippers.repelCreepers)
            .component(ModDataComponents.PHANTOM_REPELLENT.get(), Artifacts.CONFIG.items.kittySlippers.repelPhantoms)
            .component(ModDataComponents.HURT_SOUND.get(), new HurtSound(SoundEvents.CAT_SOUNDS.get(CatSoundVariants.SoundSet.CLASSIC).adultSounds().hurtSound(), Artifacts.CONFIG.items.kittySlippers.modifyHurtSounds))
    );
    public static final Holder<Item> RUNNING_SHOES = register("running_shoes", builder -> builder
            .equipable()
            .addAttributeModifier(ModAttributes.SPRINTING_SPEED, Artifacts.CONFIG.items.runningShoes.sprintingSpeedBonus, AttributeModifier.Operation.ADD_MULTIPLIED_BASE)
            .addAttributeModifier(ModAttributes.SPRINTING_STEP_HEIGHT, Artifacts.CONFIG.items.runningShoes.sprintingStepHeightBonus, AttributeModifier.Operation.ADD_VALUE)
    );
    public static final Holder<Item> SNOWSHOES = register("snowshoes", builder -> builder
            .equipable()
            .component(ModDataComponents.WALK_ON_POWDER_SNOW.get(), Artifacts.CONFIG.items.snowshoes.allowWalkingOnPowderedSnow)
            .addAttributeModifier(ModAttributes.MOVEMENT_SPEED_ON_SNOW, Artifacts.CONFIG.items.snowshoes.movementSpeedOnSnowBonus, AttributeModifier.Operation.ADD_MULTIPLIED_BASE)
    );
    public static final Holder<Item> STEADFAST_SPIKES = register("steadfast_spikes", builder -> builder
            .equipable()
            .addAttributeModifier(Attributes.KNOCKBACK_RESISTANCE, Artifacts.CONFIG.items.steadfastSpikes.knockbackResistance, AttributeModifier.Operation.ADD_VALUE)
            .addAttributeModifier(ModAttributes.SLIP_RESISTANCE, Artifacts.CONFIG.items.steadfastSpikes.slipperinessReduction, AttributeModifier.Operation.ADD_VALUE)
    );
    public static final Holder<Item> FLIPPERS = register("flippers", builder -> builder
            .equipable()
            .addAttributeModifier(ModAttributes.SWIM_SPEED, Artifacts.CONFIG.items.flippers.swimSpeedBonus, AttributeModifier.Operation.ADD_MULTIPLIED_BASE)
    );
    public static final Holder<Item> ROOTED_BOOTS = register("rooted_boots", builder -> builder
            .equipable(SoundEvents.ARMOR_EQUIP_LEATHER)
            .component(ModDataComponents.REPLENISH_HUNGER_ON_GRASS.get(), new ReplenishHungerOnGrass(
                    Artifacts.CONFIG.items.rootedBoots.enabled,
                    Artifacts.CONFIG.items.rootedBoots.hungerReplenishingDuration
            ))
            .component(ModDataComponents.POST_EATING_PLANT_GROWTH.get(), Artifacts.CONFIG.items.rootedBoots.growPlantsAfterEating)
    );
    public static final Holder<Item> STRIDER_SHOES = register("strider_shoes", builder -> builder
            .equipable(SoundEvents.ARMOR_EQUIP_LEATHER)
            .component(ModDataComponents.FLUID_COLLISION.get(), new FluidCollision(Artifacts.CONFIG.items.striderShoes.enabled,
                    Optional.of(FluidTags.LAVA), EntityCondition.SNEAKING)
            ).component(ModDataComponents.DAMAGE_IMMUNITY.get(),
                    new DamageImmunity(Artifacts.CONFIG.items.striderShoes.cancelHotFloorDamage, ModTags.IS_HOT_FLOOR, EntityCondition.ALWAYS)
            )
    );

    // curio
    public static final Holder<Item> WHOOPEE_CUSHION = register("whoopee_cushion", builder -> builder
            .equipable(ModSoundEvents.FART)
            .addAttributeModifier(ModAttributes.FLATULENCE, Artifacts.CONFIG.items.whoopeeCushion.fartChance, AttributeModifier.Operation.ADD_VALUE)
    );

    private static Holder<Item> register(String name, Consumer<ArtifactProperties> consumer) {
        return register(name, Item::new, () -> {
            ArtifactProperties builder = new ArtifactProperties(name);
            consumer.accept(builder);
            return builder.build();
        });
    }

    private static Holder<Item> register(String name, Function<Item.Properties, ? extends Item> factory, Supplier<Item.Properties> properties) {
        return ITEMS.register(name, () -> factory.apply(properties.get().setId(Artifacts.key(Registries.ITEM, name))));
    }
}
