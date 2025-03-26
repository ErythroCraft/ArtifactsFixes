package artifacts.registry;

import artifacts.Artifacts;
import artifacts.world.CampsiteFeature;
import artifacts.world.CampsiteFeatureConfiguration;
import dev.architectury.registry.registries.DeferredRegister;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

public class ModFeatures {

    public static final DeferredRegister<Feature<?>> FEATURES = DeferredRegister.create(Artifacts.MOD_ID, Registries.FEATURE);

    public static final Holder<Feature<CampsiteFeatureConfiguration>> CAMPSITE = FEATURES.register("campsite", CampsiteFeature::new);

    public static final ResourceKey<PlacedFeature> UNDERGROUND_CAMPSITE = Artifacts.key(Registries.PLACED_FEATURE, "underground_campsite");
}
