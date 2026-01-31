package artifacts.fabric.component;

import artifacts.component.SwimData;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.ladysnake.cca.api.v3.component.ComponentV3;

public class SwimDataComponent extends SwimData implements ComponentV3 {

    @Override
    public void readData(ValueInput input) {
        isSwimming = input.getBooleanOr("ShouldSwim", false);
        hasTouchedWater = input.getBooleanOr("HasTouchedWater", false);
        swimProgress = input.getDoubleOr("SwimProgress", 0);
    }

    @Override
    public void writeData(ValueOutput output) {
        output.putBoolean("ShouldSwim", isSwimming);
        output.putBoolean("HasTouchedWater", hasTouchedWater);
        output.putDouble("SwimProgress", swimProgress);
    }
}
