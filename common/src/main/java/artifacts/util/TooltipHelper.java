package artifacts.util;

import artifacts.Artifacts;
import artifacts.client.ToggleKeyHandlers;
import artifacts.component.ability.EntityCondition;
import artifacts.component.ability.EquipmentAbility;
import artifacts.component.ability.EquipmentAttributeModifier;
import artifacts.component.ability.mobeffect.*;
import artifacts.integration.ModCompat;
import artifacts.registry.ModDataComponents;
import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.component.TypedDataComponent;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.util.StringUtil;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import org.apache.commons.lang3.mutable.MutableBoolean;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

import static net.minecraft.world.item.component.ItemAttributeModifiers.ATTRIBUTE_MODIFIER_FORMAT;

public class TooltipHelper {

    public static void addAbilityDescriptions(
            Consumer<Component> tooltip,
            ItemStack stack,
            Item.TooltipContext context,
            TooltipDisplay display,
            TooltipFlag tooltipFlag,
            @Nullable Player player
    ) {
        if (!Artifacts.CONFIG.client.showTooltips.get()) {
            return;
        }

        if (showsMissingDependencyTooltip(stack, display)) {
            tooltip.accept(Component.translatable("%s.tooltip.missing_dependency".formatted(Artifacts.MOD_ID))
                    .withStyle(ChatFormatting.RED, ChatFormatting.BOLD)
            );
        } else if (showsCosmeticTooltip(stack, display)) {
            tooltip.accept(Component.translatable("%s.tooltip.cosmetic".formatted(Artifacts.MOD_ID))
                    .withStyle(ChatFormatting.ITALIC).withStyle(ChatFormatting.GRAY)
            );
        } else if (ItemDamageUtil.needsRepair(stack)) {
            tooltip.accept(Component.translatable("%s.tooltip.broken".formatted(Artifacts.MOD_ID))
                    .withStyle(ChatFormatting.RED)
            );
        }

        // Description that shows even when the item is cosmetic (used by Novelty Drinking Hat)
        TooltipHelper.getComponentIfVisible(ModDataComponents.ABILITY_LORE.get(), stack, display).ifPresent(lore ->
                lore.addToTooltip(context, tooltip, tooltipFlag, stack)
        );

        // Tooltip order (a bit janky but vanilla does it in a similar way)
        Consumer<DataComponentType<? extends EquipmentAbility>> descriptions
                = type -> TooltipHelper.addAbilityDescription(tooltip, stack, context, display, type);

        // Non-equipable abilities
        descriptions.accept(ModDataComponents.HANDHELD_GLIDER.get());
        descriptions.accept(ModDataComponents.BLOCKS_ATTACKS.get());
        descriptions.accept(ModDataComponents.INFINITE_CONSUMABLE.get());
        descriptions.accept(ModDataComponents.EQUIPABLE_TOTEM.get());

        // Composite abilities
        descriptions.accept(ModDataComponents.MOB_EFFECTS.get());
        descriptions.accept(ModDataComponents.ENCHANTMENT_LEVEL_MODIFIERS.get());
        descriptions.accept(ModDataComponents.ATTRIBUTE_MODIFIERS.get());
        descriptions.accept(ModDataComponents.POST_DAMAGE_EFFECTS.get());
        descriptions.accept(ModDataComponents.POST_EATING_EFFECTS.get());
        descriptions.accept(ModDataComponents.ATTACK_EFFECTS.get());
        descriptions.accept(ModDataComponents.RETALIATION_EFFECTS.get());

        // Other
        descriptions.accept(ModDataComponents.TOOL_TIER_UPGRADE.get());
        descriptions.accept(ModDataComponents.DOUBLE_JUMP.get());
        descriptions.accept(ModDataComponents.CURE_EFFECTS.get());
        descriptions.accept(ModDataComponents.DAMAGE_ABSORPTION.get());
        descriptions.accept(ModDataComponents.ENDER_PEARL_HUNGER_COST.get());
        descriptions.accept(ModDataComponents.ENDER_PEARL_DAMAGE_IMMUNITY.get());
        descriptions.accept(ModDataComponents.REPLENISH_HUNGER_ON_GRASS.get());
        descriptions.accept(ModDataComponents.CREEPER_REPELLENT.get());
        descriptions.accept(ModDataComponents.PHANTOM_REPELLENT.get());
        descriptions.accept(ModDataComponents.SINKING.get());
        descriptions.accept(ModDataComponents.AUTO_SMELT.get());
        descriptions.accept(ModDataComponents.FLUID_COLLISION.get());
        descriptions.accept(ModDataComponents.SWIM_IN_AIR.get());
        descriptions.accept(ModDataComponents.WALK_ON_POWDER_SNOW.get());
        descriptions.accept(ModDataComponents.DAMAGE_IMMUNITY.get());
        descriptions.accept(ModDataComponents.POST_EATING_PLANT_GROWTH.get());

        // Toggle key(s)
        TooltipHelper.getComponentIfVisible(ModDataComponents.TOGGLE_KEY.get(), stack, display).ifPresent(toggleKey -> {
            if (!TooltipHelper.isCosmetic(stack) && player != null && player.level().isClientSide()) {
                ToggleKeyHandlers.addTooltip(toggleKey, stack.has(ModDataComponents.DISABLED_BY_TOGGLE.get()), tooltip);
            }
        });
    }

