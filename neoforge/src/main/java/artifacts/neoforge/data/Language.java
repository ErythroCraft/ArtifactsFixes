package artifacts.neoforge.data;

import artifacts.Artifacts;
import artifacts.config.ConfigManager;
import artifacts.neoforge.data.tags.ItemTags;
import artifacts.registry.*;
import com.google.common.base.CaseFormat;
import joptsimple.internal.Strings;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.common.data.LanguageProvider;

import java.util.List;
import java.util.Objects;

public class Language extends LanguageProvider {

    public Language(PackOutput output) {
        super(output, Artifacts.MOD_ID, "en_us");
    }

    @Override
    protected void addTranslations() {
        Artifacts.CONFIG.setup();

        addMiscTranslations();
        addAbilities();
        addAttributes();
        addEntities();
        addConfigs();
        addItems();
        addTags();
        addTooltips();
        addEffects();
        Advancements.TRANSLATIONS.forEach(this::add);
    }

    public void override(String key, String value) {
        try {
            add(key, value);
        } catch (IllegalStateException ignored) {

        }
    }

    private void addMiscTranslations() {
        add("artifacts.creative_tab", "Artifacts");
        add("artifacts.key.helium_flamingo.activate", "Activate Helium Flamingo");
        add("artifacts.key.charm_of_shrinking.toggle", "Toggle Charm of Shrinking");
        add("artifacts.key.charm_of_sinking.toggle", "Toggle Charm of Sinking");
        add("artifacts.key.night_vision_goggles.toggle", "Toggle Night Vision Goggles");
        add("artifacts.key.universal_attractor.toggle", "Toggle Universal Attractor");
        add("artifacts.key.scarf_of_invisibility.toggle", "Toggle Scarf of Invisibility");
        add("artifacts.key_category", "Artifacts");
        add(ModSoundEvents.FART.value(), "Fart");
        add("curios.identifier.feet", "Feet");
        add("curios.modifiers.feet", "When on feet:");
    }

