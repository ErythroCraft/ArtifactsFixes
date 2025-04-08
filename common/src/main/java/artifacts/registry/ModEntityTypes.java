package artifacts.registry;

import artifacts.Artifacts;
import artifacts.entity.MimicEntity;
import artifacts.platform.PlatformServices;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;

public class ModEntityTypes {

    public static final Register<EntityType<?>> ENTITY_TYPES = PlatformServices.platformHelper.createRegister(Registries.ENTITY_TYPE);

    public static final RegistryHolder<EntityType<?>, EntityType<MimicEntity>> MIMIC = ENTITY_TYPES.register("mimic",
            () -> EntityType.Builder.of(MimicEntity::new, MobCategory.MISC)
                    .sized(14 / 16F, 14 / 16F)
                    .clientTrackingRange(8)
                    .build(Artifacts.id("mimic").toString())
    );
}
