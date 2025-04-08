package artifacts.registry;

import artifacts.ability.ArtifactAbility;
import artifacts.platform.PlatformServices;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;

import java.util.List;

public class ModDataComponents {

    public static final Register<DataComponentType<?>> DATA_COMPONENT_TYPES = PlatformServices.platformHelper.createRegister(Registries.DATA_COMPONENT_TYPE);

    public static final RegistryHolder<DataComponentType<?>, DataComponentType<List<ArtifactAbility>>> ABILITIES = DATA_COMPONENT_TYPES.register("abilities", () ->
            DataComponentType.<List<ArtifactAbility>>builder()
                    .persistent(ArtifactAbility.CODEC.sizeLimitedListOf(256))
                    .networkSynchronized(ByteBufCodecs.<RegistryFriendlyByteBuf, ArtifactAbility>list().apply(ArtifactAbility.STREAM_CODEC))
                    .cacheEncoding()
                    .build()
    );
}
