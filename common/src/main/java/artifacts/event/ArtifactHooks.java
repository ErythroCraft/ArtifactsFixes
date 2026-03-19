package artifacts.event;

import artifacts.attribute.DynamicAttributeModifier;
import artifacts.component.SwimData;
import artifacts.component.ability.EquipmentAbility;
import artifacts.component.ability.PostDamageCooldown;
import artifacts.component.ability.mobeffect.AttackEffect;
import artifacts.component.ability.mobeffect.PostDamageEffect;
import artifacts.equipment.EquipmentHelper;
import artifacts.extensions.ability.LivingEntityExtensions;
import artifacts.item.UmbrellaHelper;
import artifacts.mixin.accessors.MobAccessor;
import artifacts.platform.PlatformServices;
import artifacts.registry.ModAttributes;
import artifacts.registry.ModDataComponents;
import artifacts.registry.ModTags;
import artifacts.util.DamageSourceHelper;
import be.florens.expandability.api.EventResult;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
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
    }

    public static void onLivingDamaged(LivingEntity entity, DamageSource source, float amount) {
        absorbDamage(entity, source, amount);
        PostDamageEffect.onLivingDamaged(entity, source);
        PostDamageCooldown.onLivingDamaged(entity, source);
    }

    public static void onItemChanged(LivingEntity entity, ItemStack oldStack, ItemStack newStack) {
        if (entity.level().isClientSide() || ItemStack.matches(oldStack, newStack)) {
            return;
        }

        boolean wasToggledOff = !oldStack.has(ModDataComponents.DISABLED_BY_TOGGLE.get())
                && newStack.has(ModDataComponents.DISABLED_BY_TOGGLE.get());
        for (var entry : ModDataComponents.TICKING_ABILITIES) {
            handleUnequip(entry, entity, oldStack, newStack, wasToggledOff);
        }

        updateHasTickingAbilities(entity);
    }

    private static <T extends EquipmentAbility> void handleUnequip(
            ModDataComponents.TickingAbility<T> entry,
            LivingEntity entity,
            ItemStack oldStack, ItemStack newStack,
            boolean wasToggledOff
    ) {
        T oldAbility = oldStack.get(entry.type().get());
        // Item was toggled off, does not have the ability, or the new ability is different
        if (oldAbility != null && oldAbility.isNonCosmetic()
                && (wasToggledOff || !oldAbility.equals(newStack.get(entry.type().get())))
        ) {
            entry.ticker().onUnequip(oldAbility, entity);
        }
    }

    public static void updateHasTickingAbilities(LivingEntity entity) {
        boolean shouldTick = EquipmentHelper.reduceEquipment(entity, false, (stack, hasTickingAbilities) -> {
            for (var entry : ModDataComponents.TICKING_ABILITIES) {
                // abilities are tracked as ticking even when they're cosmetic,
                // since updating the config does not trigger onItemChanged
                if (stack.has(entry.type().get())) {
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
        for (var entry : ModDataComponents.TICKING_ABILITIES) {
            onAbilityTick(entry, entity);
        }
    }

    private static <T extends EquipmentAbility> void onAbilityTick(
            ModDataComponents.TickingAbility<T> entry,
            LivingEntity entity
    ) {
        EquipmentHelper.iterateAbilities(entry.type().get(), entity, false, false, (ability, stack) -> {
            boolean isOnCooldown = entity instanceof Player player && player.getCooldowns().isOnCooldown(stack);
            entry.ticker().wornTick(ability, entity, isOnCooldown, stack.has(ModDataComponents.DISABLED_BY_TOGGLE.get()));
        });

    }

    public static void onAttackBurningLivingHurt(LivingEntity entity, DamageSource damageSource) {
        LivingEntity attacker = DamageSourceHelper.getAttacker(damageSource);
        if (attacker != null && DamageSourceHelper.isMeleeAttack(damageSource) && !entity.fireImmune()) {
            int duration = (int) attacker.getAttributeValue(ModAttributes.ATTACK_BURNING_DURATION);
            entity.igniteForSeconds(duration);
        }
    }

    public static void doPostAttackEffects(LivingEntity entity, DamageSource damageSource) {
        EquipmentHelper.iterateAbilities(ModDataComponents.RETALIATION_EFFECTS.get(), entity, true, true,
                (ability, stack) -> ability.onLivingHurt(entity, stack, damageSource)
        );

        AttackEffect.onLivingHurt(entity, damageSource);
        onAttackBurningLivingHurt(entity, damageSource);
    }

    public static void onEntityAdded(Entity entity) {
        if (entity instanceof LivingEntity livingEntity) {
            updateHasTickingAbilities(livingEntity);
        }
        if (entity instanceof PathfinderMob creeper && creeper.getType().is(ModTags.CREEPERS)) {
            Predicate<LivingEntity> predicate = target -> EquipmentHelper.hasAbilityActive(ModDataComponents.CREEPER_REPELLENT.get(), target);
            ((MobAccessor) creeper).getGoalSelector().addGoal(3,
                    new AvoidEntityGoal<>(creeper, Player.class, predicate, 6, 1, 1.3, EntitySelector.NO_CREATIVE_OR_SPECTATOR)
            );
        }
    }

    public static void onPlaySoundAtEntity(LivingEntity entity, float volume, float pitch) {
        EquipmentHelper.iterateComponents(ModDataComponents.HURT_SOUND.get(), entity, (stack, ability) -> {
            if (ability.enabled().get()) {
                entity.playSound(ability.soundEvent().value(), volume, pitch);
            }
        });
    }

    public static ItemStack applySmeltOresAbility(ItemStack original, @Nullable Entity entity, @Nullable BlockState state, Consumer<Integer> experienceConsumer) {
        if (entity instanceof LivingEntity livingEntity
                && livingEntity.level() instanceof ServerLevel serverLevel
                && EquipmentHelper.hasAbilityActive(ModDataComponents.AUTO_SMELT.get(), livingEntity)
                && state != null
                && state.is(ModTags.ORES)
        ) {
            if (original.is(ModTags.RAW_MATERIALS)) {
                SingleRecipeInput input = new SingleRecipeInput(original);
                Optional<RecipeHolder<SmeltingRecipe>> recipe = serverLevel
                        .recipeAccess()
                        .getRecipeFor(RecipeType.SMELTING, input, livingEntity.level());
                if (recipe.isPresent()) {
                    ItemStack smeltingResult = recipe.get().value().assemble(input, livingEntity.level().registryAccess());
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
            EquipmentHelper.iterateAbilities(ModDataComponents.DAMAGE_ABSORPTION.get(), attacker, true, true, (ability, stack) -> {
                double absorptionRatio = ability.absorptionRatio().get();
                double maxHealthAbsorbed = ability.maxDamageAbsorbed().get();

                float damageDealt = Math.min(amount, entity.getHealth());
                float damageAbsorbed = (float) Math.min(maxHealthAbsorbed, absorptionRatio * damageDealt);

                if (damageAbsorbed > 0 && ability.absorptionChance().get() > entity.getRandom().nextDouble()) {
                    attacker.heal(damageAbsorbed);
                }
            });
        }
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
                && EquipmentHelper.hasAbilityActive(ModDataComponents.POST_EATING_PLANT_GROWTH.get(), entity)
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
            } else if (EquipmentHelper.hasAbilityActive(ModDataComponents.SINKING.get(), player)) {
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
        return EquipmentHelper.hasAbilityActive(ModDataComponents.FLUID_COLLISION.get(), entity, true, ability ->
                ability.matchesFluid(fluidState) && ability.condition().test(entity)
        );
    }
}
