package artifacts.component.ability;

import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

import java.util.List;
import java.util.function.Function;

public interface CompositeAbility<ENTRY extends EquipmentAbility> extends EquipmentAbility {

    static <E extends EquipmentAbility, A extends CompositeAbility<E>> Codec<A> codec(Codec<E> entryCodec, Function<List<E>, A> f, Function<A, List<E>> g) {
        return entryCodec.listOf().xmap(f, g);
    }

    static <E extends EquipmentAbility, B extends ByteBuf, A extends CompositeAbility<E>> StreamCodec<B, A> streamCodec(StreamCodec<B, E> entryCodec, Function<List<E>, A> f, Function<A, List<E>> g) {
        return ByteBufCodecs.<B, E>list().apply(entryCodec).map(f, g);
    }

    List<ENTRY> entries();

    @Override
    default boolean isNonCosmetic() {
        for (ENTRY entry : entries()) {
            if (entry.isNonCosmetic()) {
                return true;
            }
        }
        return false;
    }

    @Override
    default void addToTooltip(EquipmentAbility.TooltipWriter writer) {
        for (ENTRY entry : entries()) {
            if (entry.isNonCosmetic()) {
                entry.addToTooltip(writer);
            }
        }
    }
}
