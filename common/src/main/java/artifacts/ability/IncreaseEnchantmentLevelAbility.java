package artifacts.ability;

import artifacts.config.value.Value;
import artifacts.config.value.ValueTypes;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;

import java.util.List;

public record IncreaseEnchantmentLevelAbility(ResourceKey<Enchantment> enchantment, Value<Integer> amount) implements ArtifactAbility {

    public static final List<ResourceKey<Enchantment>> ALLOWED_ENCHANTMENTS = List.of(
            Enchantments.FORTUNE,
            Enchantments.LOOTING,
            Enchantments.LURE,
            Enchantments.LUCK_OF_THE_SEA
    );

    public static final Codec<IncreaseEnchantmentLevelAbility> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ResourceKey.codec(Registries.ENCHANTMENT)
                    .validate(enchantment -> ALLOWED_ENCHANTMENTS.contains(enchantment)
                            ? DataResult.success(enchantment)
                            : DataResult.error(() -> "Unsupported enchantment: %s".formatted(enchantment.location())))
                    .fieldOf("enchantment").forGetter(IncreaseEnchantmentLevelAbility::enchantment),
            ValueTypes.ENCHANTMENT_LEVEL.codec().optionalFieldOf("level", Value.of(1)).forGetter(IncreaseEnchantmentLevelAbility::amount)
    ).apply(instance, IncreaseEnchantmentLevelAbility::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, IncreaseEnchantmentLevelAbility> STREAM_CODEC = StreamCodec.composite(
            ResourceKey.streamCodec(Registries.ENCHANTMENT),
            IncreaseEnchantmentLevelAbility::enchantment,
            ValueTypes.ENCHANTMENT_LEVEL.streamCodec(),
            IncreaseEnchantmentLevelAbility::amount,
            IncreaseEnchantmentLevelAbility::new
    );

    public int getAmount() {
        return amount.get();
    }

    @Override
    public boolean isNonCosmetic() {
        return amount().get() > 0;
    }

    @Override
    public void addAbilityTooltip(List<MutableComponent> tooltip) {
        String enchantmentName = enchantment().location().getPath();
        if (getAmount() == 1) {
            tooltip.add(tooltipLine("%s.single_level".formatted(enchantmentName)));
        } else {
            tooltip.add(tooltipLine("%s.multiple_levels".formatted(enchantmentName), getAmount()));
        }
    }
}
