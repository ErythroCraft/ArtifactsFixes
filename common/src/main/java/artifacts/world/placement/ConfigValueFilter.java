package artifacts.world.placement;

import artifacts.Artifacts;
import artifacts.config.value.Value;
import artifacts.registry.ModPlacementModifierTypes;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.placement.PlacementContext;
import net.minecraft.world.level.levelgen.placement.PlacementFilter;
import net.minecraft.world.level.levelgen.placement.PlacementModifierType;

public class ConfigValueFilter extends PlacementFilter {

    public static final MapCodec<ConfigValueFilter> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Artifacts.CONFIG.general.campsite.codec().fieldOf("value").forGetter(f -> f.value),
            Codec.BOOL.fieldOf("invert").forGetter(f -> f.invert)
    ).apply(instance, ConfigValueFilter::new));

    private final Value.ConfigValue<Boolean> value;
    private final boolean invert;

    private ConfigValueFilter(Value.ConfigValue<Boolean> value, boolean invert) {
        this.value = value;
        this.invert = invert;
    }

    @Override
    protected boolean shouldPlace(PlacementContext placementContext, RandomSource randomSource, BlockPos blockPos) {
        return value.get() ^ !invert;
    }

    @Override
    public PlacementModifierType<?> type() {
        return ModPlacementModifierTypes.CONFIG_VALUE_FILTER.value();
    }
}