    private static void addAbilityDescription(
            Consumer<Component> tooltip,
            ItemStack stack,
            Item.TooltipContext context,
            TooltipDisplay display,
            DataComponentType<? extends EquipmentAbility> type
    ) {
        TooltipHelper.getAbilityIfVisible(type, stack, display).ifPresent(ability ->
                ability.addToTooltip(new EquipmentAbility.TooltipWriter(type, tooltip, context, stack))
        );
    }

    public static void addAttributeTooltips(Consumer<Component> tooltip, ItemStack stack, Item.TooltipContext context, TooltipDisplay display) {
        boolean hasSlotTooltip = false;

        // FIXME: Fix attribute tooltips
        //  - This needs a more precise injection target to show correctly,
        //    this breaks when there are multiple vanilla slots (@ModifyReceiver on forEachModifier might work)
        //  - Don't show artifact attributes/mob effects under hand slots
        if (display.shows(DataComponents.ATTRIBUTE_MODIFIERS)) {
            for (EquipmentSlot slot : EquipmentSlot.values()) {
                MutableBoolean b = new MutableBoolean(false);
                stack.forEachModifier(slot, (_, _) -> b.setTrue());
                if (b.booleanValue()) {
                    hasSlotTooltip = true;

                    addAttributeTooltips(tooltip, stack, display);
                    addMobEffectTooltips(tooltip, stack, context, display);
                }
            }
        }

        // add attribute and mob effect tooltips if they haven't already been added under the vanilla tooltip
        if (!hasSlotTooltip) {
            if (getAbilityIfVisible(ModDataComponents.ATTRIBUTE_MODIFIERS.get(), stack, display).isPresent()
                    || getAbilityIfVisible(ModDataComponents.MOB_EFFECTS.get(), stack, display).isPresent()
            ) {
                tooltip.accept(CommonComponents.EMPTY);
                tooltip.accept(Component.translatable("item.modifiers.%s".formatted(EquipmentSlotGroup.BODY.getSerializedName()))
                        .withStyle(ChatFormatting.GRAY)
                );
            }

            addAttributeTooltips(tooltip, stack, display);
            addMobEffectTooltips(tooltip, stack, context, display);
        }

        // these don't fall under the normal "When Equipped:" tooltip
        addWhenHurtTooltips(tooltip, stack, context, display);
        addPerFoodPointEatenTooltips(tooltip, stack, context, display);
        addAttacksInflictTooltips(tooltip, stack, context, display);
    }

    private static void addAttributeTooltips(Consumer<Component> tooltip, ItemStack stack, TooltipDisplay display) {
        getAbilityIfVisible(ModDataComponents.ATTRIBUTE_MODIFIERS.get(), stack, display).ifPresent(ability -> {
            for (EquipmentAttributeModifier entry : ability.entries()) {
                addAttributeTooltip(tooltip, entry);
            }
        });
    }

    private static void addMobEffectTooltips(Consumer<Component> tooltip, ItemStack stack, Item.TooltipContext context, TooltipDisplay display) {
        getAbilityIfVisible(ModDataComponents.MOB_EFFECTS.get(), stack, display).ifPresent(ability -> {
            for (EquipmentMobEffect entry : ability.entries()) {
                MobEffectProvider provider = entry.provider();
                addMobEffectTooltip(tooltip, context, provider.mobEffect().value(), provider.duration().get(), provider.level().get(), 1, provider.condition() == EntityCondition.ALWAYS);
            }
        });
    }

