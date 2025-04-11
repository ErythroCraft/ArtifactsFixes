package artifacts.ability;

import artifacts.Artifacts;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;

import java.util.List;

public interface EquipmentAbility {

    boolean isNonCosmetic();

    default void addTooltipIfNonCosmetic(List<MutableComponent> tooltip) {
        if (isNonCosmetic()) {
            addAbilityTooltip(tooltip);
        }
    }

    @SuppressWarnings("ConstantConditions")
    default void addAbilityTooltip(List<MutableComponent> tooltip) {
        ResourceLocation id = Artifacts.id(""); // TODO
        tooltip.add(Component.translatable("%s.tooltip.ability.%s".formatted(id.getNamespace(), id.getPath())));
    }

    @SuppressWarnings("ConstantConditions")
    default MutableComponent tooltipLine(String abilityName, Object... args) {
        ResourceLocation id = Artifacts.id(""); // TODO ModAbilities.getRegistry().getKey(getType());
        return Component.translatable("%s.tooltip.ability.%s.%s".formatted(id.getNamespace(), id.getPath(), abilityName), args);
    }
}
