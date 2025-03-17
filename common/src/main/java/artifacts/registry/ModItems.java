package artifacts.registry;

import artifacts.Artifacts;
import artifacts.ability.*;
import artifacts.ability.mobeffect.*;
import artifacts.ability.retaliation.SetAttackersOnFireAbility;
import artifacts.ability.retaliation.StrikeAttackersWithLightningAbility;
import artifacts.ability.retaliation.ThornsAbility;
import artifacts.config.value.Value;
import artifacts.item.EverlastingFoodItem;
import artifacts.item.UmbrellaItem;
import artifacts.item.WearableArtifactItem;
import artifacts.platform.PlatformServices;
import dev.architectury.core.item.ArchitecturySpawnEggItem;
import dev.architectury.registry.CreativeTabRegistry;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;

@SuppressWarnings("UnstableApiUsage")
public class ModItems {

    public static final List<RegistryHolder<Item, ?>> ITEMS = new ArrayList<>();
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Artifacts.MOD_ID, Registries.CREATIVE_MODE_TAB);

    public static final RegistrySupplier<CreativeModeTab> CREATIVE_TAB = CREATIVE_MODE_TABS.register("main", () ->
            CreativeTabRegistry.create(
                    Component.translatable("%s.creative_tab".formatted(Artifacts.MOD_ID)),
                    () -> new ItemStack(ModItems.BUNNY_HOPPERS.value())
            )
    );

    public static final Holder<Item> MIMIC_SPAWN_EGG = register("mimic_spawn_egg", () -> new ArchitecturySpawnEggItem(ModEntityTypes.MIMIC, 0x805113, 0x212121, new Item.Properties().arch$tab(CREATIVE_TAB)));
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
            .addAbility(new CustomTooltipAbility(Component.translatable("artifacts.tooltip.item.novelty_drinking_hat")))
            .addAttributeModifier(ModAttributes.DRINKING_SPEED, Artifacts.CONFIG.items.noveltyDrinkingHatDrinkingSpeedBonus, AttributeModifier.Operation.ADD_MULTIPLIED_BASE)
            .addAttributeModifier(ModAttributes.EATING_SPEED, Artifacts.CONFIG.items.noveltyDrinkingHatEatingSpeedBonus, AttributeModifier.Operation.ADD_MULTIPLIED_BASE)
    );
    public static final Holder<Item> SNORKEL = wearableItem("snorkel", builder -> builder
            .addAbility(new LimitedWaterBreathingAbility(Artifacts.CONFIG.items.snorkelWaterBreathingDuration, Artifacts.CONFIG.items.snorkelIsInfinite))
    );
    public static final Holder<Item> NIGHT_VISION_GOGGLES = wearableItem("night_vision_goggles", builder -> builder
            .addAbility(new NightVisionAbility(Artifacts.CONFIG.items.nightVisionGogglesStrength))
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
            .addAbility(new PermanentMobEffectAbility(MobEffects.INVISIBILITY, Value.of(1), Artifacts.CONFIG.items.scarfOfInvisibilityEnabled))
    );
    public static final Holder<Item> CROSS_NECKLACE = wearableItem("cross_necklace", builder -> builder
            .equipSound(SoundEvents.ARMOR_EQUIP_DIAMOND)
            .addAbility(MakePiglinsNeutralAbility.INSTANCE)
            .addAbility(new ApplyCooldownAfterDamageAbility(Artifacts.CONFIG.items.crossNecklaceCooldown, Optional.empty()))
            .addAttributeModifier(ModAttributes.INVINCIBILITY_TICKS, Artifacts.CONFIG.items.crossNecklaceBonusInvincibilityTicks, AttributeModifier.Operation.ADD_VALUE, false)
    );
    public static final Holder<Item> PANIC_NECKLACE = wearableItem("panic_necklace", builder -> builder
            .equipSound(SoundEvents.ARMOR_EQUIP_DIAMOND)
            .addAbility(new ApplyMobEffectAfterDamageAbility(
                    MobEffects.MOVEMENT_SPEED,
                    Artifacts.CONFIG.items.panicNecklaceSpeedLevel,
                    Artifacts.CONFIG.items.panicNecklaceSpeedDuration,
                    Optional.empty()
            ))
            .addAbility(new ApplyCooldownAfterDamageAbility(
                    Artifacts.CONFIG.items.panicNecklaceCooldown,
                    Optional.empty()
            ))
    );
    public static final Holder<Item> SHOCK_PENDANT = wearableItem("shock_pendant", builder -> builder
            .addAbility(new StrikeAttackersWithLightningAbility(
                    Artifacts.CONFIG.items.shockPendantStrikeChance,
                    Artifacts.CONFIG.items.shockPendantCooldown
            ))
            .addAbility(new DamageImmunityAbility(Artifacts.CONFIG.items.shockPendantCancelLightningDamage, DamageTypeTags.IS_LIGHTNING))
    );
    public static final Holder<Item> FLAME_PENDANT = wearableItem("flame_pendant", builder -> builder
            .addAbility(new SetAttackersOnFireAbility(
                    Artifacts.CONFIG.items.flamePendantStrikeChance,
                    Artifacts.CONFIG.items.flamePendantCooldown,
                    Artifacts.CONFIG.items.flamePendantFireDuration,
                    Artifacts.CONFIG.items.flamePendantGrantFireResistance
            ))
    );
    public static final Holder<Item> THORN_PENDANT = wearableItem("thorn_pendant", builder -> builder
            .addAbility(new ThornsAbility(
                    Artifacts.CONFIG.items.thornPendantStrikeChance,
                    Artifacts.CONFIG.items.thornPendantCooldown,
                    Artifacts.CONFIG.items.thornPendantMinDamage,
                    Artifacts.CONFIG.items.thornPendantMaxDamage
            ))
    );
    public static final Holder<Item> CHARM_OF_SINKING = wearableItem("charm_of_sinking", builder -> builder
            .addAbility(new SimpleAbility(ModAbilities.SINKING::value, Artifacts.CONFIG.items.charmOfSinkingEnabled))
    );
    public static final Holder<Item> CHARM_OF_SHRINKING = wearableItem("charm_of_shrinking", builder -> builder
            .addAttributeModifier(Attributes.SCALE, Artifacts.CONFIG.items.charmOfShrinkingScaleModifier, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)
    );

    // belt
    public static final Holder<Item> CLOUD_IN_A_BOTTLE = wearableItem("cloud_in_a_bottle", builder -> builder
            .equipSound(SoundEvents.BOTTLE_FILL_DRAGONBREATH)
            .addAbility(new DoubleJumpAbility(
                    Artifacts.CONFIG.items.cloudInABottleEnabled,
                    Artifacts.CONFIG.items.cloudInABottleSprintJumpHorizontalVelocity,
                    Artifacts.CONFIG.items.cloudInABottleSprintJumpVerticalVelocity
            ))
            .addAttributeModifier(Attributes.SAFE_FALL_DISTANCE, Artifacts.CONFIG.items.cloudInABottleSafeFallDistanceBonus, AttributeModifier.Operation.ADD_VALUE)
    );
    public static final Holder<Item> OBSIDIAN_SKULL = wearableItem("obsidian_skull", builder -> builder
            .equipSound(SoundEvents.ARMOR_EQUIP_IRON)
            .addAbility(new ApplyMobEffectAfterDamageAbility(
                    MobEffects.FIRE_RESISTANCE,
                    Value.of(1),
                    Artifacts.CONFIG.items.obsidianSkullFireResistanceDuration,
                    Optional.of(DamageTypeTags.IS_FIRE)
            ))
            .addAbility(new ApplyCooldownAfterDamageAbility(
                    Artifacts.CONFIG.items.obsidianSkullCooldown,
                    Optional.of(DamageTypeTags.IS_FIRE)
            ))
    );
    public static final Holder<Item> ANTIDOTE_VESSEL = wearableItem("antidote_vessel", builder -> builder
            .equipSound(SoundEvents.BOTTLE_FILL)
            .addAbility(MakePiglinsNeutralAbility.INSTANCE)
            .addAbility(new RemoveBadEffectsAbility(
                    Artifacts.CONFIG.items.antidoteVesselEnabled,
                    Artifacts.CONFIG.items.antidoteVesselMaxEffectDuration
            ))
    );
    public static final Holder<Item> UNIVERSAL_ATTRACTOR = wearableItem("universal_attractor", builder -> builder
            .addAbility(MakePiglinsNeutralAbility.INSTANCE)
            .addAbility(new AttractItemsAbility(Artifacts.CONFIG.items.universalAttractorEnabled))
    );
    public static final Holder<Item> CRYSTAL_HEART = wearableItem("crystal_heart", builder -> builder
            .equipSound(SoundEvents.ARMOR_EQUIP_DIAMOND)
            .addAttributeModifier(Attributes.MAX_HEALTH, Artifacts.CONFIG.items.crystalHeartHealthBonus, AttributeModifier.Operation.ADD_VALUE)
    );
    public static final Holder<Item> HELIUM_FLAMINGO = wearableItem("helium_flamingo", builder -> builder
            .equipSound(ModSoundEvents.POP)
            .equipSoundPitch(0.7F)
            .addAbility(new SwimInAirAbility(
                    Artifacts.CONFIG.items.heliumFlamingoFlightDuration,
                    Artifacts.CONFIG.items.heliumFlamingoRechargeDuration
            ))
    );
    public static final Holder<Item> CHORUS_TOTEM = wearableItem("chorus_totem", builder -> builder
            .addAbility(new TeleportOnDeathAbility(
                    Artifacts.CONFIG.items.chorusTotemTeleportationChance,
                    Artifacts.CONFIG.items.chorusTotemHealthRestored,
                    Artifacts.CONFIG.items.chorusTotemCooldown,
                    Artifacts.CONFIG.items.chorusTotemConsumeOnUse
            ))
    );
    public static final Holder<Item> WARP_DRIVE = wearableItem("warp_drive", builder -> builder
            .addAbility(new EnderPearlsCostHungerAbility(
                    Artifacts.CONFIG.items.warpDriveEnabled,
                    Artifacts.CONFIG.items.warpDriveHungerCost,
                    Artifacts.CONFIG.items.warpDriveCooldown
            ))
            .addAbility(new SimpleAbility(
                    ModAbilities.NULLIFY_ENDER_PEARL_DAMAGE::value,
                    Artifacts.CONFIG.items.warpDriveNullifyEnderPearlDamage
            ))
    );

    // hands
    public static final Holder<Item> DIGGING_CLAWS = wearableItem("digging_claws", builder -> builder
            .equipSound(SoundEvents.ARMOR_EQUIP_NETHERITE)
            .addAttributeModifier(Attributes.BLOCK_BREAK_SPEED, Artifacts.CONFIG.items.diggingClawsBlockBreakSpeedBonus, AttributeModifier.Operation.ADD_MULTIPLIED_BASE)
            .addAbility(new UpgradeToolTierAbility(Artifacts.CONFIG.items.diggingClawsToolTier))
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
            .addAbility(new AttacksAbsorbDamageAbility(
                    Artifacts.CONFIG.items.vampiricGloveAbsorptionRatio,
                    Artifacts.CONFIG.items.vampiricGloveAbsorptionChance,
                    Artifacts.CONFIG.items.vampiricGloveMaxHealingPerHit
            ))
    );
    public static final Holder<Item> GOLDEN_HOOK = wearableItem("golden_hook", builder -> builder
            .addAttributeModifier(ModAttributes.ENTITY_EXPERIENCE, Artifacts.CONFIG.items.goldenHookEntityExperienceBonus, AttributeModifier.Operation.ADD_MULTIPLIED_BASE)
            .addAbility(MakePiglinsNeutralAbility.INSTANCE)
    );
    public static final Holder<Item> ONION_RING = wearableItem("onion_ring", builder -> builder
            .properties(properties -> properties.food(new FoodProperties.Builder().nutrition(2).build()))
            .addAbility(new ApplyMobEffectAfterEatingAbility(
                    MobEffects.DIG_SPEED,
                    Artifacts.CONFIG.items.onionRingHasteLevel,
                    Artifacts.CONFIG.items.onionRingHasteDurationPerFoodPoint
            ))
    );
    public static final Holder<Item> PICKAXE_HEATER = wearableItem("pickaxe_heater", builder -> builder
            .equipSound(SoundEvents.ARMOR_EQUIP_IRON)
            .addAbility(new SimpleAbility(ModAbilities.SMELT_ORES::value, Artifacts.CONFIG.items.pickaxeHeaterEnabled))
    );
    public static final Holder<Item> WITHERED_BRACELET = wearableItem("withered_bracelet", builder -> builder
            .addAbility(new AttacksInflictMobEffectAbility(
                    MobEffects.WITHER,
                    Artifacts.CONFIG.items.witheredBraceletWitherLevel,
                    Artifacts.CONFIG.items.witheredBraceletWitherDuration,
                    Artifacts.CONFIG.items.witheredBraceletCooldown,
                    Artifacts.CONFIG.items.witheredBraceletWitherChance
            ))
    );

    // feet
    public static final Holder<Item> AQUA_DASHERS = wearableItem("aqua_dashers", builder -> builder
            .addAbility(new CollideWithFluidsAbility(ModAbilities.SPRINT_ON_FLUIDS::value, Artifacts.CONFIG.items.aquaDashersEnabled, Optional.empty()))
    );
    public static final Holder<Item> BUNNY_HOPPERS = wearableItem("bunny_hoppers", builder -> builder
            .addAttributeModifier(Attributes.JUMP_STRENGTH, Artifacts.CONFIG.items.bunnyHoppersJumpStrengthBonus, AttributeModifier.Operation.ADD_MULTIPLIED_BASE)
            .addAttributeModifier(Attributes.FALL_DAMAGE_MULTIPLIER, Artifacts.CONFIG.items.bunnyHoppersFallDamageMultiplier, AttributeModifier.Operation.ADD_MULTIPLIED_BASE)
            .addAttributeModifier(Attributes.SAFE_FALL_DISTANCE, Artifacts.CONFIG.items.bunnyHoppersSafeFallDistanceBonus, AttributeModifier.Operation.ADD_VALUE)
            .addAbility(new ModifyHurtSoundAbility(BuiltInRegistries.SOUND_EVENT.wrapAsHolder(SoundEvents.RABBIT_HURT)))
    );
    public static final Holder<Item> KITTY_SLIPPERS = wearableItem("kitty_slippers", builder -> builder
            .equipSound(SoundEvents.CAT_AMBIENT)
            .addAbility(new SimpleAbility(ModAbilities.SCARE_CREEPERS::value, Artifacts.CONFIG.items.kittySlippersEnabled))
            .addAbility(new ModifyHurtSoundAbility(BuiltInRegistries.SOUND_EVENT.wrapAsHolder(SoundEvents.CAT_HURT)))
    );
    public static final Holder<Item> RUNNING_SHOES = wearableItem("running_shoes", builder -> builder
            .addAttributeModifier(ModAttributes.SPRINTING_SPEED, Artifacts.CONFIG.items.runningShoesSprintingSpeedBonus, AttributeModifier.Operation.ADD_MULTIPLIED_BASE)
            .addAttributeModifier(ModAttributes.SPRINTING_STEP_HEIGHT, Artifacts.CONFIG.items.runningShoesSprintingStepHeightBonus, AttributeModifier.Operation.ADD_VALUE)
    );
    public static final Holder<Item> SNOWSHOES = wearableItem("snowshoes", builder -> builder
            .addAbility(new SimpleAbility(ModAbilities.WALK_ON_POWDER_SNOW::value, Artifacts.CONFIG.items.snowshoesAllowWalkingOnPowderedSnow))
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
            .addAbility(new ReplenishHungerOnGrassAbility(
                    Artifacts.CONFIG.items.rootedBootsEnabled,
                    Artifacts.CONFIG.items.rootedBootsHungerReplenishingDuration
            ))
            .addAbility(new SimpleAbility(ModAbilities.GROW_PLANTS_AFTER_EATING::value, Artifacts.CONFIG.items.rootedBootsGrowPlantsAfterEating))
    );
    public static final Holder<Item> STRIDER_SHOES = wearableItem("strider_shoes", builder -> builder
            .equipSound(SoundEvents.ARMOR_EQUIP_LEATHER)
            .addAbility(new CollideWithFluidsAbility(ModAbilities.SNEAK_ON_FLUIDS::value, Artifacts.CONFIG.items.striderShoesEnabled, Optional.of(FluidTags.LAVA)))
            .addAbility(new DamageImmunityAbility(Artifacts.CONFIG.items.striderShoesCancelHotFloorDamage, ModTags.IS_HOT_FLOOR))
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
            builder.properties(properties -> properties.component(ModDataComponents.COSMETICS_ENABLED.get(), true));
            return builder.build();
        });
    }

    private static Holder<Item> register(String name, Supplier<? extends Item> supplier) {
        RegistryHolder<Item, ?> holder = new RegistryHolder<>(Artifacts.key(Registries.ITEM, name), supplier);
        ITEMS.add(holder);
        return holder;
    }
}
