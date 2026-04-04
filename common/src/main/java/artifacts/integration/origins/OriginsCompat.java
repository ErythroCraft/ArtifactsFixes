package artifacts.integration.origins;

import io.github.apace100.apoli.component.PowerHolderComponent;
import io.github.apace100.origins.power.type.WaterBreathingPowerType;
import net.minecraft.world.entity.LivingEntity;

public class OriginsCompat {

    public static boolean hasWaterBreathing(LivingEntity entity) {
        return PowerHolderComponent.hasPowerType(entity, WaterBreathingPowerType.class);
    }
}
