package artifacts.equipment;

import artifacts.component.ability.EquipmentAbility;
import artifacts.registry.ComponentType;
import artifacts.registry.ModDataComponents;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.Unit;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import org.jetbrains.annotations.Nullable;

import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

public class EquipmentHelper {

    public static boolean hasComponent(ComponentType<?, ?> type, @Nullable LivingEntity entity) {
        return reduceComponents(type, entity, false, false, false, (_, _, _) -> true);
    }

    public static boolean hasAbilityActive(ComponentType<?, ? extends EquipmentAbility> type, @Nullable LivingEntity entity) {
        return hasAbilityActive(type, entity, true);
    }

    public static boolean hasAbilityActive(ComponentType<?, ? extends EquipmentAbility> type, @Nullable LivingEntity entity, boolean skipItemsOnCooldown) {
        return hasAbilityActive(type, entity, skipItemsOnCooldown, _ -> true);
    }

    public static <A extends EquipmentAbility> boolean hasAbilityActive(ComponentType<?, A> type, @Nullable LivingEntity entity, boolean skipItemsOnCooldown, Predicate<A> predicate) {
        if (entity == null) {
            return false;
        }
        return reduceComponents(type, entity, skipItemsOnCooldown, true, false, (ability, _, b) -> b || predicate.test(ability));
    }

    public static int getEnchantmentLevelIncrease(ResourceKey<Enchantment> enchantment, LivingEntity entity) {
        return sumInt(ModDataComponents.ENCHANTMENT_LEVEL_MODIFIERS, entity, ability -> {
            if (ability.enchantment().equals(enchantment)) {
                return ability.amount().get();
            }
            return 0;
        }, true);
    }

    public static <C, E> int sumInt(ComponentType<C, E> type, LivingEntity entity, Function<E, Integer> f, boolean skipItemsOnCooldown) {
        return reduceComponents(type, entity, skipItemsOnCooldown, true, 0, (ability, _, i) -> i + f.apply(ability));
    }

    public static <C, E> double maxDouble(ComponentType<C, E> type, LivingEntity entity, Function<E, Double> f, boolean skipItemsOnCooldown) {
        return reduceComponents(type, entity, skipItemsOnCooldown, true, 0D, (ability, _, d) -> Math.max(d, f.apply(ability)));
    }

    public static <C, E> double minDouble(ComponentType<C, E> type, LivingEntity entity, double init, Function<E, Double> f, boolean skipItemsOnCooldown) {
        return reduceComponents(type, entity, skipItemsOnCooldown, true, init, (ability, _, d) -> Math.min(d, f.apply(ability)));
    }

    public static <C, E> int maxInt(ComponentType<C, E> type, LivingEntity entity, Function<E, Integer> f, boolean skipItemsOnCooldown) {
        return reduceComponents(type, entity, skipItemsOnCooldown, true, 0, (ability, _, d) -> Math.max(d, f.apply(ability)));
    }

    public static <C, E> int minInt(ComponentType<C, E> type, LivingEntity entity, int init, Function<E, Integer> f, boolean skipItemsOnCooldown) {
        return reduceComponents(type, entity, skipItemsOnCooldown, true, init, (ability, _, d) -> Math.min(d, f.apply(ability)));
    }

    public static <C, E> void iterateComponents(ComponentType<C, E> type, LivingEntity entity, boolean skipItemsOnCooldown, boolean skipDisabledItems, ComponentVisitor<E> visitor) {
        reduceComponents(type, entity, skipItemsOnCooldown, skipDisabledItems, Unit.INSTANCE, (component, stack, _) -> {
            visitor.visit(component, stack);
            return Unit.INSTANCE;
        });
    }

    public static <C, E, ACC> ACC reduceComponents(ComponentType<C, E> type, LivingEntity entity, boolean skipItemsOnCooldown, boolean skipDisabledItems, ACC init, ComponentAccumulator<E, ACC> visitor) {
        return reduceEquipment(entity, skipItemsOnCooldown, skipDisabledItems, init, (slotAccess, acc) -> {
            for (E entry : type.getEntries(slotAccess.get())) {
                // TODO: consider removing instanceof check & making ability isNonCosmetic checks explicit
                if (!skipDisabledItems || !(entry instanceof EquipmentAbility ability) || ability.isNonCosmetic()) {
                    acc = visitor.accumulate(entry, slotAccess, acc);
                }
            }
            return acc;
        });
    }

    public static void iterateEquipment(LivingEntity entity, boolean skipItemsOnCooldown, boolean skipDisabledItems, Consumer<ItemStack> consumer) {
        reduceEquipment(entity, skipItemsOnCooldown, skipDisabledItems, Unit.INSTANCE, (slotAccess, unit) -> {
            consumer.accept(slotAccess.get());
            return unit;
        });
    }

    public static <ACC> ACC reduceEquipment(LivingEntity entity, boolean skipItemsOnCooldown, boolean skipDisabledItems, ACC init, BiFunction<EquipmentSlotAccess, ACC, ACC> f) {
        return EquipmentSlotManager.reduceEquipment(entity, init, ((slotAccess, acc) -> {
            boolean checkCooldown = !skipItemsOnCooldown || !slotAccess.isOnCooldown(entity);
            boolean checkDisabled = !skipDisabledItems || !slotAccess.isDisabledOrBroken();
            if (checkCooldown && checkDisabled) {
                return f.apply(slotAccess, acc);
            }
            return acc;
        }));
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
