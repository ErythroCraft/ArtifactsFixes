package artifacts.world;

import com.mojang.serialization.Codec;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;

public record SuspiciousChestFeatureConfiguration() implements FeatureConfiguration {

    public static final Codec<SuspiciousChestFeatureConfiguration> CODEC = Codec.unit(SuspiciousChestFeatureConfiguration::new);
}
