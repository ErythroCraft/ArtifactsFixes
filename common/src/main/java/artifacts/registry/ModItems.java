package artifacts.registry;

import artifacts.Artifacts;
import artifacts.ability.*;
import artifacts.ability.mobeffect.*;
import artifacts.ability.retaliation.SetAttackersOnFireAbility;
import artifacts.ability.retaliation.StrikeAttackersWithLightningAbility;
import artifacts.ability.retaliation.ThornsAbility;
import artifacts.component.ToggleIdentifier;
import artifacts.config.value.Value;
import artifacts.item.EverlastingFoodItem;
import artifacts.item.UmbrellaItem;
import artifacts.item.WearableArtifactItem;
import artifacts.platform.PlatformServices;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantments;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class ModItems {

    public static final Register<Item> ITEMS = PlatformServices.platformHelper.createRegister(Registries.ITEM);
    public static final Register<CreativeModeTab> CREATIVE_MODE_TABS = PlatformServices.platformHelper.createRegister(Registries.CREATIVE_MODE_TAB);

    static {
        @SuppressWarnings({"ConstantConditions", "unused"})
        RegistryHolder<CreativeModeTab, CreativeModeTab> tab = CREATIVE_MODE_TABS.register("main", () -> new CreativeModeTab.Builder(null, 0)
                .title(Component.translatable("%s.creative_tab".formatted(Artifacts.MOD_ID)))
                .icon(() -> new ItemStack(ModItems.BUNNY_HOPPERS.value()))
                .displayItems((parameters, output) -> ITEMS.forEach(output::accept)
                ).build()
        );
    }

    public static final Holder<Item> MIMIC_SPAWN_EGG = register("mimic_spawn_egg", () -> PlatformServices.platformHelper.createMimicSpawnEgg(new Item.Properties()));
    public static final Holder<Item> UMBRELLA = register("umbrella", UmbrellaItem::new);
    public static final Holder<Item> EVERLASTING_BEEF = register("everlasting_beef", () -> new EverlastingFoodItem(new FoodProperties.Builder().nutrition(3).saturationModifier(0.3F).build(), Artifacts.CONFIG.items.everlastingBeefCooldown, Artifacts.CONFIG.items.everlastingBeefEnabled));
    public static final Holder<Item> ETERNAL_STEAK = register("eternal_steak", () -> new EverlastingFoodItem(new FoodProperties.Builder().nutrition(8).saturationModifier(0.8F).build(), Artifacts.CONFIG.items.eternalSteakCooldown, Artifacts.CONFIG.items.eternalSteakEnabled));

    // head
    public static final Holder<Item> PLASTIC_DRINKING_HAT = wearableItem("plastic_drinking_hat", builder -> builder
            .equipSound(SoundEvents.BOTTLE_FILL)
            .addAttributeModifier(ModAttributes.DRINKING_SPEED, Artifacts.CONFIG.items.plasticDrinkingHatDrinkingSpeedBonus, AttributeModifier.Operation.ADD_MULTIPLIED_BASE)
            .addAttributeModifier(ModAttributes.EATING_SPEED, Artifacts.CONFIG.items.plasticDrinkingHatEatingSpeedBonus, AttributeModifier.Operation.ADD_MULTIPLIED_BASE)
    );
    public static final Holder<Item> NOVELTY_DRINKING_HAT = wearableItem("novelty_drinking_hat", builder -> builder
            .equipSound(SoundEvents.BOTTLE_FILL)
            .component(ModDataComponents.CUSTOM_TOOLTIP.get(),
                    new CustomTooltipAbility(Component.translatable("artifacts.tooltip.item.novelty_drinking_hat"))
            )
            .addAttributeModifier(ModAttributes.DRINKING_SPEED, Artifacts.CONFIG.items.noveltyDrinkingHatDrinkingSpeedBonus, AttributeModifier.Operation.ADD_MULTIPLIED_BASE)
            .addAttributeModifier(ModAttributes.EATING_SPEED, Artifacts.CONFIG.items.noveltyDrinkingHatEatingSpeedBonus, AttributeModifier.Operation.ADD_MULTIPLIED_BASE)
    );
    public static final Holder<Item> SNORKEL = wearableItem("snorkel", builder -> builder
            .component(ModDataComponents.LIMITED_WATER_BREATHING.get(), new LimitedWaterBreathingAbility(Artifacts.CONFIG.items.snorkelWaterBreathingDuration, Artifacts.CONFIG.items.snorkelIsInfinite))
    );
    public static final Holder<Item> NIGHT_VISION_GOGGLES = wearableItem("night_vision_goggles", builder -> builder
            .component(ModDataComponents.NIGHT_VISION.get(), new NightVisionAbility(Artifacts.CONFIG.items.nightVisionGogglesStrength))
            .component(ModDataComponents.TOGGLE_KEY.get(), ToggleIdentifier.NIGHT_VISION_GOGGLES)
    );
    public static final Holder<Item> VILLAGER_HAT = wearableItem("villager_hat", builder -> builder
            .addAttributeModifier(ModAttributes.VILLAGER_REPUTATION, Artifacts.CONFIG.items.villagerHatReputationBonus, AttributeModifier.Operation.ADD_VALUE)
    );
    public static final Holder<Item> SUPERSTITIOUS_HAT = wearableItem("superstitious_hat", builder -> builder
            .increasesEnchantment(Enchantments.LOOTING, Artifacts.CONFIG.items.superstitiousHatLootingLevelBonus)
    );
    public static final Holder<Item> COWBOY_HAT = wearableItem("cowboy_hat", builder -> builder
            .equipSound(SoundEvents.ARMOR_EQUIP_LEATHER)
            .addAttributeModifier(ModAttributes.MOUNT_SPEED, Artifacts.CONFIG.items.cowboyHatMountSpeedBonus, AttributeModifier.Operation.ADD_MULTIPLIED_BASE)
    );
    public static final Holder<Item> ANGLERS_HAT = wearableItem("anglers_hat", builder -> builder
            .equipSound(SoundEvents.ARMOR_EQUIP_LEATHER)
            .increasesEnchantment(Enchantments.LUCK_OF_THE_SEA, Artifacts.CONFIG.items.anglersHatLuckOfTheSeaLevelBonus)
            .increasesEnchantment(Enchantments.LURE, Artifacts.CONFIG.items.anglersHatLureLevelBonus)
    );

    // necklace
    public static final Holder<Item> LUCKY_SCARF = wearableItem("lucky_scarf", builder -> builder
            .increasesEnchantment(Enchantments.FORTUNE, Artifacts.CONFIG.items.luckScarfFortuneBonus)
    );
    public static final Holder<Item> SCARF_OF_INVISIBILITY = wearableItem("scarf_of_invisibility", builder -> builder
            .component(ModDataComponents.MOB_EFFECT.get(), new PermanentMobEffectAbility(MobEffects.INVISIBILITY, Value.of(1), Artifacts.CONFIG.items.scarfOfInvisibilityEnabled))
            .component(ModDataComponents.TOGGLE_KEY.get(), ToggleIdentifier.SCARF_OF_INVISIBILITY)
    );
    public static final Holder<Item> CROSS_NECKLACE = wearableItem("cross_necklace", builder -> builder
            .equipSound(SoundEvents.ARMOR_EQUIP_DIAMOND)
            .component(ModDataComponents.MAKE_PIGLINS_NEUTRAL.get(), MakePiglinsNeutralAbility.INSTANCE)
            .component(ModDataComponents.APPLY_COOLDOWN_AFTER_DAMAGE.get(), new ApplyCooldownAfterDamageAbility(Artifacts.CONFIG.items.crossNecklaceCooldown, Optional.empty()))
            .addAttributeModifier(ModAttributes.INVINCIBILITY_TICKS, Artifacts.CONFIG.items.crossNecklaceBonusInvincibilityTicks, AttributeModifier.Operation.ADD_VALUE, false)
    );
    public static final Holder<Item> PANIC_NECKLACE = wearableItem("panic_necklace", builder -> builder
            .equipSound(SoundEvents.ARMOR_EQUIP_DIAMOND)
            .component(ModDataComponents.APPLY_MOB_EFFECT_AFTER_DAMAGE.get(), new ApplyMobEffectAfterDamageAbility(
                    MobEffects.MOVEMENT_SPEED,
                    Artifacts.CONFIG.items.panicNecklaceSpeedLevel,
                    Artifacts.CONFIG.items.panicNecklaceSpeedDuration,
                    Optional.empty()
            ))
            .component(ModDataComponents.APPLY_COOLDOWN_AFTER_DAMAGE.get(), new ApplyCooldownAfterDamageAbility(
                    Artifacts.CONFIG.items.panicNecklaceCooldown,
                    Optional.empty()
            ))
    );
    public static final Holder<Item> SHOCK_PENDANT = wearableItem("shock_pendant", builder -> builder
            .component(ModDataComponents.STRIKE_ATTACKERS_WITH_LIGHTNING.get(), new StrikeAttackersWithLightningAbility(
                    Artifacts.CONFIG.items.shockPendantStrikeChance,
                    Artifacts.CONFIG.items.shockPendantCooldown
            )).component(ModDataComponents.DAMAGE_IMMUNITY.get(),
                    new DamageImmunityAbility(Artifacts.CONFIG.items.shockPendantCancelLightningDamage, DamageTypeTags.IS_LIGHTNING)
            )
    );
    public static final Holder<Item> FLAME_PENDANT = wearableItem("flame_pendant", builder -> builder
            .component(ModDataComponents.SET_ATTACKERS_ON_FIRE.get(), new SetAttackersOnFireAbility(
                    Artifacts.CONFIG.items.flamePendantStrikeChance,
                    Artifacts.CONFIG.items.flamePendantCooldown,
                    Artifacts.CONFIG.items.flamePendantFireDuration,
                    Artifacts.CONFIG.items.flamePendantGrantFireResistance
            ))
    );
    public static final Holder<Item> THORN_PENDANT = wearableItem("thorn_pendant", builder -> builder
            .component(ModDataComponents.THORNS.get(), new ThornsAbility(
                    Artifacts.CONFIG.items.thornPendantStrikeChance,
                    Artifacts.CONFIG.items.thornPendantCooldown,
                    Artifacts.CONFIG.items.thornPendantMinDamage,
                    Artifacts.CONFIG.items.thornPendantMaxDamage
            ))
    );
    public static final Holder<Item> CHARM_OF_SINKING = wearableItem("charm_of_sinking", builder -> builder
            .component(ModDataComponents.SINKING.get(), new SimpleAbility(Artifacts.CONFIG.items.charmOfSinkingEnabled))
            .component(ModDataComponents.TOGGLE_KEY.get(), ToggleIdentifier.CHARM_OF_SINKING)
    );
    public static final Holder<Item> CHARM_OF_SHRINKING = wearableItem("charm_of_shrinking", builder -> builder
            .addAttributeModifier(Attributes.SCALE, Artifacts.CONFIG.items.charmOfShrinkingScaleModifier, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)
            .component(ModDataComponents.TOGGLE_KEY.get(), ToggleIdentifier.CHARM_OF_SHRINKING)
    );

    // belt
    public static final Holder<Item> CLOUD_IN_A_BOTTLE = wearableItem("cloud_in_a_bottle", builder -> builder
            .equipSound(SoundEvents.BOTTLE_FILL_DRAGONBREATH)
            .component(ModDataComponents.DOUBLE_JUMP.get(), new DoubleJumpAbility(
                    Artifacts.CONFIG.items.cloudInABottleEnabled,
                    Artifacts.CONFIG.items.cloudInABottleSprintJumpHorizontalVelocity,
                    Artifacts.CONFIG.items.cloudInABottleSprintJumpVerticalVelocity
            ))
            .addAttributeModifier(Attributes.SAFE_FALL_DISTANCE, Artifacts.CONFIG.items.cloudInABottleSafeFallDistanceBonus, AttributeModifier.Operation.ADD_VALUE)
    );
    public static final Holder<Item> OBSIDIAN_SKULL = wearableItem("obsidian_skull", builder -> builder
            .equipSound(SoundEvents.ARMOR_EQUIP_IRON)
            .component(ModDataComponents.APPLY_MOB_EFFECT_AFTER_DAMAGE.get(), new ApplyMobEffectAfterDamageAbility(
                    MobEffects.FIRE_RESISTANCE,
                    Value.of(1),
                    Artifacts.CONFIG.items.obsidianSkullFireResistanceDuration,
                    Optional.of(DamageTypeTags.IS_FIRE)
            ))
            .component(ModDataComponents.APPLY_COOLDOWN_AFTER_DAMAGE.get(), new ApplyCooldownAfterDamageAbility(
                    Artifacts.CONFIG.items.obsidianSkullCooldown,
                    Optional.of(DamageTypeTags.IS_FIRE)
            ))
    );
    public static final Holder<Item> ANTIDOTE_VESSEL = wearableItem("antidote_vessel", builder -> builder
            .equipSound(SoundEvents.BOTTLE_FILL)
            .component(ModDataComponents.MAKE_PIGLINS_NEUTRAL.get(), MakePiglinsNeutralAbility.INSTANCE)
            .component(ModDataComponents.REMOVE_BAD_EFFECTS.get(), new RemoveBadEffectsAbility(
                    Artifacts.CONFIG.items.antidoteVesselEnabled,
                    Artifacts.CONFIG.items.antidoteVesselMaxEffectDuration
            ))
    );
    public static final Holder<Item> UNIVERSAL_ATTRACTOR = wearableItem("universal_attractor", builder -> builder
            .component(ModDataComponents.MAKE_PIGLINS_NEUTRAL.get(), MakePiglinsNeutralAbility.INSTANCE)
            .component(ModDataComponents.MOB_EFFECT.get(), new PermanentMobEffectAbility(
                    ModMobEffects.MAGNETISM, Value.of(1), Artifacts.CONFIG.items.universalAttractorEnabled
            ))
            .component(ModDataComponents.TOGGLE_KEY.get(), ToggleIdentifier.UNIVERSAL_ATTRACTOR)
    );
    public static final Holder<Item> CRYSTAL_HEART = wearableItem("crystal_heart", builder -> builder
            .equipSound(SoundEvents.ARMOR_EQUIP_DIAMOND)
            .addAttributeModifier(Attributes.MAX_HEALTH, Artifacts.CONFIG.items.crystalHeartHealthBonus, AttributeModifier.Operation.ADD_VALUE)
    );
    public static final Holder<Item> HELIUM_FLAMINGO = wearableItem("helium_flamingo", builder -> builder
            .equipSound(ModSoundEvents.POP)
            .equipSoundPitch(0.7F)
            .component(ModDataComponents.SWIM_IN_AIR.get(), new SwimInAirAbility(
                    Artifacts.CONFIG.items.heliumFlamingoFlightDuration,
                    Artifacts.CONFIG.items.heliumFlamingoRechargeDuration
            ))
    );
    public static final Holder<Item> CHORUS_TOTEM = wearableItem("chorus_totem", builder -> builder
            .component(ModDataComponents.TELEPORT_ON_DEATH.get(), new TeleportOnDeathAbility(
                    Artifacts.CONFIG.items.chorusTotemTeleportationChance,
                    Artifacts.CONFIG.items.chorusTotemHealthRestored,
                    Artifacts.CONFIG.items.chorusTotemCooldown,
                    Artifacts.CONFIG.items.chorusTotemConsumeOnUse
            ))
    );
    public static final Holder<Item> WARP_DRIVE = wearableItem("warp_drive", builder -> builder
            .component(ModDataComponents.ENDER_PEARLS_COST_HUNGER.get(), new EnderPearlsCostHungerAbility(
                    Artifacts.CONFIG.items.warpDriveEnabled,
                    Artifacts.CONFIG.items.warpDriveHungerCost,
                    Artifacts.CONFIG.items.warpDriveCooldown
            ))
            .component(ModDataComponents.NULLIFY_ENDER_PEARL_DAMAGE.get(),
                    new SimpleAbility(Artifacts.CONFIG.items.warpDriveNullifyEnderPearlDamage)
            )
    );

    // hands
    public static final Holder<Item> DIGGING_CLAWS = wearableItem("digging_claws", builder -> builder
            .equipSound(SoundEvents.ARMOR_EQUIP_NETHERITE)
            .addAttributeModifier(Attributes.BLOCK_BREAK_SPEED, Artifacts.CONFIG.items.diggingClawsBlockBreakSpeedBonus, AttributeModifier.Operation.ADD_MULTIPLIED_BASE)
            .component(ModDataComponents.UPGRADE_TOOL_TIER.get(), new UpgradeToolTierAbility(Artifacts.CONFIG.items.diggingClawsToolTier))
    );
    public static final Holder<Item> FERAL_CLAWS = wearableItem("feral_claws", builder -> builder
            .equipSound(SoundEvents.ARMOR_EQUIP_NETHERITE)
            .addAttributeModifier(Attributes.ATTACK_SPEED, Artifacts.CONFIG.items.feralClawsAttackSpeedBonus, AttributeModifier.Operation.ADD_MULTIPLIED_BASE)
    );
    public static final Holder<Item> POWER_GLOVE = wearableItem("power_glove", builder -> builder
            .addAttributeModifier(Attributes.ATTACK_DAMAGE, Artifacts.CONFIG.items.powerGloveAttackDamageBonus, AttributeModifier.Operation.ADD_VALUE)
    );
    public static final Holder<Item> FIRE_GAUNTLET = wearableItem("fire_gauntlet", builder -> builder
            .equipSound(SoundEvents.ARMOR_EQUIP_IRON)
            .addAttributeModifier(ModAttributes.ATTACK_BURNING_DURATION, Artifacts.CONFIG.items.fireGauntletFireDuration, AttributeModifier.Operation.ADD_VALUE)
    );
    public static final Holder<Item> POCKET_PISTON = wearableItem("pocket_piston", builder -> builder
            .equipSound(SoundEvents.PISTON_EXTEND)
            .addAttributeModifier(Attributes.ATTACK_KNOCKBACK, Artifacts.CONFIG.items.pocketPistonAttackKnockbackBonus, AttributeModifier.Operation.ADD_VALUE)
    );
    public static final Holder<Item> VAMPIRIC_GLOVE = wearableItem("vampiric_glove", builder -> builder
            .component(ModDataComponents.ATTACKS_ABSORB_DAMAGE.get(), new AttacksAbsorbDamageAbility(
                    Artifacts.CONFIG.items.vampiricGloveAbsorptionRatio,
                    Artifacts.CONFIG.items.vampiricGloveAbsorptionChance,
                    Artifacts.CONFIG.items.vampiricGloveMaxHealingPerHit
            ))
    );
    public static final Holder<Item> GOLDEN_HOOK = wearableItem("golden_hook", builder -> builder
            .addAttributeModifier(ModAttributes.ENTITY_EXPERIENCE, Artifacts.CONFIG.items.goldenHookEntityExperienceBonus, AttributeModifier.Operation.ADD_MULTIPLIED_BASE)
            .component(ModDataComponents.MAKE_PIGLINS_NEUTRAL.get(), MakePiglinsNeutralAbility.INSTANCE)
    );
    public static final Holder<Item> ONION_RING = wearableItem("onion_ring", builder -> builder
            .properties(properties -> properties.food(new FoodProperties.Builder().nutrition(2).build()))
            .component(ModDataComponents.APPLY_MOB_EFFECT_AFTER_EATING.get(), new ApplyMobEffectAfterEatingAbility(
                    MobEffects.DIG_SPEED,
                    Artifacts.CONFIG.items.onionRingHasteLevel,
                    Artifacts.CONFIG.items.onionRingHasteDurationPerFoodPoint
            ))
    );
    public static final Holder<Item> PICKAXE_HEATER = wearableItem("pickaxe_heater", builder -> builder
            .equipSound(SoundEvents.ARMOR_EQUIP_IRON)
            .component(ModDataComponents.SMELT_ORES.get(), new SimpleAbility(Artifacts.CONFIG.items.pickaxeHeaterEnabled))
    );
    public static final Holder<Item> WITHERED_BRACELET = wearableItem("withered_bracelet", builder -> builder
            .component(ModDataComponents.ATTACKS_INFLICT_MOB_EFFECT.get(), new AttacksInflictMobEffectAbility(
                    MobEffects.WITHER,
                    Artifacts.CONFIG.items.witheredBraceletWitherLevel,
                    Artifacts.CONFIG.items.witheredBraceletWitherDuration,
                    Artifacts.CONFIG.items.witheredBraceletCooldown,
                    Artifacts.CONFIG.items.witheredBraceletWitherChance
            ))
    );

    // feet
    public static final Holder<Item> AQUA_DASHERS = wearableItem("aqua_dashers", builder -> builder
            .component(
                    ModDataComponents.SPRINT_ON_FLUIDS.get(),
                    new CollideWithFluidsAbility(Artifacts.CONFIG.items.aquaDashersEnabled, Optional.empty(), Optional.empty())
            )
    );
    public static final Holder<Item> BUNNY_HOPPERS = wearableItem("bunny_hoppers", builder -> builder
            .addAttributeModifier(Attributes.JUMP_STRENGTH, Artifacts.CONFIG.items.bunnyHoppersJumpStrengthBonus, AttributeModifier.Operation.ADD_MULTIPLIED_BASE)
            .addAttributeModifier(Attributes.FALL_DAMAGE_MULTIPLIER, Artifacts.CONFIG.items.bunnyHoppersFallDamageMultiplier, AttributeModifier.Operation.ADD_MULTIPLIED_BASE)
            .addAttributeModifier(Attributes.SAFE_FALL_DISTANCE, Artifacts.CONFIG.items.bunnyHoppersSafeFallDistanceBonus, AttributeModifier.Operation.ADD_VALUE)
            .component(ModDataComponents.MODIFY_HURT_SOUND.get(), new ModifyHurtSoundAbility(BuiltInRegistries.SOUND_EVENT.wrapAsHolder(SoundEvents.RABBIT_HURT)))
    );
    public static final Holder<Item> KITTY_SLIPPERS = wearableItem("kitty_slippers", builder -> builder
            .equipSound(SoundEvents.CAT_AMBIENT)
            .component(ModDataComponents.SCARE_CREEPERS.get(),
                    new SimpleAbility(Artifacts.CONFIG.items.kittySlippersEnabled)
            )
            .component(ModDataComponents.MODIFY_HURT_SOUND.get(), new ModifyHurtSoundAbility(BuiltInRegistries.SOUND_EVENT.wrapAsHolder(SoundEvents.CAT_HURT)))
    );
    public static final Holder<Item> RUNNING_SHOES = wearableItem("running_shoes", builder -> builder
            .addAttributeModifier(ModAttributes.SPRINTING_SPEED, Artifacts.CONFIG.items.runningShoesSprintingSpeedBonus, AttributeModifier.Operation.ADD_MULTIPLIED_BASE)
            .addAttributeModifier(ModAttributes.SPRINTING_STEP_HEIGHT, Artifacts.CONFIG.items.runningShoesSprintingStepHeightBonus, AttributeModifier.Operation.ADD_VALUE)
    );
    public static final Holder<Item> SNOWSHOES = wearableItem("snowshoes", builder -> builder
            .component(ModDataComponents.WALK_ON_POWDER_SNOW.get(),
                    new SimpleAbility(Artifacts.CONFIG.items.snowshoesAllowWalkingOnPowderedSnow)
            )
            .addAttributeModifier(ModAttributes.MOVEMENT_SPEED_ON_SNOW, Artifacts.CONFIG.items.snowshoesMovementSpeedOnSnowBonus, AttributeModifier.Operation.ADD_MULTIPLIED_BASE)
    );
    public static final Holder<Item> STEADFAST_SPIKES = wearableItem("steadfast_spikes", builder -> builder
            .addAttributeModifier(Attributes.KNOCKBACK_RESISTANCE, Artifacts.CONFIG.items.steadfastSpikesKnockbackResistance, AttributeModifier.Operation.ADD_VALUE)
            .addAttributeModifier(ModAttributes.SLIP_RESISTANCE, Artifacts.CONFIG.items.steadfastSpikesSlipperinessReduction, AttributeModifier.Operation.ADD_VALUE)
    );
    public static final Holder<Item> FLIPPERS = wearableItem("flippers", builder -> builder
            .addAttributeModifier(ModAttributes.SWIM_SPEED, Artifacts.CONFIG.items.flippersSwimSpeedBonus, AttributeModifier.Operation.ADD_MULTIPLIED_BASE)
    );
    public static final Holder<Item> ROOTED_BOOTS = wearableItem("rooted_boots", builder -> builder
            .equipSound(SoundEvents.ARMOR_EQUIP_LEATHER)
            .component(ModDataComponents.REPLENISH_HUNGER_ON_GRASS.get(), new ReplenishHungerOnGrassAbility(
                    Artifacts.CONFIG.items.rootedBootsEnabled,
                    Artifacts.CONFIG.items.rootedBootsHungerReplenishingDuration
            ))
            .component(ModDataComponents.GROW_PLANTS_AFTER_EATING.get(),
                    new SimpleAbility(Artifacts.CONFIG.items.rootedBootsGrowPlantsAfterEating)
            )
    );
    public static final Holder<Item> STRIDER_SHOES = wearableItem("strider_shoes", builder -> builder
            .equipSound(SoundEvents.ARMOR_EQUIP_LEATHER)
            .component(ModDataComponents.SNEAK_ON_FLUIDS.get(),
                    new CollideWithFluidsAbility(Artifacts.CONFIG.items.striderShoesEnabled, Optional.of(FluidTags.LAVA), Optional.empty())
            ).component(ModDataComponents.DAMAGE_IMMUNITY.get(),
                    new DamageImmunityAbility(Artifacts.CONFIG.items.striderShoesCancelHotFloorDamage, ModTags.IS_HOT_FLOOR)
            )
    );

    // curio
    public static final Holder<Item> WHOOPEE_CUSHION = wearableItem("whoopee_cushion", builder -> builder
            .equipSound(ModSoundEvents.FART)
            .addAttributeModifier(ModAttributes.FLATULENCE, Artifacts.CONFIG.items.whoopeeCushionFartChance, AttributeModifier.Operation.ADD_VALUE)
    );

    private static Holder<Item> wearableItem(String name, Consumer<WearableArtifactItem.Builder> consumer) {
        return register(name, () -> {
            WearableArtifactItem.Builder builder = new WearableArtifactItem.Builder(name);
            consumer.accept(builder);
            return builder.build();
        });
    }

    private static Holder<Item> register(String name, Supplier<? extends Item> supplier) {
        return ITEMS.register(name, supplier);
    }
}