    private void addAbilities() {
        addAbilityTooltip(ModDataComponents.APPLY_MOB_EFFECT_AFTER_DAMAGE.get(), MobEffects.FIRE_RESISTANCE, "Applies a temporary fire resistance effect after taking fire damage");
        addAbilityTooltip(ModDataComponents.APPLY_MOB_EFFECT_AFTER_DAMAGE.get(), MobEffects.MOVEMENT_SPEED, "Increases the wearer's movement speed after taking damage");
        addAbilityTooltip(ModDataComponents.APPLY_MOB_EFFECT_AFTER_EATING.get(), MobEffects.DIG_SPEED, "Grants a temporary boost to mining speed after eating food");
        addAbilityTooltip(ModDataComponents.ATTACKS_ABSORB_DAMAGE.get(), "chance", "The wearer's melee attacks have a chance to absorb health");
        addAbilityTooltip(ModDataComponents.ATTACKS_ABSORB_DAMAGE.get(), "constant", "Causes the wearer's melee attacks to absorb health");
        addAbilityTooltip(ModDataComponents.ATTACKS_INFLICT_MOB_EFFECT.get(), MobEffects.WITHER, "chance", "Melee attacks have a chance to inflict a wither effect");
        addAbilityTooltip(ModDataComponents.ATTACKS_INFLICT_MOB_EFFECT.get(), MobEffects.WITHER, "constant", "Causes the wearer's melee attacks to inflict a wither effect");
        addAbilityTooltip(ModDataComponents.ATTRIBUTE_MODIFIER.get(), ModAttributes.ATTACK_BURNING_DURATION, "Causes the wearer's melee attacks to deal fire damage");
        addAbilityTooltip(ModDataComponents.ATTRIBUTE_MODIFIER.get(), Attributes.ATTACK_DAMAGE, "Increases damage dealt by the wearer");
        addAbilityTooltip(ModDataComponents.ATTRIBUTE_MODIFIER.get(), Attributes.ATTACK_KNOCKBACK, "Increases knockback dealt by the wearer");
        addAbilityTooltip(ModDataComponents.ATTRIBUTE_MODIFIER.get(), Attributes.ATTACK_SPEED, "Increases the wearer's attack speed");
        addAbilityTooltip(ModDataComponents.ATTRIBUTE_MODIFIER.get(), ModAttributes.DRINKING_SPEED, "Decreases the time it takes to drink items");
        addAbilityTooltip(ModDataComponents.ATTRIBUTE_MODIFIER.get(), ModAttributes.EATING_SPEED, "Decreases the time it takes to eat items");
        addAbilityTooltip(ModDataComponents.ATTRIBUTE_MODIFIER.get(), Attributes.FALL_DAMAGE_MULTIPLIER, "Reduces fall damage taken by the wearer");
        addAbilityTooltip(ModDataComponents.ATTRIBUTE_MODIFIER.get(), ModAttributes.FLATULENCE, "Increases the wearer's flatulence");
        addAbilityTooltip(ModDataComponents.ATTRIBUTE_MODIFIER.get(), ModAttributes.INVINCIBILITY_TICKS, "Increases the length of invincibility after taking damage");
        addAbilityTooltip(ModDataComponents.ATTRIBUTE_MODIFIER.get(), Attributes.JUMP_STRENGTH, "Increases the wearer's jump height");
        addAbilityTooltip(ModDataComponents.ATTRIBUTE_MODIFIER.get(), Attributes.KNOCKBACK_RESISTANCE, "Grants immunity to knockback");
        addAbilityTooltip(ModDataComponents.ATTRIBUTE_MODIFIER.get(), Attributes.MAX_HEALTH, "Increases the wearer's maximum health");
        addAbilityTooltip(ModDataComponents.ATTRIBUTE_MODIFIER.get(), ModAttributes.MOUNT_SPEED, "Increases the speed of ridden mounts");
        addAbilityTooltip(ModDataComponents.ATTRIBUTE_MODIFIER.get(), Attributes.SAFE_FALL_DISTANCE, "Increases the wearer's maximum safe fall distance");
        addAbilityTooltip(ModDataComponents.ATTRIBUTE_MODIFIER.get(), Attributes.SCALE, "Shrinks the wearer");
        addAbilityTooltip(ModDataComponents.ATTRIBUTE_MODIFIER.get(), ModAttributes.SLIP_RESISTANCE, "Makes ice less slippery to walk on");
        addAbilityTooltip(ModDataComponents.ATTRIBUTE_MODIFIER.get(), ModAttributes.MOVEMENT_SPEED_ON_SNOW, "Increases the wearer's walking speed on snow");
        addAbilityTooltip(ModDataComponents.ATTRIBUTE_MODIFIER.get(), ModAttributes.SPRINTING_SPEED, "Increases the wearer's movement speed while sprinting");
        addAbilityTooltip(ModDataComponents.ATTRIBUTE_MODIFIER.get(), ModAttributes.SPRINTING_STEP_HEIGHT, "Increases the wearer's step height while sprinting");
        addAbilityTooltip(ModDataComponents.ATTRIBUTE_MODIFIER.get(), "generic.swim_speed", "Improves agility in water");
        addAbilityTooltip(ModDataComponents.ATTRIBUTE_MODIFIER.get(), Attributes.BLOCK_BREAK_SPEED, "Increases the wearer's mining speed");
        addAbilityTooltip(ModDataComponents.ATTRIBUTE_MODIFIER.get(), ModAttributes.ENTITY_EXPERIENCE, "Increases experience dropped by creatures");
        addAbilityTooltip(ModDataComponents.ATTRIBUTE_MODIFIER.get(), ModAttributes.VILLAGER_REPUTATION, "Decreases the trading prices of villagers");
        addAbilityTooltip(ModDataComponents.DAMAGE_IMMUNITY.get(), ModTags.IS_HOT_FLOOR.location().getPath(), "Grants protection against hot floor damage");
        addAbilityTooltip(ModDataComponents.DAMAGE_IMMUNITY.get(), DamageTypeTags.IS_LIGHTNING.location().getPath(), "Grants protection against lightning strikes");
        addAbilityTooltip(ModDataComponents.DOUBLE_JUMP.get(), "Allows the wearer to double jump");
        addAbilityTooltip(ModDataComponents.ENDER_PEARLS_COST_HUNGER.get(), "free", "Ender Pearls are not consumed when thrown");
        addAbilityTooltip(ModDataComponents.ENDER_PEARLS_COST_HUNGER.get(), "cost", "Ender Pearls are not consumed, but cost hunger instead");
        addAbilityTooltip(ModDataComponents.GROW_PLANTS_AFTER_EATING.get(), "Plants grow after eating when standing on grass");
        addAbilityTooltip(ModDataComponents.INCREASE_ENCHANTMENT_LEVEL.get(), "fortune", "multiple_levels", "Applies %s extra levels of fortune to mined blocks");
        addAbilityTooltip(ModDataComponents.INCREASE_ENCHANTMENT_LEVEL.get(), "fortune", "single_level", "Applies an extra level of fortune to mined blocks");
        addAbilityTooltip(ModDataComponents.INCREASE_ENCHANTMENT_LEVEL.get(), "looting", "multiple_levels", "Applies %s extra levels of looting to killed entities");
        addAbilityTooltip(ModDataComponents.INCREASE_ENCHANTMENT_LEVEL.get(), "looting", "single_level", "Applies an extra level of looting to killed entities");
        addAbilityTooltip(ModDataComponents.INCREASE_ENCHANTMENT_LEVEL.get(), "luck_of_the_sea", "multiple_levels", "Applies %s extra levels of Luck of the Sea when fishing");
        addAbilityTooltip(ModDataComponents.INCREASE_ENCHANTMENT_LEVEL.get(), "luck_of_the_sea", "single_level", "Applies an extra Luck of the Sea when fishing");
        addAbilityTooltip(ModDataComponents.INCREASE_ENCHANTMENT_LEVEL.get(), "lure", "multiple_levels", "Applies %s extra levels of Lure when fishing");
        addAbilityTooltip(ModDataComponents.INCREASE_ENCHANTMENT_LEVEL.get(), "lure", "single_level", "Applies an extra level of Lure when fishing");
        addAbilityTooltip(ModDataComponents.LIMITED_WATER_BREATHING.get(), "infinite", "Allows the wearer to breathe underwater");
        addAbilityTooltip(ModDataComponents.LIMITED_WATER_BREATHING.get(), "limited", "Allows the wearer to breathe underwater for a limited amount of time");
        addAbilityTooltip(ModDataComponents.MOB_EFFECT.get(), "invisibility", "Turns the wearer invisible");
        addAbilityTooltip(ModDataComponents.MOB_EFFECT.get(), "magnetism", "Attracts nearby items");
        addAbilityTooltip(ModDataComponents.NIGHT_VISION.get(), "full", "Allows the wearer to see in the dark");
        addAbilityTooltip(ModDataComponents.NIGHT_VISION.get(), "partial", "Allows the wearer to see in the dark slightly");
        addAbilityTooltip(ModDataComponents.NULLIFY_ENDER_PEARL_DAMAGE.get(), "Ender Pearls deal no damage");
        addAbilityTooltip(ModDataComponents.REMOVE_BAD_EFFECTS.get(), "Greatly reduces the duration of negative effects");
        addAbilityTooltip(ModDataComponents.REPLENISH_HUNGER_ON_GRASS.get(), "Slowly replenishes hunger while walking on grass");
        addAbilityTooltip(ModDataComponents.SCARE_CREEPERS.get(), "Creepers avoid the wearer");
        addAbilityTooltip(ModDataComponents.SET_ATTACKERS_ON_FIRE.get(), "fire_resistance", "Grants fire resistance after lighting an attacker on fire");
        addAbilityTooltip(ModDataComponents.SET_ATTACKERS_ON_FIRE.get(), "chance", "Has a chance to light attackers on fire");
        addAbilityTooltip(ModDataComponents.SET_ATTACKERS_ON_FIRE.get(), "constant", "Attacking entities are lit on fire");
        addAbilityTooltip(ModDataComponents.SINKING.get(), "Allows the wearer to move freely in water");
        addAbilityTooltip(ModDataComponents.SMELT_ORES.get(), "Automatically smelts mined ores");
        addAbilityTooltip(ModDataComponents.SNEAK_ON_FLUIDS.get(), "lava", "Allows the wearer to stand on lava while sneaking");
        addAbilityTooltip(ModDataComponents.SPRINT_ON_FLUIDS.get(), "Allows the wearer to walk on fluids while sprinting");
        addAbilityTooltip(ModDataComponents.STRIKE_ATTACKERS_WITH_LIGHTNING.get(), "chance", "Has a chance to strike attackers with lightning");
        addAbilityTooltip(ModDataComponents.STRIKE_ATTACKERS_WITH_LIGHTNING.get(), "constant", "Attacking entities are struck by lightning");
        addAbilityTooltip(ModDataComponents.SWIM_IN_AIR.get(), "keymapping", "Press %s while in the air to start swimming");
        addAbilityTooltip(ModDataComponents.SWIM_IN_AIR.get(), "swimming", "Allows the wearer to swim in the air for a limited period of time");
        addAbilityTooltip(ModDataComponents.TELEPORT_ON_DEATH.get(), "chance", "A fatal hit has a chance to teleport you somewhere else instead");
        addAbilityTooltip(ModDataComponents.TELEPORT_ON_DEATH.get(), "constant", "A fatal hit teleports you somewhere else instead");
        addAbilityTooltip(ModDataComponents.TELEPORT_ON_DEATH.get(), "not_consumed", "Not consumed on use");
        addAbilityTooltip(ModDataComponents.THORNS.get(), "chance", "Has a chance to damage attackers");
        addAbilityTooltip(ModDataComponents.THORNS.get(), "constant", "Attacking entities are damaged as well");
        addAbilityTooltip(ModDataComponents.UPGRADE_TOOL_TIER.get(), "Increases the wearer's base mining level to %s");
        addAbilityTooltip(ModDataComponents.WALK_ON_POWDER_SNOW.get(), "Allows the wearer to walk on Powder Snow");
    }

