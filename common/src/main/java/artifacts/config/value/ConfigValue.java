package artifacts.config.value;

import artifacts.config.value.type.ValueType;
import net.minecraft.util.StringRepresentable;

public final class ConfigValue<T> implements Value<T>, StringRepresentable {

    private final ValueType<T, ?> type;
    private final String id;
    private final T defaultValue;
    private final boolean requiresRestart;

    private T value;

    public ConfigValue(ValueType<T, ?> type, String id, T defaultValue, boolean requiresRestart) {
        this.type = type;
        this.id = id;
        this.requiresRestart = requiresRestart;
        this.defaultValue = defaultValue;
        this.value = defaultValue;
    }

    public String getId() {
        return id;
    }

    public String getSerializedName() {
        return id;
    }

    public T get() {
        return value;
    }

    public void set(T value) {
        this.value = value;
    }

    public T getDefaultValue() {
        return defaultValue;
    }

    public ValueType<T, ?> type() {
        return type;
    }

    public boolean requiresRestart() {
        return requiresRestart;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof ConfigValue<?> that)) return false;
        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
