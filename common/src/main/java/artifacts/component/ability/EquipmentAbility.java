package artifacts.component.ability;

import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;

import java.util.Objects;
import java.util.function.Consumer;

public interface EquipmentAbility {

    boolean isNonCosmetic();

    default void addToTooltip(TooltipWriter writer) {
        writer.addDefaultTooltipKey();
    }

    class TooltipWriter {

        private final Consumer<Component> tooltip;
        private final String namespace;
        private final String path;
        private final Item.TooltipContext context;

        public TooltipWriter(DataComponentType<? extends EquipmentAbility> type, Consumer<Component> tooltip, Item.TooltipContext context) {
            this.tooltip = tooltip;
            ResourceLocation id = Objects.requireNonNull(BuiltInRegistries.DATA_COMPONENT_TYPE.getKey(type));
            this.namespace = id.getNamespace();
            this.path = id.getPath();
            this.context = context;
        }

        public Item.TooltipContext context() {
            return context;
        }

        public TooltipWriter add(String identifier, Object... args) {
            return addRaw(Component.translatable("%s.tooltip.ability.%s.%s".formatted(namespace, path, identifier), args).withStyle(ChatFormatting.GRAY));
        }

        public TooltipWriter addDefaultTooltipKey(Object... args) {
            return addRaw(Component.translatable("%s.tooltip.ability.%s".formatted(namespace, path), args).withStyle(ChatFormatting.GRAY));
        }

        public TooltipWriter addRaw(Component component) {
            tooltip.accept(component);
            return this;
        }
    }
}
