package artifacts.registry;

import artifacts.platform.PlatformServices;
import artifacts.world.placement.CampsiteCountPlacement;
import artifacts.world.placement.CampsiteHeightRangePlacement;
import artifacts.world.placement.CeilingHeightFilter;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.placement.PlacementModifierType;

import java.util.function.Supplier;

public class ModPlacementModifierTypes {

    public static final Register<PlacementModifierType<?>> PLACEMENT_MODIFIER_TYPES = PlatformServices.platformHelper.createRegister(Registries.PLACEMENT_MODIFIER_TYPE);

    public static final RegistryHolder<PlacementModifierType<?>, PlacementModifierType<CeilingHeightFilter>> CEILING_HEIGHT_FILTER = register("ceiling_height_filter", () -> () -> CeilingHeightFilter.CODEC);
    public static final RegistryHolder<PlacementModifierType<?>, PlacementModifierType<CampsiteCountPlacement>> CAMPSITE_COUNT = register("campsite_count", () -> () -> CampsiteCountPlacement.CODEC);
    public static final RegistryHolder<PlacementModifierType<?>, PlacementModifierType<CampsiteHeightRangePlacement>> CAMPSITE_HEIGHT_RANGE = register("campsite_height_range", () -> () -> CampsiteHeightRangePlacement.CODEC);

    private static <T extends PlacementModifierType<?>> RegistryHolder<PlacementModifierType<?>, T> register(String name, Supplier<T> supplier) {
        return PLACEMENT_MODIFIER_TYPES.register(name, supplier);
    }
}
