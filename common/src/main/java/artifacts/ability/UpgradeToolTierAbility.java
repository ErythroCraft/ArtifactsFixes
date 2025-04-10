package artifacts.ability;

import artifacts.Artifacts;
import artifacts.config.value.Value;
import artifacts.config.value.ValueTypes;
import artifacts.registry.ModDataComponents;
import artifacts.registry.ModTags;
import artifacts.util.AbilityHelper;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;

public record UpgradeToolTierAbility(Value<Tier> tier) implements ArtifactAbility {

    public static final Codec<UpgradeToolTierAbility> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ValueTypes.TOOL_TIER.codec().fieldOf("tier").forGetter(UpgradeToolTierAbility::tier)
    ).apply(instance, UpgradeToolTierAbility::new));

    public static final StreamCodec<ByteBuf, UpgradeToolTierAbility> STREAM_CODEC = StreamCodec.composite(
            ValueTypes.TOOL_TIER.streamCodec(),
            UpgradeToolTierAbility::tier,
            UpgradeToolTierAbility::new
    );

    public static boolean canHarvestWithTier(LivingEntity entity, BlockState state) {
        if (state.is(ModTags.MINEABLE_WITH_DIGGING_CLAWS)) {
            Tier tier = Tier.fromLevel(AbilityHelper.maxInt(
                    ModDataComponents.UPGRADE_TOOL_TIER.get(), entity,
                    ability -> ability.tier().get().getLevel(), false
            ));
            return isCorrectTierForDrops(tier, state);
        }
        return false;
    }

    public static boolean isCorrectTierForDrops(Tier tier, BlockState state) {
        if (!state.requiresCorrectToolForDrops()) {
            return true;
        }
        int i = tier.getLevel();
        if (state.is(BlockTags.NEEDS_DIAMOND_TOOL)) {
            return i >= 4;
        } else if (state.is(BlockTags.NEEDS_IRON_TOOL)) {
            return i >= 3;
        } else if (state.is(BlockTags.NEEDS_STONE_TOOL)) {
            return i >= 2;
        } else {
            return i >= 1;
        }
    }

    @Override
    public boolean isNonCosmetic() {
        return tier().get() != Tier.NONE;
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public void addAbilityTooltip(List<MutableComponent> tooltip) {
        ResourceLocation id = Artifacts.id(""); // TODO ModAbilities.getRegistry().getKey(getType());
        tooltip.add(
                Component.translatable(
                        "%s.tooltip.ability.%s".formatted(id.getNamespace(), id.getPath()),
                        getTierName(tier.get())
                )
        );
    }

    public static Component getTierName(Tier tier) {
        return Component.translatable("%s.tooltip.tool_tier.%s".formatted(Artifacts.MOD_ID, tier.getSerializedName()));
    }

    public enum Tier implements StringRepresentable {
        NONE(0),
        WOOD(1),
        STONE(2),
        IRON(3),
        DIAMOND(4),
        NETHERITE(5);

        private final int level;

        Tier(int level) {
            this.level = level;
        }

        public static Tier fromLevel(int level) {
            return switch (level) {
                case 0 -> NONE;
                case 1 -> WOOD;
                case 2 -> STONE;
                case 3 -> IRON;
                case 4 -> DIAMOND;
                default -> NETHERITE;
            };
        }

        public int getLevel() {
            return level;
        }

        @Override
        public String getSerializedName() {
            return toString().toLowerCase();
        }
    }
}
