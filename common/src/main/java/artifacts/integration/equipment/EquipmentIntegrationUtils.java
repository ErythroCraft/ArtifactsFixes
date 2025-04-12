package artifacts.integration.equipment;

import net.minecraft.util.Unit;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Consumer;

public class EquipmentIntegrationUtils {

    private static final Map<String, EquipmentIntegration> INTEGRATIONS = new LinkedHashMap<>();

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

    public static void iterateEquipment(LivingEntity entity, Consumer<ItemStack> consumer) {
        reduceEquipment(entity, Unit.INSTANCE, (stack, unit) -> {
            consumer.accept(stack);
            return unit;
        });
    }

    public static <T> T reduceEquipment(LivingEntity entity, T init, BiFunction<ItemStack, T, T> f) {
        for (EquipmentIntegration integration : INTEGRATIONS.values()) {
            init = integration.reduceEquipment(entity, init, f);
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