    private void addAttributes() {
        for (RegistryHolder<Attribute, ?> attribute : ModAttributes.ATTRIBUTES.getEntries()) {
            add(attribute.get().getDescriptionId(), fromSnakeCasedString(attribute.unwrapKey().orElseThrow().location().getPath().split("\\.")[1]));
        }
        add("generic.swim_speed", "Swim Speed");
    }

    private void addEntities() {
        for (RegistryHolder<EntityType<?>, ?> entityType : ModEntityTypes.ENTITY_TYPES.getEntries()) {
            add(entityType.get().getDescriptionId(), fromSnakeCasedString(entityType.unwrapKey().orElseThrow().location().getPath()));
        }
        add(ModSoundEvents.MIMIC_CLOSE.value(), "Mimic closes");
        add(ModSoundEvents.MIMIC_DEATH.value(), "Mimic dies");
        add(ModSoundEvents.MIMIC_HURT.value(), "Mimic hurts");
        add(ModSoundEvents.MIMIC_OPEN.value(), "Mimic hops");
    }

    private void addConfigs() {
        add(configTitle(), "Artifacts Config");
        add("artifacts.config.enabled.title", "Enabled");
        add("artifacts.config.cooldown.title", "Cooldown");
        for (ConfigManager config : Artifacts.CONFIG.configs) {
            add(configTitle(config.getName()), fromCamelCasedString(config.getName()));
            addConfigNames(config);
            addConfigTooltips(config);
        }
    }

