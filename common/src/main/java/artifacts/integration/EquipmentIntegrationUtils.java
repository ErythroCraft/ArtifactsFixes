package artifacts.integration;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class EquipmentIntegrationUtils {

    private static final Map<String, EquipmentIntegration> INTEGRATIONS = new LinkedHashMap<>();

    @Nullable
    public static EquipmentIntegration getIntegration(String name) {
        return INTEGRATIONS.get(name);
    }

    public static void setupIntegrations() {
        INTEGRATIONS.values().forEach(EquipmentIntegration::setup);
    }

    public static void registerIntegration(EquipmentIntegration integration) {
        String name = integration.name();

        if (INTEGRATIONS.containsKey(name)) {
            throw new IllegalStateException("Duplicate Equipment Integration detected! [Name: " + name + "]");
        }

        INTEGRATIONS.put(name, integration);
    }

    public static Stream<ItemStack> findAllEquippedBy(LivingEntity entity, Predicate<ItemStack> predicate) {
        Stream<ItemStack> allEquippedStacks = Stream.of();

        for (EquipmentIntegration integration : INTEGRATIONS.values()) {
            allEquippedStacks = Stream.concat(allEquippedStacks, integration.findAllEquippedBy(entity, predicate));
        }

        return allEquippedStacks;
    }

    public static void iterateEquippedAccessories(LivingEntity entity, Consumer<ItemStack> consumer) {
        for (EquipmentIntegration integration : INTEGRATIONS.values()) {
            integration.iterateEquippedAccessories(entity, consumer);
        }
    }

    public static <T> T reduceAccessories(LivingEntity entity, T init, BiFunction<ItemStack, T, T> f) {
        for (EquipmentIntegration integration : INTEGRATIONS.values()) {
            init = integration.reduceAccessories(entity, init, f);
        }

        return init;
    }

    public static boolean equipAccessory(LivingEntity entity, ItemStack stack) {
        for (EquipmentIntegration integration : INTEGRATIONS.values()) {
            if (integration.equipAccessory(entity, stack)) {
                return true;
            }
        }

        return false;
    }
}
