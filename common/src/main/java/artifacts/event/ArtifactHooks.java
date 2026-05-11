package artifacts.event;

import artifacts.attribute.DynamicAttributeModifier;
import artifacts.component.DamageOnBlockMined;
import artifacts.component.DamageOnHurt;
import artifacts.component.DamageOverTime;
import artifacts.component.SwimData;
import artifacts.component.ability.EquipmentAbility;
import artifacts.component.ability.PostDamageCooldown;
import artifacts.component.ability.ToolTierUpgrade;
import artifacts.component.ability.mobeffect.AttackEffect;
import artifacts.component.ability.mobeffect.PostDamageEffect;
import artifacts.equipment.EquipmentHelper;
import artifacts.extensions.ability.LivingEntityExtensions;
import artifacts.item.UmbrellaHelper;
import artifacts.mixin.accessors.MobAccessor;
import artifacts.platform.PlatformServices;
import artifacts.registry.*;
import artifacts.util.DamageSourceHelper;
import artifacts.util.ItemDamageUtil;
import be.florens.expandability.api.EventResult;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.BoneMealItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUseAnimation;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.item.crafting.SmeltingRecipe;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class ArtifactHooks {

    public static void livingUpdate(LivingEntity entity) {
        if (entity instanceof Player player) {
            SwimData swimData = PlatformServices.getPlatformHelper().getSwimData(entity);
            if (swimData != null) {
                swimData.update(player);
            }
        }
        onItemTick(entity);
        DynamicAttributeModifier.tickModifiers(entity);
        if (!entity.onGround()) {
            UmbrellaHelper.onLivingUpdate(entity);
        }
        DamageOverTime.onLivingUpdate(entity);
    }

    // Called just before the entity's health is modified
    public static void beforeLivingDamaged(LivingEntity entity, DamageSource source, float newHealth) {
        // clamp the entity's new health in case the damage dealt exceeds the remaining hp
        float damageDealt = entity.getHealth() - Math.max(0, newHealth);
        boolean isKillingBlow = newHealth <= 0;

        // abilities & effects
        absorbDamage(entity, source, damageDealt);
        PostDamageEffect.onLivingDamaged(entity, source);
        // item damage
        DamageOnHurt.onLivingDamaged(entity, source);
        damageItemsWhenAttacking(entity, source, isKillingBlow);
        // cooldowns
        PostDamageCooldown.onLivingDamaged(entity, source);
    }

    public static void onItemChanged(LivingEntity entity, ItemStack oldStack, ItemStack newStack) {
        if (entity.level().isClientSide() || ItemStack.matches(oldStack, newStack)) {
            return;
        }

        boolean wasToggledOff = !ItemDamageUtil.isDisabledOrBroken(oldStack)
                && ItemDamageUtil.isDisabledOrBroken(newStack);
        for (ModDataComponents.TickingAbility<?, ?> entry : ModDataComponents.TICKING_ABILITIES) {
            handleUnequip(entry, entity, oldStack, newStack, wasToggledOff);
        }

        updateHasTickingAbilities(entity);
    }

    private static <C, T extends EquipmentAbility> void handleUnequip(
            ModDataComponents.TickingAbility<C, T> tickingAbility,
            LivingEntity entity,
            ItemStack oldStack, ItemStack newStack,
            boolean wasToggledOff
    ) {
        C oldComponent = oldStack.get(tickingAbility.type().get());
        // Item was toggled off, does not have the ability, or the new ability is different
        if (oldComponent != null && (wasToggledOff || !oldComponent.equals(newStack.get(tickingAbility.type().get())))) {
            for (T oldAbility : tickingAbility.type().getEntries(oldComponent)) {
                if (oldAbility.isNonCosmetic()) {
                    tickingAbility.ticker().onUnequip(oldAbility, entity);
                }
            }
        }
    }

    public static void updateHasTickingAbilities(LivingEntity entity) {
        boolean shouldTick = EquipmentHelper.reduceEquipment(entity, false, false, false, (slotAccess, hasTickingAbilities) -> {
            for (ModDataComponents.TickingAbility<?, ?> entry : ModDataComponents.TICKING_ABILITIES) {
                // abilities are tracked as ticking even when they're cosmetic,
                // since updating the config does not trigger onItemChanged
                if (slotAccess.get().has(entry.type().get())) {
                    return true;
                }
            }
            return hasTickingAbilities;
        });
        ((LivingEntityExtensions) entity).artifacts$setTickingAbilities(shouldTick);
    }

    public static void onItemTick(LivingEntity entity) {
        // tick players both server- and clientside
        if (!(entity instanceof Player) && !((LivingEntityExtensions) entity).artifacts$hasTickingAbilities()) {
            return;
        }
        for (ModDataComponents.TickingAbility<?, ?> entry : ModDataComponents.TICKING_ABILITIES) {
            onAbilityTick(entry, entity);
        }
    }

    private static <T extends EquipmentAbility> void onAbilityTick(
            ModDataComponents.TickingAbility<?, T> entry,
            LivingEntity entity
    ) {
        EquipmentHelper.iterateComponents(entry.type(), entity, false, false, (ability, slotAccess) ->
                entry.ticker().wornTick(ability, slotAccess, entity, slotAccess.isOnCooldown(entity), slotAccess.isDisabledOrBroken())
        );
    }

    public static void onAttackBurningLivingHurt(LivingEntity entity, DamageSource damageSource) {
        LivingEntity attacker = DamageSourceHelper.getAttacker(damageSource);
        if (attacker != null && DamageSourceHelper.isMeleeAttack(damageSource) && !entity.fireImmune()) {
            int duration = (int) attacker.getAttributeValue(ModAttributes.ATTACK_BURNING_DURATION);
            entity.igniteForSeconds(duration);
        }
    }

    public static void doPostAttackEffects(LivingEntity entity, DamageSource damageSource) {
        EquipmentHelper.iterateComponents(ModDataComponents.RETALIATION_EFFECTS, entity, true, true,
                (ability, slotAccess) -> ability.onLivingHurt(entity, slotAccess, damageSource)
        );

        AttackEffect.onLivingHurt(entity, damageSource);
        onAttackBurningLivingHurt(entity, damageSource);
    }

    public static void onEntityAdded(Entity entity) {
        if (entity instanceof LivingEntity livingEntity) {
            updateHasTickingAbilities(livingEntity);
        }
        if (entity instanceof PathfinderMob creeper && creeper.is(ModTags.CREEPERS)) {
            Predicate<LivingEntity> predicate = target -> EquipmentHelper.hasAbilityActive(ModDataComponents.CREEPER_REPELLENT, target);
            ((MobAccessor) creeper).getGoalSelector().addGoal(3,
                    new AvoidEntityGoal<>(creeper, Player.class, predicate, 6, 1, 1.3, EntitySelector.NO_CREATIVE_OR_SPECTATOR)
            );
        }
    }

    public static void onPlaySoundAtEntity(LivingEntity entity, float volume, float pitch) {
        EquipmentHelper.iterateComponents(
                ModDataComponents.HURT_SOUND,
                entity,
                false, false,
                (component, _) -> {
                    if (component.enabled().get()) {
                        entity.playSound(component.soundEvent().value(), volume, pitch);
                    }
                }
        );
    }

    public static ItemStack applySmeltOresAbility(ItemStack original, @Nullable Entity entity, @Nullable BlockState state, Consumer<Integer> experienceConsumer) {
        if (entity instanceof LivingEntity livingEntity
                && livingEntity.level() instanceof ServerLevel serverLevel
                && EquipmentHelper.hasAbilityActive(ModDataComponents.AUTO_SMELT, livingEntity)
                && state != null
                && state.is(ModTags.ORES)
        ) {
            if (original.is(ModTags.RAW_MATERIALS)) {
                SingleRecipeInput input = new SingleRecipeInput(original);
                Optional<RecipeHolder<SmeltingRecipe>> recipe = serverLevel
                        .recipeAccess()
                        .getRecipeFor(RecipeType.SMELTING, input, livingEntity.level());
                if (recipe.isPresent()) {
                    ItemStack smeltingResult = recipe.get().value().assemble(input);
                    if (!smeltingResult.isEmpty()) {
                        experienceConsumer.accept(getExperience(recipe.get().value().experience()));
                        return smeltingResult.copyWithCount(smeltingResult.getCount() * original.getCount());
                    }
                }
            }
        }
        return original;
    }

    private static int getExperience(float experience) {
        int amount = Mth.floor(experience);
        if (Math.random() < Mth.frac(experience)) {
            amount++;
        }
        return amount;
    }

    public static void onBlockBroken(LivingEntity entity, BlockState blockState) {
        DamageOnBlockMined.onBlockBroken(entity, blockState);
        ToolTierUpgrade.onBlockBroken(entity, blockState);
    }

    public static int modifyUseDuration(int originalDuration, ItemStack item, LivingEntity entity) {
        if (originalDuration <= 0) {
            return originalDuration;
        }
        if (item.getUseAnimation() == ItemUseAnimation.EAT) {
            return (int) Math.max(1, Math.round(originalDuration / entity.getAttributeValue(ModAttributes.EATING_SPEED)));
        } else if (item.getUseAnimation() == ItemUseAnimation.DRINK) {
            return (int) Math.max(1, Math.round(originalDuration / entity.getAttributeValue(ModAttributes.DRINKING_SPEED)));
        }
        return originalDuration;
    }

    public static int modifyExperience(int originalXp, LivingEntity entity, Player attacker) {
        if (attacker == null || entity instanceof Player || originalXp <= 0) {
            return originalXp;
        }

        double multiplier = attacker.getAttributeValue(ModAttributes.ENTITY_EXPERIENCE);
        int droppedXp = (int) Math.round(originalXp * multiplier);
        return Math.max(0, droppedXp);
    }

    public static void absorbDamage(LivingEntity entity, DamageSource damageSource, float amount) {
        LivingEntity attacker = DamageSourceHelper.getAttacker(damageSource);
        if (attacker != null && DamageSourceHelper.isMeleeAttack(damageSource)) {
            EquipmentHelper.iterateComponents(
                    ModDataComponents.DAMAGE_ABSORPTION,
                    attacker,
                    true, true,
                    (ability, slotAccess) -> {
                        double absorptionRatio = ability.absorptionRatio().get();
                        double maxHealthAbsorbed = ability.maxDamageAbsorbed().get();

                        float damageDealt = Math.min(amount, entity.getHealth());
                        float damageAbsorbed = (float) Math.min(maxHealthAbsorbed, absorptionRatio * damageDealt);

                        if (damageAbsorbed > 0 && ability.absorptionChance().get() > entity.getRandom().nextDouble()) {
                            attacker.heal(damageAbsorbed);
                            slotAccess.hurtAndBreak(entity, ability.itemDamage().get());
                        }
                    }
            );
        }
    }

    public static void damageItemsWhenAttacking(LivingEntity target, DamageSource damageSource, boolean isKillingBlow) {
        LivingEntity attacker = DamageSourceHelper.getAttacker(damageSource);
        if (attacker == null) {
            return;
        }
        EquipmentHelper.iterateComponents(
                ModDataComponents.DAMAGE_ON_ATTACK,
                attacker,
                true, true,
                (component, slotAccess) -> {
                    boolean checkMelee = !component.requireMelee() || DamageSourceHelper.isMeleeAttack(damageSource);
                    boolean checkKill = !component.requireKill() || isKillingBlow;
                    boolean checkEntity = component.entity().isEmpty() || component.entity().get().contains(target.typeHolder());
                    if (checkMelee && checkKill && checkEntity) {
                        slotAccess.hurtAndBreak(attacker, component.itemDamage().get());
                    }
                }
        );
    }

    public static float getModifiedFriction(float friction, LivingEntity entity, Block block) {
        if (friction > 0.6F && block.defaultBlockState().is(BlockTags.ICE)) {
            double slipperinessReduction = entity.getAttributeValue(ModAttributes.SLIP_RESISTANCE);
            return Mth.lerp(((float) slipperinessReduction), friction, 0.6F);
        }
        return friction;
    }

    public static void applyBoneMealAfterEating(LivingEntity entity, FoodProperties properties) {
        if (!entity.level().isClientSide()
                && EquipmentHelper.hasAbilityActive(ModDataComponents.POST_EATING_PLANT_GROWTH, entity)
                && properties.nutrition() > 0
                && !properties.canAlwaysEat()
                && entity.onGround()
                && entity.getBlockStateOn().is(ModTags.ROOTED_BOOTS_GRASS)
        ) {
            BoneMealItem.growCrop(new ItemStack(Items.BONE_MEAL), entity.level(), entity.getOnPos());
        }
    }

    public static EventResult onPlayerSwim(Player player) {
        SwimData swimData = PlatformServices.getPlatformHelper().getSwimData(player);
        if (swimData != null) {
            if (swimData.isSwimFlying()) {
                return EventResult.SUCCESS;
            } else if (EquipmentHelper.hasAbilityActive(ModDataComponents.SINKING, player)) {
                return EventResult.FAIL;
            }
        }
        return EventResult.PASS;
    }

    public static boolean onFluidCollision(LivingEntity entity, FluidState fluidState) {
        SwimData swimData = PlatformServices.getPlatformHelper().getSwimData(entity);
        if (swimData == null || swimData.shouldBreakSurfaceTension() || swimData.isSwimFlying()) {
            return false;
        }
        return EquipmentHelper.hasAbilityActive(ModDataComponents.FLUID_COLLISION, entity, true, ability ->
                ability.matchesFluid(fluidState) && ability.condition().test(entity)
        );
    }

    public static boolean fart(LivingEntity entity) {
        double chance = entity.getAttributeValue(ModAttributes.FLATULENCE);
        if (!entity.level().isClientSide() && entity.getRandom().nextFloat() < chance) {
            entity.gameEvent(ModGameEvents.FART);
            entity.level().playSound(null, entity, ModSoundEvents.FART.value(), SoundSource.PLAYERS, 1, 0.9F + entity.getRandom().nextFloat() * 0.2F);
            EquipmentHelper.iterateComponents(
                    ModDataComponents.DAMAGE_ON_FART,
                    entity,
                    true, true,
                    (component, slotAccess) -> slotAccess.hurtAndBreak(entity, component.get())
            );
            return true;
        }
        return false;
    }

    public static void onItemFished(Player player) {
        EquipmentHelper.iterateComponents(
                ModDataComponents.DAMAGE_ON_ITEM_FISHED,
                player,
                true, true,
                (component, slotAccess) -> slotAccess.hurtAndBreak(player, component.get())
        );
    }

    public static void onJump(LivingEntity entity) {
        EquipmentHelper.iterateComponents(
                ModDataComponents.DAMAGE_ON_JUMP,
                entity,
                true, true,
                (component, slotAccess) -> slotAccess.hurtAndBreak(entity, component.get())
        );
    }

    public static void onFall(LivingEntity entity, int damage, double effectiveFallDistance) {
        if (!entity.is(EntityTypeTags.FALL_DAMAGE_IMMUNE) && damage <= 0 && effectiveFallDistance > 3) {
            EquipmentHelper.iterateComponents(
                    ModDataComponents.DAMAGE_ON_FALL,
                    entity,
                    true, true,
                    (component, slotAccess) -> slotAccess.hurtAndBreak(entity, component.get())
            );
        }
    }
}
