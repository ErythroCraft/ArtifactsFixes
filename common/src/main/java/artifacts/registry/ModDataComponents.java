package artifacts.registry;

import artifacts.Artifacts;
import artifacts.ability.ArtifactAbility;
import dev.architectury.registry.registries.DeferredRegister;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;

import java.util.List;

public class ModDataComponents {

    public static final DeferredRegister<DataComponentType<?>> DATA_COMPONENT_TYPES = DeferredRegister.create(Artifacts.MOD_ID, Registries.DATA_COMPONENT_TYPE);

    public static final Holder<DataComponentType<List<ArtifactAbility>>> ABILITIES = DATA_COMPONENT_TYPES.register("abilities", () ->
            DataComponentType.<List<ArtifactAbility>>builder()
                    .persistent(ArtifactAbility.CODEC.sizeLimitedListOf(256))
                    .networkSynchronized(ByteBufCodecs.<RegistryFriendlyByteBuf, ArtifactAbility>list().apply(ArtifactAbility.STREAM_CODEC))
                    .cacheEncoding()
                    .build()
    );

    public static void register() {
        DATA_COMPONENT_TYPES.register();
    }
}
