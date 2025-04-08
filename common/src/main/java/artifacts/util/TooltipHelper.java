package artifacts.util;

import artifacts.ability.AttributeModifierAbility;
import artifacts.registry.ModAbilities;
import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.util.StringUtil;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import org.apache.commons.lang3.mutable.MutableBoolean;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Unique;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import static net.minecraft.world.item.component.ItemAttributeModifiers.ATTRIBUTE_MODIFIER_FORMAT;

public class TooltipHelper {

    public static void addAttributeTooltips(Consumer<Component> consumer, ItemStack stack) {
        ItemAttributeModifiers itemAttributeModifiers = stack.getOrDefault(DataComponents.ATTRIBUTE_MODIFIERS, ItemAttributeModifiers.EMPTY);
        boolean hasSlotTooltip = false;
        if (itemAttributeModifiers.showInTooltip()) {
            for (EquipmentSlot slot : EquipmentSlot.values()) {
                MutableBoolean b = new MutableBoolean(false);
                stack.forEachModifier(slot, (holder, attributeModifier) -> b.setTrue());
                if (b.booleanValue()) {
                    hasSlotTooltip = true;
                    artifacts$addAbilityAttributeTooltips(stack, consumer);
                }
            }
        }
        if (!hasSlotTooltip) {
            if (AbilityHelper.hasAbility(ModAbilities.ATTRIBUTE_MODIFIER.value(), stack)
                    || AbilityHelper.hasAbility(ModAbilities.MOB_EFFECT.value(), stack)
                    || AbilityHelper.hasAbility(ModAbilities.LIMITED_WATER_BREATHING.value(), stack)
            ) {
                consumer.accept(CommonComponents.EMPTY);
                consumer.accept(Component.translatable("item.modifiers.body").withStyle(ChatFormatting.GRAY));
            }
            artifacts$addAbilityAttributeTooltips(stack, consumer);
        }
        addWhenHurtTooltips(consumer, stack);
        addPerFoodPointEatenTooltip(consumer, stack);
        addAttacksInflictTooltip(consumer, stack, false);
        addAttacksInflictTooltip(consumer, stack, true);
    }

    @Unique
    private static void artifacts$addAbilityAttributeTooltips(ItemStack stack, Consumer<Component> tooltip) {
        AbilityHelper.iterateAbilities(ModAbilities.ATTRIBUTE_MODIFIER.value(), stack, ability ->
                artifacts$addAbilityAttributeTooltip(tooltip, ability)
        );
        AbilityHelper.iterateAbilities(ModAbilities.MOB_EFFECT.value(), stack, ability ->
                addMobEffectTooltip(tooltip, ability.mobEffect().value(), ability.duration().get(), ability.level().get(), ability.isInfinite())
        );
        AbilityHelper.iterateAbilities(ModAbilities.LIMITED_WATER_BREATHING.value(), stack, ability ->
                addMobEffectTooltip(tooltip, ability.mobEffect().value(), ability.duration().get(), ability.level().get(), ability.isInfinite())
        );
    }

    @Unique
    private static void artifacts$addAbilityAttributeTooltip(Consumer<Component> tooltip, AttributeModifierAbility ability) {
        double amount = ability.amount().get();

        if (ability.operation() != AttributeModifier.Operation.ADD_VALUE) {
            amount *= 100;
        } else if (ability.attribute().equals(Attributes.KNOCKBACK_RESISTANCE)) {
            amount *= 10;
        }

        if (amount > 0) {
            tooltip.accept(Component.translatable(
                    "attribute.modifier.plus." + ability.operation().id(),
                    ATTRIBUTE_MODIFIER_FORMAT.format(amount),
                    Component.translatable(ability.attribute().value().getDescriptionId())
            ).withStyle(ability.attribute().value().getStyle(true)));
        } else if (amount < 0) {
            amount *= -1;
            tooltip.accept(Component.translatable(
                    "attribute.modifier.take." + ability.operation().id(),
                    ATTRIBUTE_MODIFIER_FORMAT.format(amount),
                    Component.translatable(ability.attribute().value().getDescriptionId())
            ).withStyle(ability.attribute().value().getStyle(false)));
        }
    }

