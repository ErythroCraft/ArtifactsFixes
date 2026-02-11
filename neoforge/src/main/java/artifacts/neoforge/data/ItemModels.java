package artifacts.neoforge.data;

import artifacts.Artifacts;
import net.minecraft.client.data.models.ModelProvider;
import net.minecraft.data.PackOutput;

public class ItemModels extends ModelProvider {

    public ItemModels(PackOutput packOutput) {
        super(packOutput, Artifacts.MOD_ID);
    }
}