    private static void addWhenHurtTooltips(Consumer<Component> tooltip, ItemStack stack, Item.TooltipContext context, TooltipDisplay display) {
        MutableBoolean flag = new MutableBoolean(false);
        List<TagKey<DamageType>> list = new ArrayList<>();
        getAbilityIfVisible(ModDataComponents.POST_DAMAGE_EFFECTS.get(), stack, display).ifPresent(ability -> {
            for (PostDamageEffect entry : ability.entries()) {
                if (entry.tag().isEmpty()) {
                    flag.setTrue();
                } else {
                    list.add(entry.tag().get());
                }
            }
        });
        getAbilityIfVisible(ModDataComponents.POST_DAMAGE_COOLDOWN.get(), stack, display).ifPresent(ability -> {
            if (ability.tag().isEmpty()) {
                flag.setTrue();
            } else if (!list.contains(ability.tag().get())) {
                list.add(ability.tag().get());
            }
        });

        if (flag.booleanValue()) {
            tooltip.accept(CommonComponents.EMPTY);
            tooltip.accept(Component.translatable("artifacts.tooltip.when_hurt").withStyle(ChatFormatting.GRAY));
            addWhenHurtTooltip(tooltip, stack, context, display, null);
        }
        for (TagKey<DamageType> tag : list) {
            tooltip.accept(CommonComponents.EMPTY);
            tooltip.accept(Component.translatable("artifacts.tooltip.when_hurt.%s".formatted(
                    tag.location()
                            .toString()
                            .replace("minecraft:", "")
                            .replace(':', '.')
            )).withStyle(ChatFormatting.GRAY));
            addWhenHurtTooltip(tooltip, stack, context, display, tag);
        }
    }

    private static void addWhenHurtTooltip(Consumer<Component> tooltip, ItemStack stack, Item.TooltipContext context, TooltipDisplay display, @Nullable TagKey<DamageType> tag) {
        getAbilityIfVisible(ModDataComponents.POST_DAMAGE_EFFECTS.get(), stack, display).ifPresent(ability -> {
            for (PostDamageEffect entry : ability.entries()) {
                if (entry.tag().isEmpty() && tag == null || entry.tag().isPresent() && entry.tag().get().equals(tag)) {
                    addMobEffectTooltip(tooltip, context, entry.provider().mobEffect().value(), entry.provider().duration().get(), entry.provider().level().get(), entry.chance().get(), false);
                }
            }
        });
        getAbilityIfVisible(ModDataComponents.POST_DAMAGE_COOLDOWN.get(), stack, display).ifPresent(ability -> {
            if (ability.tag().isEmpty() && tag == null || ability.tag().isPresent() && ability.tag().get().equals(tag)) {
                tooltip.accept(Component.translatable("artifacts.tooltip.cooldown", formatDurationSeconds(context, ability.cooldown().get())).withStyle(ChatFormatting.GOLD));
            }
        });
    }

    private static void addPerFoodPointEatenTooltips(Consumer<Component> tooltip, ItemStack stack, Item.TooltipContext context, TooltipDisplay display) {
        getAbilityIfVisible(ModDataComponents.POST_EATING_EFFECTS.get(), stack, display).ifPresent(ability -> {
            tooltip.accept(CommonComponents.EMPTY);
            tooltip.accept(Component.translatable("artifacts.tooltip.per_food_point_restored").withStyle(ChatFormatting.GRAY));
            for (PostEatingEffect entry : ability.entries()) {
                MobEffectProvider provider = entry.provider();
                addMobEffectTooltip(tooltip, context, provider.mobEffect().value(), provider.duration().get(), provider.level().get(), 1, false);
            }
        });
    }

    private static void addAttacksInflictTooltips(Consumer<Component> tooltip, ItemStack stack, Item.TooltipContext context, TooltipDisplay display) {
        getAbilityIfVisible(ModDataComponents.ATTACK_EFFECTS.get(), stack, display).ifPresent(ability -> {
            tooltip.accept(CommonComponents.EMPTY);
            tooltip.accept(Component.translatable("artifacts.tooltip.attacks_inflict").withStyle(ChatFormatting.GRAY));
            for (AttackEffect entry : ability.entries()) {
                addMobEffectTooltip(tooltip, context, entry.provider().mobEffect().value(), entry.provider().duration().get(), entry.provider().level().get(), entry.chance().get(), false);
                if (entry.cooldown().get() > 0) {
                    tooltip.accept(Component.translatable("artifacts.tooltip.cooldown", formatDurationSeconds(context, entry.cooldown().get())).withStyle(ChatFormatting.GOLD));
                }
            }
        });
    }

