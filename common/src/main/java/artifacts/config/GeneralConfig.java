package artifacts.config;

import java.util.function.Supplier;

public class GeneralConfig extends ConfigManager {

    public final Supplier<Double> artifactRarity = defineNonNegativeDouble("artifactRarity", 1.0,
            "Affects how common artifacts are in chests",
            "Values above 1 will make artifacts rarer, values between 0 and 1 will make artifacts more common",
            "Doubling this value will make artifacts approximately twice as hard to find, and vice versa",
            "To prevent artifacts from appearing as chest loot, set this to 10000.");
    // TODO move to item configs
    public final Supplier<Double> everlastingBeefChance = defineFraction("everlastingBeefChance", 1 / 500D,
            "The chance everlasting beef drops when a cow or mooshroom is killed by a player");
    public final Supplier<Double> entityEquipmentChance = defineFraction("entityEquipmentChance", 0.0015D,
            "The chance that a skeleton, zombie or piglin spawns with an artifact equipped");
    public final Supplier<Double> archaeologyChance = defineFraction("archaeologyChance", 1 / 16D,
            "The chance that an artifact generates in suspicious sand or gravel");
    // TODO move to item configs
    public final Supplier<Boolean> modifyHurtSounds = defineBool("modifyHurtSounds", true, false,
            "Whether the Kitty Slippers and Bunny Hoppers change the player's hurt sounds");

    public final Campsite campsite = new Campsite();

    public class Campsite {

        public final Supplier<Integer> count = defineNonNegativeInt("campsite.campsiteCount", 40,
                "How many times a campsite will attempt to generate per chunk",
                "Set this to 0 to prevent campsites from generating");
        public final Supplier<Integer> minY = defineInt("campsite.minY", -60,
                "The minimum height campsites can spawn at");
        public final Supplier<Integer> maxY = defineInt("campsite.maxY", 40,
                "The maximum height campsites can spawn at");
        public final Supplier<Double> mimicChance = defineFraction("campsite.mimicChance", 0.3,
                "The probability that a campsite has a mimic instead of a chest");
        public final Supplier<Boolean> useModdedChests = defineBool("campsite.useModdedChests", true, false,
                "Whether to use wooden chests from other mods when generating campsites");
        public final Supplier<Boolean> allowLightSources = defineBool("campsite.allowLightSources", true, false,
                "Whether campsites can contain blocks that emit light");
    }

    protected GeneralConfig() {
        super("general");
    }
}
