package artifacts.registry;

import artifacts.component.CompositeComponent;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

public abstract class ComponentType<C, E> implements Supplier<DataComponentType<C>> {

    private final Supplier<DataComponentType<C>> type;

    public ComponentType(Supplier<DataComponentType<C>> type) {
        this.type = type;
    }

    public Iterable<E> getEntries(ItemStack stack) {
        return getEntries(stack.get(type.get()));
    }

    public Iterable<E> getEntries(@Nullable C component) {
        if (component == null) {
            return List.of();
        }
        return extractEntries(component);
    }

    protected abstract Iterable<E> extractEntries(C component);

    public DataComponentType<C> get() {
        return type.get();
    }

    public static class Singleton<T> extends ComponentType<T, T> {

        public Singleton(Supplier<DataComponentType<T>> type) {
            super(type);
        }

        @Override
        public Iterable<T> extractEntries(@Nullable T component) {
            return Collections.singleton(component);
        }
    }

    public static class Composite<T> extends ComponentType<CompositeComponent<T>, T> {

        public Composite(Supplier<DataComponentType<CompositeComponent<T>>> type) {
            super(type);
        }

        @Override
        public Iterable<T> extractEntries(CompositeComponent<T> component) {
            return component.entries();
        }
    }
}