    // TODO: consider using ItemAttributeModifiers.Display if possible
    private static void addAttributeTooltip(Consumer<Component> tooltip, EquipmentAttributeModifier entry) {
        double amount = entry.amount().get();

        if (entry.operation() != AttributeModifier.Operation.ADD_VALUE) {
            amount *= 100;
        } else if (entry.attribute().equals(Attributes.KNOCKBACK_RESISTANCE)) {
            amount *= 10;
        }

        if (amount > 0) {
            tooltip.accept(Component.translatable(
                    "attribute.modifier.plus." + entry.operation().id(),
                    ATTRIBUTE_MODIFIER_FORMAT.format(amount),
                    Component.translatable(entry.attribute().value().getDescriptionId())
            ).withStyle(entry.attribute().value().getStyle(true)));
        } else if (amount < 0) {
            amount *= -1;
            tooltip.accept(Component.translatable(
                    "attribute.modifier.take." + entry.operation().id(),
                    ATTRIBUTE_MODIFIER_FORMAT.format(amount),
                    Component.translatable(entry.attribute().value().getDescriptionId())
            ).withStyle(entry.attribute().value().getStyle(false)));
        }
    }

    private static void addMobEffectTooltip(Consumer<Component> tooltip, Item.TooltipContext context, MobEffect mobEffect, int duration, int level, double chance, boolean isInfinite) {
        MutableComponent mutableComponent;
        mutableComponent = Component.translatable(mobEffect.getDescriptionId());
        if (level > 1) {
            mutableComponent = Component.translatable("potion.withAmplifier", mutableComponent, Component.translatable("potion.potency." + (level - 1)));
        }
        if (!isInfinite) {
            mutableComponent = Component.translatable("potion.withDuration", mutableComponent, formatDurationSeconds(context, duration));
        }
        if (Mth.equal(chance, 1)) {
            tooltip.accept(Component.translatable("artifacts.tooltip.plus_mob_effect", mutableComponent).withStyle(mobEffect.getCategory().getTooltipFormatting()));
        } else {
            tooltip.accept(Component.translatable("artifacts.tooltip.plus_mob_effect_chance", mutableComponent, Math.round(chance * 100)).withStyle(mobEffect.getCategory().getTooltipFormatting()));
        }
    }

    private static MutableComponent formatDurationSeconds(Item.TooltipContext context, int seconds) {
        return Component.literal(StringUtil.formatTickDuration(seconds * 20, context.tickRate()));
    }

    private static <A extends EquipmentAbility> Optional<A> getAbilityIfVisible(DataComponentType<A> type, ItemStack stack, TooltipDisplay display) {
        return getComponentIfVisible(type, stack, display)
                .filter(EquipmentAbility::isNonCosmetic);
    }

    private static <C> Optional<C> getComponentIfVisible(DataComponentType<C> type, ItemStack stack, TooltipDisplay display) {
        if (display.shows(type)) {
            return Optional.ofNullable(stack.get(type));
        }
        return Optional.empty();
    }

    private static boolean isCosmetic(ItemStack stack) {
        for (TypedDataComponent<?> component : stack.getComponents()) {
            if (component.value() instanceof EquipmentAbility ability && ability.isNonCosmetic()) {
                return false;
            }
        }
        return true;
    }

    private static boolean showsMissingDependencyTooltip(ItemStack stack, TooltipDisplay display) {
        return stack.has(ModDataComponents.DEPENDENCY_CHECK_TOOLTIP.get())
                && display.shows(ModDataComponents.DEPENDENCY_CHECK_TOOLTIP.get())
                && !ModCompat.CURIOS.isLoaded()
                && !ModCompat.TRINKETS.isLoaded()
                && !ModCompat.ACCESSORIES.isLoaded();
    }

    private static boolean showsCosmeticTooltip(ItemStack stack, TooltipDisplay display) {
        return stack.has(ModDataComponents.COSMETIC_TOOLTIP.get())
                && display.shows(ModDataComponents.COSMETIC_TOOLTIP.get())
                && isCosmetic(stack);
    }
}
