package artifacts.component;

import artifacts.config.value.Value;
import artifacts.config.value.ValueTypes;
import artifacts.equipment.EquipmentHelper;
import artifacts.registry.ModDataComponents;
import artifacts.util.ModCodecs;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Optional;

public record DamageOnBlockMined(Value<Integer> itemDamage, Optional<TagKey<Block>> tag) {

    public static final Codec<DamageOnBlockMined> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ValueTypes.NON_NEGATIVE_INT.codec().fieldOf("item_damage").forGetter(DamageOnBlockMined::itemDamage),
            TagKey.codec(Registries.BLOCK).optionalFieldOf("tag").forGetter(DamageOnBlockMined::tag)
    ).apply(instance, DamageOnBlockMined::new));

    public static final StreamCodec<ByteBuf, DamageOnBlockMined> STREAM_CODEC = StreamCodec.composite(
            ValueTypes.DURATION.streamCodec(),
            DamageOnBlockMined::itemDamage,
            ByteBufCodecs.optional(ModCodecs.tagKeyStreamCodec(Registries.BLOCK)),
            DamageOnBlockMined::tag,
            DamageOnBlockMined::new
    );

    public static void onBlockBroken(LivingEntity entity, BlockState state) {
        EquipmentHelper.iterateComponents(
                ModDataComponents.DAMAGE_ON_BLOCK_MINED.get(),
                entity,
                true, true,
                (component, slotAccess) -> {
                    if (component.tag().isEmpty() || state.is(component.tag().get())) {
                        slotAccess.hurtAndBreak(entity, component.itemDamage().get());
                    }
                }
        );
    }
}
