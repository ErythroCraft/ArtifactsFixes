package artifacts.world;

import artifacts.Artifacts;
import artifacts.entity.MimicEntity;
import artifacts.registry.ModEntityTypes;
import artifacts.registry.ModTags;
import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.RandomizableContainer;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.storage.loot.LootTable;

public abstract class AbstractCampsiteFeature<FC extends FeatureConfiguration> extends Feature<FC> {

    public static final ResourceKey<LootTable> CHEST_LOOT = Artifacts.key(Registries.LOOT_TABLE, "chests/campsite_chest");

    public AbstractCampsiteFeature(Codec<FC> codec) {
        super(codec);
    }

    // TODO add chest feature config (loot table, trapped chest probability, mimic probability)
    public void placeChest(WorldGenLevel level, BlockPos pos, RandomSource random, Direction facing) {
        if (random.nextFloat() < Artifacts.CONFIG.general.campsite.mimicChance.get()) {
            MimicEntity mimic = ModEntityTypes.MIMIC.get().create(level.getLevel());
            if (mimic != null) {
                mimic.setDormant(true);
                mimic.setFacing(facing);
                mimic.setPos(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);
                level.addFreshEntity(mimic);
            }
        } else {
            BlockState chest;
            if (random.nextInt(8) == 0) {
                setBlock(level, pos.below(), Blocks.TNT.defaultBlockState());
                chest = Blocks.TRAPPED_CHEST.defaultBlockState();
                setBlock(level, pos, Blocks.TRAPPED_CHEST.defaultBlockState().setValue(ChestBlock.FACING, Direction.Plane.HORIZONTAL.getRandomDirection(random)));
            } else if (Artifacts.CONFIG.general.campsite.useModdedChests.get()) {
                chest = ModTags.getTag(ModTags.CAMPSITE_CHESTS)
                        .getRandomElement(random)
                        .map(Holder::value)
                        .orElse(Blocks.CHEST)
                        .defaultBlockState();
            } else {
                chest = Blocks.CHEST.defaultBlockState();
            }

            if (chest.hasProperty(BlockStateProperties.HORIZONTAL_FACING)) {
                chest = chest.setValue(BlockStateProperties.HORIZONTAL_FACING, facing);
            }
            setBlock(level, pos, chest);

            RandomizableContainer.setBlockEntityLootTable(level, random, pos, CHEST_LOOT);
        }
    }
}
