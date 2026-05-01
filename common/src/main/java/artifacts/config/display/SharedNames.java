package artifacts.config.display;

import artifacts.datagen.LangEntry;

public class SharedNames {

    public static class Titles {
        public static final LangEntry ENABLED = title("enabled", "Enabled");
        public static final LangEntry COOLDOWN = title("cooldown", "Cooldown");
        public static final LangEntry GENERATE_AS_LOOT = title("generateAsLoot", "Generate as Loot");
        public static final LangEntry DURABILITY = title("durability", "Durability");
        public static final LangEntry CAN_BE_DAMAGED = title("durability.canBeDamaged", "Can Be Damaged");
        public static final LangEntry CAN_BE_REPAIRED = title("durability.canBeRepaired", "Can Be Repaired");
        public static final LangEntry INDESTRUCTIBLE = title("durability.indestructible", "Indestructible");
        public static final LangEntry MAX_DAMAGE = title("durability.maxDamage", "Max Damage");
        public static final LangEntry DAMAGE_WHEN_CONSUMED = title("durability.damageWhenConsumed", "Damage When Consumed");
        public static final LangEntry DAMAGE_PER_ACTIVATION = title("durability.damagePerActivation", "Damage per Activation");
    }

    public static class Descriptions {
        public static final LangEntry GENERATE_AS_LOOT = description("generateAsLoot",
                "Whether this item can be found in structures or drop from entities"
        );
        public static final LangEntry CAN_BE_DAMAGED = description("durability.canBeDamaged",
                "Whether this item has a limited number of uses"
        );
        public static final LangEntry CAN_BE_REPAIRED = description("durability.canBeRepaired",
                "Whether this item can be repaired using items in the %s item tag"
        );
        public static final LangEntry INDESTRUCTIBLE = description("durability.indestructible",
                "Whether this item should remain intact and stop working when its durability reaches 1"
        );
        public static final LangEntry MAX_DAMAGE = description("durability.maxDamage",
                "The maximum amount of damage that this item can take before breaking"
        );
        public static final LangEntry DAMAGE_WHEN_CONSUMED = description("durability.damageWhenConsumed",
                "The amount of durability that is lost when this item is consumed"
        );
        public static final LangEntry DAMAGE_PER_ACTIVATION = description("durability.damagePerActivation",
                "The amount durability lost every time this item's ability is triggered"
        );
    }

    private static LangEntry title(String key, String english) {
        return entry(key, english).withSuffix("title");
    }

    private static LangEntry description(String key, String english) {
        return entry(key, english).withSuffix("description");
    }

    private static LangEntry entry(String key, String english) {
        return new LangEntry(key, english)
                .withPrefix("artifacts.config");
    }
}
