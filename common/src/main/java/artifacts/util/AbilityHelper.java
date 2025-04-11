package artifacts.util;

import artifacts.ability.EquipmentAbility;
import artifacts.integration.equipment.EquipmentIntegrationUtils;
import artifacts.registry.ModDataComponents;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.TypedDataComponent;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.*;

// TODO implement foreach through reduction
// TODO cleanup EquipmentIntegrationUtils
public class AbilityHelper {

    public static <A extends EquipmentAbility, T> T reduce(DataComponentType<A> type, LivingEntity entity, boolean skipItemsOnCooldown, boolean skipDisabledItems, T init, BiFunction<A, T, T> f) {
        return EquipmentIntegrationUtils.reduceEquipment(entity, init, (stack, init_) -> {
            A ability = stack.get(type);
            if (ability != null) {
                boolean checkCooldown = !skipItemsOnCooldown || !(entity instanceof Player player) || !player.getCooldowns().isOnCooldown(stack.getItem());
                boolean checkDisabled = !skipDisabledItems || !stack.has(ModDataComponents.DISABLED_BY_TOGGLE.get());
                boolean checkCosmetic = !skipDisabledItems || ability.isNonCosmetic();
                if (checkCooldown && checkDisabled && checkCosmetic) {
                    init_ = f.apply(ability, init_);
                }
            }
            return init_;
        });
    }

    public static boolean hasNonCosmeticAbility(DataComponentType<? extends EquipmentAbility> type, ItemStack stack) {
        return hasNonCosmeticAbility(type, stack, ability -> true);
    }

    public static <T extends EquipmentAbility> boolean hasNonCosmeticAbility(DataComponentType<T> type, ItemStack stack, Predicate<T> predicate) {
        return stack.get(type) instanceof T ability && ability.isNonCosmetic() && predicate.test(ability);
    }

    public static boolean hasAbilityActive(DataComponentType<? extends EquipmentAbility> type, @Nullable LivingEntity entity, boolean skipItemsOnCooldown) {
        return hasAbilityActive(type, entity, skipItemsOnCooldown, ability -> true);
    }

    public static <A extends EquipmentAbility> boolean hasAbilityActive(DataComponentType<A> type, @Nullable LivingEntity entity, boolean skipItemsOnCooldown, Predicate<A> predicate) {
        if (entity == null) {
            return false;
        }
        return reduce(type, entity, skipItemsOnCooldown, true, false, (ability, b) -> b || ability.isNonCosmetic() && predicate.test(ability));
    }

    // TODO replace with iteration
    public static List<EquipmentAbility> getAbilities(ItemStack stack) {
        List<EquipmentAbility> list = new ArrayList<>(0);
        for (TypedDataComponent<?> component : stack.getComponents()) {
            if (component.value() instanceof EquipmentAbility ability) {
                list.add(ability);
            }
        }
        return list;
    }

    // TODO remove
    public static <A extends EquipmentAbility> void iterateNonCosmeticAbilities(DataComponentType<A> type, ItemStack stack, Consumer<A> consumer) {
        // TODO fix compound abilities
        if (stack.get(type) instanceof A ability && ability.isNonCosmetic()) {
            consumer.accept(ability);
        }
    }

    // TODO move to tooltip class
    public static boolean isCosmetic(ItemStack stack) {
        for (TypedDataComponent<?> component : stack.getComponents()) {
            if (component.value() instanceof EquipmentAbility ability && ability.isNonCosmetic()) {
                return false;
            }
        }
        return true;
    }

    public static int getEnchantmentSum(ResourceKey<Enchantment> enchantment, LivingEntity entity) {
        return sumInt(ModDataComponents.INCREASE_ENCHANTMENT_LEVEL.get(), entity, ability ->
                ability.enchantment().equals(enchantment) ? ability.getAmount() : 0, true
        );
    }

    public static <A extends EquipmentAbility> int sumInt(DataComponentType<A> type, LivingEntity entity, Function<A, Integer> f, boolean skipItemsOnCooldown) {
        return reduce(type, entity, skipItemsOnCooldown, true, 0, (ability, i) -> i + f.apply(ability));
    }

    public static <A extends EquipmentAbility> double maxDouble(DataComponentType<A> type, LivingEntity entity, Function<A, Double> f, boolean skipItemsOnCooldown) {
        return reduce(type, entity, skipItemsOnCooldown, true, 0D, (ability, d) -> Math.max(d, f.apply(ability)));
    }

    public static <A extends EquipmentAbility> int maxInt(DataComponentType<A> type, LivingEntity entity, Function<A, Integer> f, boolean skipItemsOnCooldown) {
        return reduce(type, entity, skipItemsOnCooldown, true, 0, (ability, d) -> Math.max(d, f.apply(ability)));
    }

    public static <A extends EquipmentAbility> int minInt(DataComponentType<A> type, LivingEntity entity, int init, Function<A, Integer> f, boolean skipItemsOnCooldown) {
        return reduce(type, entity, skipItemsOnCooldown, true, init, (ability, d) -> Math.min(d, f.apply(ability)));
    }

    public static <A extends EquipmentAbility> void forEach(DataComponentType<A> type, LivingEntity entity, BiConsumer<A, ItemStack> consumer, boolean skipItemsOnCooldown, boolean skipDisabledItems) {
        EquipmentIntegrationUtils.iterateEquipment(entity, stack -> {
            A ability = stack.get(type);
            if (ability != null) {
                boolean checkCooldown = !skipItemsOnCooldown || !(entity instanceof Player player) || !player.getCooldowns().isOnCooldown(stack.getItem());
                boolean checkDisabled = !skipDisabledItems || !stack.has(ModDataComponents.DISABLED_BY_TOGGLE.get());
                boolean checkCosmetic = !skipDisabledItems || ability.isNonCosmetic();
                if (checkCooldown && checkDisabled && checkCosmetic) {
                    consumer.accept(ability, stack);
                }
            }
        });
    }
}
