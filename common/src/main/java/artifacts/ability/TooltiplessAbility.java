package artifacts.ability;

import net.minecraft.network.chat.MutableComponent;

import java.util.List;

public interface TooltiplessAbility extends EquipmentAbility {

    @Override
    default void addTooltipIfNonCosmetic(List<MutableComponent> tooltip) {

    }
}
