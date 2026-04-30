package artifacts.equipment;

import artifacts.component.ability.EnchantmentLevelModifier;
import artifacts.component.ability.EquipmentAbility;
import artifacts.registry.ModDataComponents;
import artifacts.util.ItemDamageUtil;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.Unit;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import org.jetbrains.annotations.Nullable;

import java.util.function.*;

public class EquipmentHelper {

    public static boolean isDisabledOrBroken(ItemStack stack) {
        if (stack.has(ModDataComponents.DISABLED_BY_TOGGLE.get())) {
            return true;
        }
        return ItemDamageUtil.needsRepair(stack);
    }

    public static boolean hasComponent(DataComponentType<?> type, @Nullable LivingEntity entity) {
        return reduceComponents(type, entity, false, (_, _, _) -> true);
    }

    public static boolean hasAbilityActive(DataComponentType<? extends EquipmentAbility> type, @Nullable LivingEntity entity) {
        return hasAbilityActive(type, entity, true);
    }

    public static boolean hasAbilityActive(DataComponentType<? extends EquipmentAbility> type, @Nullable LivingEntity entity, boolean skipItemsOnCooldown) {
        return hasAbilityActive(type, entity, skipItemsOnCooldown, _ -> true);
    }

    public static <A extends EquipmentAbility> boolean hasAbilityActive(DataComponentType<A> type, @Nullable LivingEntity entity, boolean skipItemsOnCooldown, Predicate<A> predicate) {
        if (entity == null) {
            return false;
        }
        return reduceAbilities(type, entity, skipItemsOnCooldown, true, false, (ability, _, b) -> b || predicate.test(ability));
    }

    public static int getEnchantmentLevelIncrease(ResourceKey<Enchantment> enchantment, LivingEntity entity) {
        return sumInt(ModDataComponents.ENCHANTMENT_LEVEL_MODIFIERS.get(), entity, ability -> {
            int amount = 0;
            for (EnchantmentLevelModifier entry : ability.entries()) {
                if (entry.enchantment().equals(enchantment)) {
                    amount += entry.amount().get();
                }
            }
            return amount;
        }, true);
    }

    public static <A extends EquipmentAbility> int sumInt(DataComponentType<A> type, LivingEntity entity, Function<A, Integer> f, boolean skipItemsOnCooldown) {
        return reduceAbilities(type, entity, skipItemsOnCooldown, true, 0, (ability, _, i) -> i + f.apply(ability));
    }

    public static <A extends EquipmentAbility> double maxDouble(DataComponentType<A> type, LivingEntity entity, Function<A, Double> f, boolean skipItemsOnCooldown) {
        return reduceAbilities(type, entity, skipItemsOnCooldown, true, 0D, (ability, _, d) -> Math.max(d, f.apply(ability)));
    }

    public static <A extends EquipmentAbility> double minDouble(DataComponentType<A> type, LivingEntity entity, double init, Function<A, Double> f, boolean skipItemsOnCooldown) {
        return reduceAbilities(type, entity, skipItemsOnCooldown, true, init, (ability, _, d) -> Math.min(d, f.apply(ability)));
    }

    public static <A extends EquipmentAbility> int maxInt(DataComponentType<A> type, LivingEntity entity, Function<A, Integer> f, boolean skipItemsOnCooldown) {
        return reduceAbilities(type, entity, skipItemsOnCooldown, true, 0, (ability, _, d) -> Math.max(d, f.apply(ability)));
    }

    public static <A extends EquipmentAbility> int minInt(DataComponentType<A> type, LivingEntity entity, int init, Function<A, Integer> f, boolean skipItemsOnCooldown) {
        return reduceAbilities(type, entity, skipItemsOnCooldown, true, init, (ability, _, d) -> Math.min(d, f.apply(ability)));
    }

    public static <A extends EquipmentAbility> void iterateAbilities(DataComponentType<A> type, LivingEntity entity, boolean skipItemsOnCooldown, boolean skipDisabledItems, ComponentVisitor<A> consumer) {
        reduceAbilities(type, entity, skipItemsOnCooldown, skipDisabledItems, Unit.INSTANCE, (ability, slotAccess, _) -> {
            consumer.visit(ability, slotAccess);
            return Unit.INSTANCE;
        });
    }

    public static <ABILITY extends EquipmentAbility, ACC> ACC reduceAbilities(DataComponentType<ABILITY> type, LivingEntity entity, boolean skipItemsOnCooldown, boolean skipDisabledItems, ACC init, ComponentAccumulator<ABILITY, ACC> f) {
        return reduceEquipment(entity, init, (slotAccess, init_) -> {
            ABILITY ability = slotAccess.get().get(type);
            if (ability != null) {
                boolean checkCooldown = !skipItemsOnCooldown || !(entity instanceof Player player) || !player.getCooldowns().isOnCooldown(slotAccess.get());
                boolean checkDisabled = !skipDisabledItems || !isDisabledOrBroken(slotAccess.get());
                boolean checkCosmetic = !skipDisabledItems || ability.isNonCosmetic();
                if (checkCooldown && checkDisabled && checkCosmetic) {
                    init_ = f.accumulate(ability, slotAccess, init_);
                }
            }
            return init_;
        });
    }

    public static <C> void iterateComponents(DataComponentType<C> type, LivingEntity entity, ComponentVisitor<C> visitor) {
        reduceComponents(type, entity, Unit.INSTANCE, (component, stack, _) -> {
            visitor.visit(component, stack);
            return Unit.INSTANCE;
        });
    }

    public static <C, ACC> ACC reduceComponents(DataComponentType<C> type, LivingEntity entity, ACC init, ComponentAccumulator<C, ACC> visitor) {
        return reduceEquipment(entity, init, (slotAccess, acc) -> {
            C component = slotAccess.get().get(type);
            if (component != null) {
                acc = visitor.accumulate(component, slotAccess, acc);
            }
            return acc;
        });
    }

    public static void iterateEquipment(LivingEntity entity, Consumer<ItemStack> consumer) {
        reduceEquipment(entity, Unit.INSTANCE, (slotAccess, unit) -> {
            consumer.accept(slotAccess.get());
            return unit;
        });
    }

    public static <ACC> ACC reduceEquipment(LivingEntity entity, ACC init, BiFunction<EquipmentSlotAccess, ACC, ACC> f) {
        return EquipmentSlotManager.reduceEquipment(entity, init, f);
    }

    @FunctionalInterface
    public interface ComponentAccumulator<C, ACC> {

        ACC accumulate(C element, EquipmentSlotAccess slotAccess, ACC prefix);

    }

    @FunctionalInterface
    public interface ComponentVisitor<C> {

        void visit(C element, EquipmentSlotAccess slotAccess);

    }
}
