package artifacts.event;

import artifacts.Artifacts;
import artifacts.ability.ApplyCooldownAfterDamageAbility;
import artifacts.ability.ArtifactAbility;
import artifacts.ability.AttractItemsAbility;
import artifacts.ability.SwimInAirAbility;
import artifacts.ability.mobeffect.ApplyMobEffectAfterDamageAbility;
import artifacts.ability.mobeffect.AttacksInflictMobEffectAbility;
import artifacts.ability.retaliation.RetaliationAbility;
import artifacts.attribute.DynamicAttributeModifier;
import artifacts.integration.EquipmentIntegrationUtils;
import artifacts.item.UmbrellaItem;
import artifacts.mixin.accessors.MobAccessor;
import artifacts.platform.PlatformServices;
import artifacts.registry.ModAbilities;
import artifacts.registry.ModAttributes;
import artifacts.registry.ModDataComponents;
import artifacts.registry.ModTags;
import artifacts.util.AbilityHelper;
import artifacts.util.DamageSourceHelper;
import dev.architectury.event.EventResult;
import dev.architectury.event.events.common.EntityEvent;
import dev.architectury.event.events.common.PlayerEvent;
import dev.architectury.event.events.common.TickEvent;
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
import net.minecraft.world.item.Items;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.item.crafting.SmeltingRecipe;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class ArtifactEvents {

    public static void register() {
        PlayerEvent.DROP_ITEM.register(AttractItemsAbility::onItemToss);
        EntityEvent.LIVING_HURT.register(ArtifactEvents::onAttackBurningLivingHurt);
        EntityEvent.LIVING_HURT.register(ArtifactEvents::onPendantLivingHurt);
        EntityEvent.LIVING_HURT.register(ArtifactEvents::onLightningHurt);
        EntityEvent.LIVING_HURT.register(AttacksInflictMobEffectAbility::onLivingHurt);
        EntityEvent.ADD.register(ArtifactEvents::onEntityJoinWorld);
        TickEvent.PLAYER_PRE.register(SwimInAirAbility::onHeliumFlamingoTick);
    }

    public static void livingUpdate(LivingEntity entity) {
        onItemTick(entity);
        UmbrellaItem.onLivingUpdate(entity);
        DynamicAttributeModifier.tickModifiers(entity);
    }

    public static void onLivingDamaged(LivingEntity entity, DamageSource source, float amount) {
        ArtifactEvents.absorbDamage(entity, source, amount);
        ApplyMobEffectAfterDamageAbility.onLivingDamaged(entity, source, amount);
        ApplyCooldownAfterDamageAbility.onLivingDamaged(entity, source);
    }

    public static void onItemChanged(LivingEntity entity, ItemStack oldStack, ItemStack newStack) {
        if (entity.level().isClientSide()) {
            return;
        }
        List<ArtifactAbility> oldAbilities = oldStack.get(ModDataComponents.ABILITIES.value());
        List<ArtifactAbility> newAbilities = newStack.get(ModDataComponents.ABILITIES.value());
        if (oldAbilities == null || oldAbilities.equals(newAbilities)) {
            return;
        } else if (newAbilities == null) {
            newAbilities = List.of();
        }

        for (ArtifactAbility ability : oldAbilities) {
            if (!newAbilities.contains(ability)) {
                boolean wasActive = ability.isEnabled() && AbilityHelper.isToggledOn(ability.getType(), entity);
                ability.onUnequip(entity, wasActive);
            }
        }
    }

    public static void onItemTick(LivingEntity entity) {
        if (entity.level().isClientSide()) {
            return;
        }
        EquipmentIntegrationUtils.findAllEquippedBy(entity, stack -> stack.has(ModDataComponents.ABILITIES.value()))
                .forEach(stack -> {
                    for (ArtifactAbility ability : AbilityHelper.getAbilities(stack)) {
                        boolean isActive = ability.isActive(entity);
                        boolean isOnCooldown = entity instanceof Player player && player.getCooldowns().isOnCooldown(stack.getItem());
                        ability.wornTick(entity, isOnCooldown, isActive);
                    }
                });
    }

    public static EventResult onAttackBurningLivingHurt(LivingEntity entity, DamageSource damageSource, float amount) {
        LivingEntity attacker = DamageSourceHelper.getAttacker(damageSource);
        if (attacker != null && DamageSourceHelper.isMeleeAttack(damageSource) && !entity.fireImmune()) {
            int duration = (int) attacker.getAttributeValue(ModAttributes.ATTACK_BURNING_DURATION);
            entity.igniteForSeconds(duration);
        }
        return EventResult.pass();
    }

    private static EventResult onPendantLivingHurt(LivingEntity entity, DamageSource damageSource, float amount) {
        activateRetaliationAbility(ModAbilities.SET_ATTACKERS_ON_FIRE.value(), entity, damageSource, amount);
        activateRetaliationAbility(ModAbilities.THORNS.value(), entity, damageSource, amount);

        return EventResult.pass();
    }

    private static void activateRetaliationAbility(ArtifactAbility.Type<? extends RetaliationAbility> type, LivingEntity entity, DamageSource damageSource, float amount) {
        AbilityHelper.forEach(type, entity, (ability, stack) -> ability.onLivingHurt(entity, stack, damageSource, amount), true);
    }

    private static EventResult onEntityJoinWorld(Entity entity, Level level) {
        if (entity instanceof PathfinderMob creeper && creeper.getType().is(ModTags.CREEPERS)) {
            Predicate<LivingEntity> predicate = target -> AbilityHelper.hasAbilityActive(ModAbilities.SCARE_CREEPERS.value(), target);
            ((MobAccessor) creeper).getGoalSelector().addGoal(3,
                    new AvoidEntityGoal<>(creeper, Player.class, predicate, 6, 1, 1.3, EntitySelector.NO_CREATIVE_OR_SPECTATOR::test)
            );
        }
        return EventResult.pass();
    }

    public static void onPlaySoundAtEntity(LivingEntity entity, float volume, float pitch) {
        if (Artifacts.CONFIG.general.modifyHurtSounds.get()) {
            AbilityHelper.forEach(ModAbilities.MODIFY_HURT_SOUND.value(), entity, ability -> entity.playSound(ability.soundEvent().value(), volume, pitch));
        }
    }

    private static EventResult onLightningHurt(LivingEntity entity, DamageSource damageSource, float amount) {
        if (!entity.level().isClientSide()
                && amount > 0
                && AbilityHelper.hasAbilityActive(ModAbilities.DAMAGE_IMMUNITY.value(), entity, ability -> damageSource.is(ability.tag()))
        ) {
            return EventResult.interruptFalse();
        }
        return EventResult.pass();
    }

    public static ItemStack applySmeltOresAbility(ItemStack original, @Nullable Entity entity, @Nullable BlockState state, Consumer<Integer> experienceConsumer) {
        if (entity instanceof LivingEntity livingEntity
                && AbilityHelper.hasAbilityActive(ModAbilities.SMELT_ORES.value(), livingEntity)
                && state != null
                && state.is(ModTags.ORES)
        ) {
            if (original.is(ModTags.RAW_MATERIALS)) {
                Optional<RecipeHolder<SmeltingRecipe>> recipe = livingEntity.level()
                        .getRecipeManager()
                        .getRecipeFor(RecipeType.SMELTING, new SingleRecipeInput(original), livingEntity.level());
                if (recipe.isPresent()) {
                    ItemStack smeltingResult = recipe.get().value().getResultItem(livingEntity.level().registryAccess());
                    if (!smeltingResult.isEmpty()) {
                        experienceConsumer.accept(getExperience(recipe.get().value().getExperience()));
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
        if (item.getUseAnimation() == UseAnim.EAT) {
            return (int) Math.max(1, Math.round(originalDuration / entity.getAttributeValue(ModAttributes.EATING_SPEED)));
        } else if (item.getUseAnimation() == UseAnim.DRINK) {
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
            AbilityHelper.forEach(ModAbilities.ATTACKS_ABSORB_DAMAGE.value(), attacker, ability -> {
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
        if (ModTags.isInTag(block, BlockTags.ICE)
                && friction > 0.6F
        ) {
            double slipperinessReduction = entity.getAttributeValue(ModAttributes.SLIP_RESISTANCE);
            return Mth.lerp(((float) slipperinessReduction), friction, 0.6F);
        }
        return friction;
    }

    public static void applyBoneMealAfterEating(LivingEntity entity, FoodProperties properties) {
        if (!entity.level().isClientSide()
                && AbilityHelper.hasAbilityActive(ModAbilities.GROW_PLANTS_AFTER_EATING.value(), entity)
                && properties.nutrition() > 0
                && !properties.canAlwaysEat()
                && entity.onGround()
                && entity.getBlockStateOn().is(ModTags.ROOTED_BOOTS_GRASS)
        ) {
            BoneMealItem.growCrop(new ItemStack(Items.BONE_MEAL), entity.level(), entity.getOnPos());
        }
    }
}
