package artifacts.integration;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class VanillaEquipmentIntegration implements EquipmentIntegration {

    @Override
    public void setup() {}

    @Override
    public Stream<ItemStack> findAllEquippedBy(LivingEntity entity, Predicate<ItemStack> predicate) {
        return StreamSupport.stream(entity.getArmorAndBodyArmorSlots().spliterator(), false).filter(predicate);
    }

    @Override
    public void iterateEquippedAccessories(LivingEntity entity, Consumer<ItemStack> consumer) {
        for (ItemStack item : entity.getArmorAndBodyArmorSlots()) {
            if (!item.isEmpty()) consumer.accept(item);
        }
    }

    @Override
    public <T> T reduceAccessories(LivingEntity entity, T init, BiFunction<ItemStack, T, T> f) {
        for (ItemStack item : entity.getArmorAndBodyArmorSlots()) {
            if (!item.isEmpty()) init = f.apply(item, init);
        }
        return init;
    }

    @Override
    public boolean equipAccessory(LivingEntity entity, ItemStack stack) {
        return false;
    }

    @Override
    public String name() {
        return "minecraft";
    }
}
