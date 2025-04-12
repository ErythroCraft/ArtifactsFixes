package artifacts.util;

import artifacts.component.ability.AttributeModifierAbility;
import artifacts.component.ability.EquipmentAbility;
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
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import org.apache.commons.lang3.mutable.MutableBoolean;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Unique;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

import static net.minecraft.world.item.component.ItemAttributeModifiers.ATTRIBUTE_MODIFIER_FORMAT;

public class TooltipHelper {

    public static void addAttributeTooltips(Consumer<Component> consumer, ItemStack stack, Item.TooltipContext context) {
        ItemAttributeModifiers itemAttributeModifiers = stack.getOrDefault(DataComponents.ATTRIBUTE_MODIFIERS, ItemAttributeModifiers.EMPTY);
        boolean hasSlotTooltip = false;
        if (itemAttributeModifiers.showInTooltip()) {
            for (EquipmentSlot slot : EquipmentSlot.values()) {
                MutableBoolean b = new MutableBoolean(false);
                stack.forEachModifier(slot, (holder, attributeModifier) -> b.setTrue());
                if (b.booleanValue()) {
                    hasSlotTooltip = true;
                    addAbilityAttributeTooltips(stack, context, consumer);
                }
            }
        }
        if (!hasSlotTooltip) {
            if (getAbility(ModDataComponents.ATTRIBUTE_MODIFIER.get(), stack).isPresent()
                    || getAbility(ModDataComponents.MOB_EFFECT.get(), stack).isPresent()
                    || getAbility(ModDataComponents.LIMITED_WATER_BREATHING.get(), stack).isPresent()
                    || getAbility(ModDataComponents.NIGHT_VISION.get(), stack).isPresent()
            ) {
                consumer.accept(CommonComponents.EMPTY);
                consumer.accept(Component.translatable("item.modifiers.body").withStyle(ChatFormatting.GRAY));
            }
            addAbilityAttributeTooltips(stack, context, consumer);
        }
        addWhenHurtTooltips(consumer, context, stack);
        addPerFoodPointEatenTooltip(consumer, context, stack);
        addAttacksInflictTooltip(consumer, context, stack, false);
        addAttacksInflictTooltip(consumer, context, stack, true);
    }

    @Unique
    private static void addAbilityAttributeTooltips(ItemStack stack, Item.TooltipContext context, Consumer<Component> tooltip) {
        getAbility(ModDataComponents.ATTRIBUTE_MODIFIER.get(), stack).ifPresent(ability ->
                addAbilityAttributeTooltip(tooltip, ability)
        );
        getAbility(ModDataComponents.MOB_EFFECT.get(), stack).ifPresent(ability ->
                addMobEffectTooltip(tooltip, context, ability.mobEffect().value(), ability.duration().get(), ability.level().get(), ability.isInfinite())
        );
        getAbility(ModDataComponents.LIMITED_WATER_BREATHING.get(), stack).ifPresent(ability ->
                addMobEffectTooltip(tooltip, context, ability.mobEffect().value(), ability.duration().get(), ability.level().get(), ability.isInfinite())
        );
        getAbility(ModDataComponents.NIGHT_VISION.get(), stack).ifPresent(ability ->
                addMobEffectTooltip(tooltip, context, ability.mobEffect().value(), ability.duration().get(), ability.level().get(), ability.isInfinite())
        );
    }