    private void addConfigNames(ConfigManager config) {
        config.getValues().forEach((key, value) -> {
            String[] words = key.split("\\.");
            String name = words[words.length - 1];
            if (!name.equals("cooldown") && !name.equals("enabled")) {
                String translation = fromCamelCasedString(name);
                key = config.getName() + '.' + key;
                add(configTitle(key), translation);
                StringBuilder categoryKey = new StringBuilder(config.getName());
                for (int i = 0; i < words.length - 1; i++) {
                    categoryKey.append('.').append(words[i]);
                    if (!BuiltInRegistries.ITEM.containsKey(Artifacts.id(words[i]))) {
                        override(configTitle(categoryKey.toString()), fromSnakeCasedString(words[i]));
                    }
                }
            }
        });
    }

    private void addConfigTooltips(ConfigManager config) {
        config.getValues().forEach((key, value) -> {
            List<String> tooltips = config.getDescription(key);
            key = config.getName() + '.' + key;
            if (tooltips.size() == 1) {
                add(configDescription(key), tooltips.getFirst());
            } else {
                for (int i = 0; i < tooltips.size(); i++) {
                    add(concat(configDescription(key), Integer.toString(i)), tooltips.get(i));
                }
            }
        });
    }

    private void addItems() {
        for (Holder<Item> item : ModItems.ITEMS.getEntries()) {
            add(item.value(), fromSnakeCasedString(item.unwrapKey().orElseThrow().location().getPath()));
        }
        override(ModItems.ANGLERS_HAT.value().getDescriptionId(), "Angler's Hat");
        override(ModItems.AQUA_DASHERS.value().getDescriptionId(), "Aqua-Dashers");
    }

    private void addTags() {
        add(ItemTags.ARTIFACTS, "Artifacts");
        add(ItemTags.ALL, "Any Slot Equipable Artifacts");
        add(ItemTags.BELT, "Belt Slot Equipable Artifacts");
        add(ItemTags.FEET, "Feet Slot Equipable Artifacts");
        add(ItemTags.HANDS, "Hands Slot Equipable Artifacts");
        add(ItemTags.HEAD, "Head Slot Equipable Artifacts");
        add(ItemTags.NECKLACE, "Necklace Slot Equipable Artifacts");

        add(ModTags.ANTIDOTE_VESSEL_CANCELLABLE, "Antidote Vessel Cancellable");
        add(ModTags.CAMPSITE_CHESTS, "Campsite Chests");
        add(ModTags.CREEPERS, "Creepers");
        add(ModTags.MINEABLE_WITH_DIGGING_CLAWS, "Mineable With Digging Claws");
        add(ModTags.ROOTED_BOOTS_GRASS, "Rooted Boots Grass");
        add(ModTags.SNOW_LAYERS, "Snow Layers");
    }

