package artifacts.util;

import artifacts.component.ability.EquipmentAbility;
import artifacts.integration.equipment.EquipmentIntegrationUtils;
import artifacts.registry.ModDataComponents;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.Unit;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import org.apache.commons.lang3.function.TriFunction;
import org.jetbrains.annotations.Nullable;

import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Predicate;

// TODO fix compound abilities (mob effects/attributes)
// TODO render mob effects as infinite in inventory
public class AbilityHelper {

    public static boolean hasComponent(DataComponentType<?> type, @Nullable LivingEntity entity) {
        return reduceComponents(type, entity, false, (prefix, stack, component) -> true);
    }

    public static boolean hasAbilityActive(DataComponentType<? extends EquipmentAbility> type, @Nullable LivingEntity entity, boolean skipItemsOnCooldown) {
        return hasAbilityActive(type, entity, skipItemsOnCooldown, ability -> true);
    }

    public static <A extends EquipmentAbility> boolean hasAbilityActive(DataComponentType<A> type, @Nullable LivingEntity entity, boolean skipItemsOnCooldown, Predicate<A> predicate) {
        if (entity == null) {
            return false;
        }
        return reduceAbilities(type, entity, skipItemsOnCooldown, true, false, (ability, stack, b) -> b || ability.isNonCosmetic() && predicate.test(ability));
    }

    public static int getEnchantmentSum(ResourceKey<Enchantment> enchantment, LivingEntity entity) {
        return sumInt(ModDataComponents.INCREASE_ENCHANTMENT_LEVEL.get(), entity, ability ->
                ability.enchantment().equals(enchantment) ? ability.getAmount() : 0, true
        );
    }

    public static <A extends EquipmentAbility> int sumInt(DataComponentType<A> type, LivingEntity entity, Function<A, Integer> f, boolean skipItemsOnCooldown) {
        return reduceAbilities(type, entity, skipItemsOnCooldown, true, 0, (ability, stack, i) -> i + f.apply(ability));
    }

    public static <A extends EquipmentAbility> double maxDouble(DataComponentType<A> type, LivingEntity entity, Function<A, Double> f, boolean skipItemsOnCooldown) {
        return reduceAbilities(type, entity, skipItemsOnCooldown, true, 0D, (ability, stack, d) -> Math.max(d, f.apply(ability)));
    }

    public static <A extends EquipmentAbility> int maxInt(DataComponentType<A> type, LivingEntity entity, Function<A, Integer> f, boolean skipItemsOnCooldown) {
        return reduceAbilities(type, entity, skipItemsOnCooldown, true, 0, (ability, stack, d) -> Math.max(d, f.apply(ability)));
    }

    public static <A extends EquipmentAbility> int minInt(DataComponentType<A> type, LivingEntity entity, int init, Function<A, Integer> f, boolean skipItemsOnCooldown) {
        return reduceAbilities(type, entity, skipItemsOnCooldown, true, init, (ability, stack, d) -> Math.min(d, f.apply(ability)));
    }

    public static <A extends EquipmentAbility> void iterateAbilities(DataComponentType<A> type, LivingEntity entity, boolean skipItemsOnCooldown, boolean skipDisabledItems, BiConsumer<A, ItemStack> consumer) {
        reduceAbilities(type, entity, skipItemsOnCooldown, skipDisabledItems, Unit.INSTANCE, (ability, stack, unit) -> {
            consumer.accept(ability, stack);
            return Unit.INSTANCE;
        });
    }

    public static <ABILITY extends EquipmentAbility, ACC> ACC reduceAbilities(DataComponentType<ABILITY> type, LivingEntity entity, boolean skipItemsOnCooldown, boolean skipDisabledItems, ACC init, TriFunction<ABILITY, ItemStack, ACC, ACC> f) {
        return EquipmentIntegrationUtils.reduceEquipment(entity, init, (stack, init_) -> {
            ABILITY ability = stack.get(type);
            if (ability != null) {
                boolean checkCooldown = !skipItemsOnCooldown || !(entity instanceof Player player) || !player.getCooldowns().isOnCooldown(stack.getItem());
                boolean checkDisabled = !skipDisabledItems || !stack.has(ModDataComponents.DISABLED_BY_TOGGLE.get());
                boolean checkCosmetic = !skipDisabledItems || ability.isNonCosmetic();
                if (checkCooldown && checkDisabled && checkCosmetic) {
                    init_ = f.apply(ability, stack, init_);
                }
            }
            return init_;
        });
    }

    public static <C> void iterateComponents(DataComponentType<C> type, LivingEntity entity, ComponentVisitor<C> visitor) {
        reduceComponents(type, entity, Unit.INSTANCE, (unit, stack, component) -> {
            visitor.visit(stack, component);
            return Unit.INSTANCE;
        });
    }

    public static <C, ACC> ACC reduceComponents(DataComponentType<C> type, LivingEntity entity, ACC init, ComponentAccumulator<C, ACC> visitor) {
        return EquipmentIntegrationUtils.reduceEquipment(entity, init, (stack, acc) -> {
            C component = stack.get(type);
            if (component != null) {
                acc = visitor.accumulate(acc, stack, component);
            }
            return acc;
        });
    }

    @FunctionalInterface
    public interface ComponentAccumulator<COMPONENT, T> {

        T accumulate(T prefix, ItemStack stack, COMPONENT component);

    }

    @FunctionalInterface
    public interface ComponentVisitor<COMPONENT> {

        void visit(ItemStack stack, COMPONENT component);

    }
}