    @Unique
    private static void addWhenHurtTooltips(Consumer<Component> tooltip, ItemStack stack) {
        MutableBoolean flag = new MutableBoolean(false);
        List<TagKey<DamageType>> list = new ArrayList<>();
        AbilityHelper.iterateAbilities(ModAbilities.APPLY_MOB_EFFECT_AFTER_DAMAGE.value(), stack, ability -> {
            if (ability.tag().isEmpty()) {
                flag.setTrue();
            } else if (!list.contains(ability.tag().get())) {
                list.add(ability.tag().get());
            }
        });
        AbilityHelper.iterateAbilities(ModAbilities.APPLY_COOLDOWN_AFTER_DAMAGE.value(), stack, ability -> {
            if (ability.tag().isEmpty()) {
                flag.setTrue();
            } else if (!list.contains(ability.tag().get())) {
                list.add(ability.tag().get());
            }
        });

        if (flag.booleanValue()) {
            tooltip.accept(CommonComponents.EMPTY);
            tooltip.accept(Component.translatable("artifacts.tooltip.when_hurt").withStyle(ChatFormatting.GRAY));
            addWhenHurtTooltip(tooltip, stack, null);
        }
        for (TagKey<DamageType> tag : list) {
            tooltip.accept(CommonComponents.EMPTY);
            tooltip.accept(Component.translatable("artifacts.tooltip.when_hurt.%s".formatted(
                    tag.location()
                            .toString()
                            .replace("minecraft:", "")
                            .replace(':', '.')
            )).withStyle(ChatFormatting.GRAY));
            addWhenHurtTooltip(tooltip, stack, tag);
        }
    }

    private static void addWhenHurtTooltip(Consumer<Component> tooltip, ItemStack stack, @Nullable TagKey<DamageType> tag) {
        AbilityHelper.iterateAbilities(ModAbilities.APPLY_MOB_EFFECT_AFTER_DAMAGE.value(), stack, ability -> {
            if (ability.tag().isEmpty() && tag == null || ability.tag().isPresent() && ability.tag().get().equals(tag)) {
                addMobEffectTooltip(tooltip, ability.mobEffect().value(), ability.duration().get(), ability.level().get(), false);
            }
        });
        AbilityHelper.iterateAbilities(ModAbilities.APPLY_COOLDOWN_AFTER_DAMAGE.value(), stack, ability -> {
            if (ability.tag().isEmpty() && tag == null || ability.tag().isPresent() && ability.tag().get().equals(tag)) {
                tooltip.accept(Component.translatable("artifacts.tooltip.cooldown", formatDurationSeconds(ability.cooldown().get())).withStyle(ChatFormatting.GOLD));
            }
        });
    }

    private static void addPerFoodPointEatenTooltip(Consumer<Component> tooltip, ItemStack stack) {
        if (AbilityHelper.hasAbility(ModAbilities.APPLY_MOB_EFFECT_AFTER_EATING.value(), stack)) {
            tooltip.accept(CommonComponents.EMPTY);
            tooltip.accept(Component.translatable("artifacts.tooltip.per_food_point_restored").withStyle(ChatFormatting.GRAY));
            AbilityHelper.iterateAbilities(ModAbilities.APPLY_MOB_EFFECT_AFTER_EATING.value(), stack, ability ->
                    addMobEffectTooltip(tooltip, ability.mobEffect().value(), ability.duration().get(), ability.level().get(), false)
            );
        }
    }

    private static void addAttacksInflictTooltip(Consumer<Component> tooltip, ItemStack stack, boolean chance) {
        if (AbilityHelper.hasAbility(ModAbilities.ATTACKS_INFLICT_MOB_EFFECT.value(), stack,
                ability -> chance ^ Mth.equal(ability.chance().get(), 1)
        )) {
            tooltip.accept(CommonComponents.EMPTY);
            tooltip.accept(Component.translatable("artifacts.tooltip.attacks_inflict." + (chance ? "chance" : "constant")).withStyle(ChatFormatting.GRAY));
            AbilityHelper.iterateAbilities(ModAbilities.ATTACKS_INFLICT_MOB_EFFECT.value(), stack, ability -> {
                addMobEffectTooltip(tooltip, ability.mobEffect().value(), ability.duration().get(), ability.level().get(), false);
                if (ability.cooldown().get() > 0) {
                    tooltip.accept(Component.translatable("artifacts.tooltip.cooldown", formatDurationSeconds(ability.cooldown().get())).withStyle(ChatFormatting.GOLD));
                }
            });
        }
    }

    private static void addMobEffectTooltip(Consumer<Component> tooltip, MobEffect mobEffect, int duration, int level, boolean isInfinite) {
        MutableComponent mutableComponent;
        mutableComponent = Component.translatable(mobEffect.getDescriptionId());
        if (level > 1) {
            mutableComponent = Component.translatable("potion.withAmplifier", mutableComponent, Component.translatable("potion.potency." + (level - 1)));
        }
        if (!isInfinite) {
            mutableComponent = Component.translatable("potion.withDuration", mutableComponent, formatDurationSeconds(duration));
        }
        tooltip.accept(Component.translatable("artifacts.tooltip.plus_mob_effect", mutableComponent).withStyle(mobEffect.getCategory().getTooltipFormatting()));
    }

    private static MutableComponent formatDurationSeconds(int seconds) {
        // TODO use correct tick rate
        return Component.literal(StringUtil.formatTickDuration(seconds * 20, 20));
    }
}