    private void addTooltips() {
        tooltip("attacks_inflict.chance", "Attacks have a chance to inflict:");
        tooltip("attacks_inflict.constant", "Attacks inflict:");
        tooltip("cooldown", "+Cooldown (%s)");
        tooltip("cosmetic", "Cosmetic");
        tooltip("cosmetics_disabled", "Cosmetics disabled (right-click to toggle)");
        tooltip("cosmetics_enabled", "Cosmetics enabled (right-click to toggle)");
        tooltip("item.everlasting_food", "Not consumed when eaten");
        tooltip("item.novelty_drinking_hat", "'Hey! I'm #1, and I let gravity do my drinking!'");
        tooltip("item.umbrella.glider", "Slows your fall when held");
        tooltip("item.umbrella.shield", "Can be used as a shield");
        tooltip("missing_dependency", "Install Curios, Trinkets or Accessories to use this item");
        tooltip("per_food_point_restored", "For every food point restored:");
        tooltip("plus_mob_effect", "+%s");
        tooltip("toggle_keymapping", "Press %s to toggle");
        tooltip("tool_tier.none", "none");
        tooltip("tool_tier.wood", "wood");
        tooltip("tool_tier.stone", "stone");
        tooltip("tool_tier.iron", "iron");
        tooltip("tool_tier.diamond", "diamond");
        tooltip("tool_tier.netherite", "netherite");
        tooltip("when_hurt", "When hurt:");
        tooltip("when_hurt.is_fire", "When hurt by Fire:");
    }

    private void add(SoundEvent soundEvent, String value) {
        //noinspection ConstantConditions
        add("%s.subtitles.%s", BuiltInRegistries.SOUND_EVENT.getKey(soundEvent), value);
    }

    private void add(String key, ResourceLocation id, String value) {
        add(key.formatted(id.getNamespace(), id.getPath()), value);
    }

    private void addAbilityTooltip(DataComponentType<?> type, Holder<?> holder, String... s) {
        List<String> list = new java.util.ArrayList<>(List.of(s));
        list.addFirst(holder.unwrapKey().orElseThrow().location().getPath());
        addAbilityTooltip(type, list.toArray(String[]::new));
    }

    private void addAbilityTooltip(DataComponentType<?> type, String... s) {
        StringBuilder key = new StringBuilder("%s.tooltip.ability.%s");
        for (int i = 0; i < s.length - 1; i++) {
            key.append('.').append(s[i]);
        }
        add(key.toString(), Objects.requireNonNull(BuiltInRegistries.DATA_COMPONENT_TYPE.getKey(type)), s[s.length - 1]);
    }

    private void addEffects() {
        addEffect(ModMobEffects.MAGNETISM::value, "Magnetism");
    }

    private void tooltip(String key, String value) {
        add("%s.tooltip.%s".formatted(Artifacts.MOD_ID, key), value);
    }

    private static String fromCamelCasedString(String string) {
        return fromSnakeCasedString(CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, string));
    }

    private static String fromSnakeCasedString(String string) {
        String[] words = string.split("_");
        for (int i = 0; i < words.length; i++) {
            words[i] = Character.toUpperCase(words[i].charAt(0)) + words[i].substring(1);
        }
        return Strings.join(words, " ")
                .replace(" A ", " a ")
                .replace(" An ", " an ")
                .replaceFirst(" In ", " in ")
                .replace(" Of ", " of ")
                .replace(" On ", " on ")
                .replaceFirst(" Per ", " per ")
                .replace(" The ", " the ");
    }

    private static String configDescription(String... names) {
        return key("config", concat(names), "description");
    }

    private static String configTitle(String... names) {
        return key("config", concat(names), "title");
    }

    private static String key(String... names) {
        return concat(Artifacts.MOD_ID, concat(names));
    }

    private static String concat(String... names) {
        if (names.length == 0) {
            return "";
        }
        StringBuilder builder = new StringBuilder(names[0]);
        for (int i = 1; i < names.length; i++) {
            if (names[i].isEmpty()) {
                continue;
            }
            builder.append('.').append(names[i]);
        }
        return builder.toString();
    }
}
