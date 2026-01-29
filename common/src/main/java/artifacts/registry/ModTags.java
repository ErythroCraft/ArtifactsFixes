package artifacts.registry;

import artifacts.Artifacts;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

public class ModTags {

    public static final TagKey<Block> MINEABLE_WITH_DIGGING_CLAWS = create(Registries.BLOCK, "mineable/digging_claws");
    public static final TagKey<Block> CAMPSITE_CHESTS = create(Registries.BLOCK, "campsite_chests");
    public static final TagKey<Block> ROOTED_BOOTS_GRASS = create(Registries.BLOCK, "rooted_boots_grass");
    public static final TagKey<Block> SNOW_LAYERS = create(Registries.BLOCK, "snow_layers");
    public static final TagKey<MobEffect> ANTIDOTE_VESSEL_CANCELLABLE = create(Registries.MOB_EFFECT, "antidote_vessel_cancellable");
    public static final TagKey<EntityType<?>> CREEPERS = create(Registries.ENTITY_TYPE, "creepers");
    public static final TagKey<DamageType> IS_HOT_FLOOR = create(Registries.DAMAGE_TYPE, "is_hot_floor");

    public static final TagKey<Block> ORES = conventionTag(Registries.BLOCK, "ores");
    public static final TagKey<Item> RAW_MATERIALS = conventionTag(Registries.ITEM, "raw_materials");

    private static <T> TagKey<T> create(ResourceKey<Registry<T>> registry, String name) {
        return TagKey.create(registry, Artifacts.id(name));
    }

    private static <T> TagKey<T> conventionTag(ResourceKey<Registry<T>> registry, String name) {
        return TagKey.create(registry, Identifier.fromNamespaceAndPath("c", name));
    }
}