    @Unique
    private static void addAbilityAttributeTooltip(Consumer<Component> tooltip, AttributeModifierAbility ability) {
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
    private static void addWhenHurtTooltips(Consumer<Component> tooltip, Item.TooltipContext context, ItemStack stack) {
        MutableBoolean flag = new MutableBoolean(false);
        List<TagKey<DamageType>> list = new ArrayList<>();
        getAbility(ModDataComponents.APPLY_MOB_EFFECT_AFTER_DAMAGE.get(), stack).ifPresent(ability -> {
            if (ability.tag().isEmpty()) {
                flag.setTrue();
            } else {
                list.add(ability.tag().get());
            }
        });
        getAbility(ModDataComponents.APPLY_COOLDOWN_AFTER_DAMAGE.get(), stack).ifPresent(ability -> {
            if (ability.tag().isEmpty()) {
                flag.setTrue();
            } else if (!list.contains(ability.tag().get())) {
                list.add(ability.tag().get());
            }
        });

        if (flag.booleanValue()) {
            tooltip.accept(CommonComponents.EMPTY);
            tooltip.accept(Component.translatable("artifacts.tooltip.when_hurt").withStyle(ChatFormatting.GRAY));
            addWhenHurtTooltip(tooltip, context, stack, null);
        }
        for (TagKey<DamageType> tag : list) {
            tooltip.accept(CommonComponents.EMPTY);
            tooltip.accept(Component.translatable("artifacts.tooltip.when_hurt.%s".formatted(
                    tag.location()
                            .toString()
                            .replace("minecraft:", "")
                            .replace(':', '.')
            )).withStyle(ChatFormatting.GRAY));
            addWhenHurtTooltip(tooltip, context, stack, tag);
        }
    }

    private static void addWhenHurtTooltip(Consumer<Component> tooltip, Item.TooltipContext context, ItemStack stack, @Nullable TagKey<DamageType> tag) {
        getAbility(ModDataComponents.APPLY_MOB_EFFECT_AFTER_DAMAGE.get(), stack).ifPresent(ability -> {
            if (ability.tag().isEmpty() && tag == null || ability.tag().isPresent() && ability.tag().get().equals(tag)) {
                addMobEffectTooltip(tooltip, context, ability.mobEffect().value(), ability.duration().get(), ability.level().get(), false);
            }
        });
        getAbility(ModDataComponents.APPLY_COOLDOWN_AFTER_DAMAGE.get(), stack).ifPresent(ability -> {
            if (ability.tag().isEmpty() && tag == null || ability.tag().isPresent() && ability.tag().get().equals(tag)) {
                tooltip.accept(Component.translatable("artifacts.tooltip.cooldown", formatDurationSeconds(context, ability.cooldown().get())).withStyle(ChatFormatting.GOLD));
            }
        });
    }

    private static void addPerFoodPointEatenTooltip(Consumer<Component> tooltip, Item.TooltipContext context, ItemStack stack) {
        getAbility(ModDataComponents.APPLY_MOB_EFFECT_AFTER_EATING.get(), stack).ifPresent(ability -> {
            tooltip.accept(CommonComponents.EMPTY);
            tooltip.accept(Component.translatable("artifacts.tooltip.per_food_point_restored").withStyle(ChatFormatting.GRAY));
            addMobEffectTooltip(tooltip, context, ability.mobEffect().value(), ability.duration().get(), ability.level().get(), false);
        });
    }

    private static void addAttacksInflictTooltip(Consumer<Component> tooltip, Item.TooltipContext context, ItemStack stack, boolean chance) {
        getAbility(ModDataComponents.ATTACKS_INFLICT_MOB_EFFECT.get(), stack).ifPresent(ability -> {
            if (chance ^ Mth.equal(ability.chance().get(), 1)) {
                addMobEffectTooltip(tooltip, context, ability.mobEffect().value(), ability.duration().get(), ability.level().get(), false);
                if (ability.cooldown().get() > 0) {
                    tooltip.accept(Component.translatable("artifacts.tooltip.cooldown", formatDurationSeconds(context, ability.cooldown().get())).withStyle(ChatFormatting.GOLD));
                }
            }
        });
    }

    private static void addMobEffectTooltip(Consumer<Component> tooltip, Item.TooltipContext context, MobEffect mobEffect, int duration, int level, boolean isInfinite) {
        MutableComponent mutableComponent;
        mutableComponent = Component.translatable(mobEffect.getDescriptionId());
        if (level > 1) {
            mutableComponent = Component.translatable("potion.withAmplifier", mutableComponent, Component.translatable("potion.potency." + (level - 1)));
        }
        if (!isInfinite) {
            mutableComponent = Component.translatable("potion.withDuration", mutableComponent, formatDurationSeconds(context, duration));
        }
        tooltip.accept(Component.translatable("artifacts.tooltip.plus_mob_effect", mutableComponent).withStyle(mobEffect.getCategory().getTooltipFormatting()));
    }

    private static MutableComponent formatDurationSeconds(Item.TooltipContext context, int seconds) {
        return Component.literal(StringUtil.formatTickDuration(seconds * 20, context.tickRate()));
    }

    public static <A extends EquipmentAbility> Optional<A> getAbility(DataComponentType<A> type, ItemStack stack) {
        if (stack.get(type) instanceof A ability && ability.isNonCosmetic()) {
            return Optional.of(ability);
        }
        return Optional.empty();
    }

    public static boolean isCosmetic(ItemStack stack) {
        for (TypedDataComponent<?> component : stack.getComponents()) {
            if (component.value() instanceof EquipmentAbility ability && ability.isNonCosmetic()) {
                return false;
            }
        }
        return true;
    }
}
