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

    /** Used to check if a block can be mined using the digging claws */
    public static final TagKey<Block> MINEABLE_WITH_DIGGING_CLAWS = create(Registries.BLOCK, "mineable/digging_claws");
    /** Chests used in campsite world generation */
    public static final TagKey<Block> CAMPSITE_CHESTS = create(Registries.BLOCK, "campsite_chests");
    /** Blocks that count as grass when standing on them with the Rooted Boots */
    public static final TagKey<Block> ROOTED_BOOTS_GRASS = create(Registries.BLOCK, "rooted_boots_grass");
    /** Blocks that affect the movement_speed_on_snow attribute when standing in them instead of on them */
    public static final TagKey<Block> SNOW_LAYERS = create(Registries.BLOCK, "snow_layers");
    /** Mob Effects that can be cancelled by the antidote vessel */
    public static final TagKey<MobEffect> ANTIDOTE_VESSEL_CANCELLABLE = create(Registries.MOB_EFFECT, "antidote_vessel_cancellable");
    /** Mobs that flee from the kitty slippers */
    public static final TagKey<EntityType<?>> CREEPERS = create(Registries.ENTITY_TYPE, "creepers");
    /** Damage Types used by the vampiric glove, fire gauntlet and withered bracelet */
    public static final TagKey<DamageType> IS_MELEE = create(Registries.DAMAGE_TYPE, "is_melee");

    /** Blocks that are affected by the pickaxe heater */
    public static final TagKey<Block> ORES = conventionTag(Registries.BLOCK, "ores");
    /** Items that are affected by the pickaxe heater */
    public static final TagKey<Item> RAW_MATERIALS = conventionTag(Registries.ITEM, "raw_materials");

    public static final TagKey<Item> REPAIRS_ANTIDOTE_VESSEL = create("repairs_antidote_vessel");
    public static final TagKey<Item> REPAIRS_ETERNAL_STEAK = create("repairs_eternal_steak");
    public static final TagKey<Item> REPAIRS_EVERLASTING_BEEF = create("repairs_everlasting_beef");
    public static final TagKey<Item> REPAIRS_FLAME_PENDANT = create("repairs_flame_pendant");
    public static final TagKey<Item> REPAIRS_OBSIDIAN_SKULL = create("repairs_obsidian_skull");
    public static final TagKey<Item> REPAIRS_ONION_RING = create("repairs_onion_ring");
    public static final TagKey<Item> REPAIRS_PANIC_NECKLACE = create("repairs_panic_necklace");
    public static final TagKey<Item> REPAIRS_SHOCK_PENDANT = create("repairs_shock_pendant");
    public static final TagKey<Item> REPAIRS_THORN_PENDANT = create("repairs_thorn_pendant");
    public static final TagKey<Item> REPAIRS_UMBRELLA = create("repairs_umbrella");

    private static TagKey<Item> create(String name) {
        return create(Registries.ITEM, name);
    }

    private static <T> TagKey<T> create(ResourceKey<Registry<T>> registry, String name) {
        return TagKey.create(registry, Artifacts.id(name));
    }

    private static <T> TagKey<T> conventionTag(ResourceKey<Registry<T>> registry, String name) {
        return TagKey.create(registry, Identifier.fromNamespaceAndPath("c", name));
    }
}
