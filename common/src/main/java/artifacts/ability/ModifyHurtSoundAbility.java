package artifacts.ability;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.sounds.SoundEvent;

public record ModifyHurtSoundAbility(Holder<SoundEvent> soundEvent) implements EquipmentAbility {

    public static final Codec<ModifyHurtSoundAbility> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            BuiltInRegistries.SOUND_EVENT.holderByNameCodec().fieldOf("sound").forGetter(ModifyHurtSoundAbility::soundEvent)
    ).apply(instance, ModifyHurtSoundAbility::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, ModifyHurtSoundAbility> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.holderRegistry(Registries.SOUND_EVENT),
            ModifyHurtSoundAbility::soundEvent,
            ModifyHurtSoundAbility::new
    );

    @Override
    public boolean isNonCosmetic() {
        return true;
    }
}
