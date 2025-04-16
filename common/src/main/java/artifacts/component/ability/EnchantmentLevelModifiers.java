package artifacts.component.ability;

import artifacts.config.value.Value;
import artifacts.config.value.ValueTypes;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;

import java.util.List;

public record EnchantmentLevelModifiers(List<Entry> entries) implements EquipmentAbility {

    public static final List<ResourceKey<Enchantment>> ALLOWED_ENCHANTMENTS = List.of(
            Enchantments.FORTUNE,
            Enchantments.LOOTING,
            Enchantments.LURE,
            Enchantments.LUCK_OF_THE_SEA
    );

    public static final Codec<EnchantmentLevelModifiers> CODEC = Entry.CODEC.listOf().xmap(
            EnchantmentLevelModifiers::new, EnchantmentLevelModifiers::entries
    );

    public static final StreamCodec<RegistryFriendlyByteBuf, EnchantmentLevelModifiers> STREAM_CODEC = ByteBufCodecs.<RegistryFriendlyByteBuf, Entry>list()
            .apply(Entry.STREAM_CODEC).map(EnchantmentLevelModifiers::new, EnchantmentLevelModifiers::entries);

    @Override
    public boolean isNonCosmetic() {
        for (Entry entry : entries) {
            if (entry.isNonCosmetic()) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void addToTooltip(TooltipWriter writer) {
        for (Entry entry : entries) {
            if (entry.isNonCosmetic()) {
                String enchantmentName = entry.enchantment().location().getPath();
                if (entry.amount().get() == 1) {
                    writer.add("%s.single_level".formatted(enchantmentName));
                } else {
                    writer.add("%s.multiple_levels".formatted(enchantmentName), entry.amount().get());
                }
            }
        }
    }

    public record Entry(ResourceKey<Enchantment> enchantment, Value<Integer> amount) {

        public static final Codec<Entry> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                ResourceKey.codec(Registries.ENCHANTMENT)
                        .validate(enchantment -> ALLOWED_ENCHANTMENTS.contains(enchantment)
                                ? DataResult.success(enchantment)
                                : DataResult.error(() -> "Unsupported enchantment: %s".formatted(enchantment.location())))
                        .fieldOf("enchantment").forGetter(Entry::enchantment),
                ValueTypes.ENCHANTMENT_LEVEL.codec().optionalFieldOf("level", Value.of(1)).forGetter(Entry::amount)
        ).apply(instance, Entry::new));

        public static final StreamCodec<RegistryFriendlyByteBuf, Entry> STREAM_CODEC = StreamCodec.composite(
                ResourceKey.streamCodec(Registries.ENCHANTMENT),
                Entry::enchantment,
                ValueTypes.ENCHANTMENT_LEVEL.streamCodec(),
                Entry::amount,
                Entry::new
        );


        public boolean isNonCosmetic() {
            return amount.get() > 0;
        }
    }
}
