package artifacts.neoforge.platform;

import artifacts.platform.ModListProvider;
import net.neoforged.fml.ModList;

public class NeoForgeModListProvider implements ModListProvider {

    @Override
    public boolean isModLoaded(String modid) {
        return ModList.get().isLoaded(modid);
    }
}
