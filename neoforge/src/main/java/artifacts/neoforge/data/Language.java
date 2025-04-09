package artifacts.neoforge.data;

import artifacts.Artifacts;
import artifacts.ability.ArtifactAbility;
import artifacts.config.ConfigManager;
import artifacts.neoforge.data.tags.ItemTags;
import artifacts.registry.*;
import com.google.common.base.CaseFormat;
import joptsimple.internal.Strings;
import net.minecraft.core.Holder;
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
        add("artifacts.key.night_vision_goggles.toggle", "Toggle Night Vision Goggles");
        add("artifacts.key.universal_attractor.toggle", "Toggle Universal Attractor");
        add("artifacts.key_category", "Artifacts");
        add(ModSoundEvents.FART.value(), "Fart");
        add("curios.identifier.feet", "Feet");
        add("curios.modifiers.feet", "When on feet:");
    }

    private void addAbilities() {
        addAbilityTooltip(ModAbilities.APPLY_MOB_EFFECT_AFTER_DAMAGE, MobEffects.FIRE_RESISTANCE, "Applies a temporary fire resistance effect after taking fire damage");
        addAbilityTooltip(ModAbilities.APPLY_MOB_EFFECT_AFTER_DAMAGE, MobEffects.MOVEMENT_SPEED, "Increases the wearer's movement speed after taking damage");
        addAbilityTooltip(ModAbilities.APPLY_MOB_EFFECT_AFTER_EATING, MobEffects.DIG_SPEED, "Grants a temporary boost to mining speed after eating food");
        addAbilityTooltip(ModAbilities.ATTACKS_ABSORB_DAMAGE, "chance", "The wearer's melee attacks have a chance to absorb health");
        addAbilityTooltip(ModAbilities.ATTACKS_ABSORB_DAMAGE, "constant", "Causes the wearer's melee attacks to absorb health");
        addAbilityTooltip(ModAbilities.ATTACKS_INFLICT_MOB_EFFECT, MobEffects.WITHER, "chance", "Melee attacks have a chance to inflict a wither effect");
        addAbilityTooltip(ModAbilities.ATTACKS_INFLICT_MOB_EFFECT, MobEffects.WITHER, "constant", "Causes the wearer's melee attacks to inflict a wither effect");
        addAbilityTooltip(ModAbilities.ATTRACT_ITEMS, "Attracts nearby items");
        addAbilityTooltip(ModAbilities.ATTRIBUTE_MODIFIER, ModAttributes.ATTACK_BURNING_DURATION, "Causes the wearer's melee attacks to deal fire damage");
        addAbilityTooltip(ModAbilities.ATTRIBUTE_MODIFIER, Attributes.ATTACK_DAMAGE, "Increases damage dealt by the wearer");
        addAbilityTooltip(ModAbilities.ATTRIBUTE_MODIFIER, Attributes.ATTACK_KNOCKBACK, "Increases knockback dealt by the wearer");
        addAbilityTooltip(ModAbilities.ATTRIBUTE_MODIFIER, Attributes.ATTACK_SPEED, "Increases the wearer's attack speed");
        addAbilityTooltip(ModAbilities.ATTRIBUTE_MODIFIER, ModAttributes.DRINKING_SPEED, "Decreases the time it takes to drink items");
        addAbilityTooltip(ModAbilities.ATTRIBUTE_MODIFIER, ModAttributes.EATING_SPEED, "Decreases the time it takes to eat items");
        addAbilityTooltip(ModAbilities.ATTRIBUTE_MODIFIER, Attributes.FALL_DAMAGE_MULTIPLIER, "Reduces fall damage taken by the wearer");
        addAbilityTooltip(ModAbilities.ATTRIBUTE_MODIFIER, ModAttributes.FLATULENCE, "Increases the wearer's flatulence");
        addAbilityTooltip(ModAbilities.ATTRIBUTE_MODIFIER, ModAttributes.INVINCIBILITY_TICKS, "Increases the length of invincibility after taking damage");
        addAbilityTooltip(ModAbilities.ATTRIBUTE_MODIFIER, Attributes.JUMP_STRENGTH, "Increases the wearer's jump height");
        addAbilityTooltip(ModAbilities.ATTRIBUTE_MODIFIER, Attributes.KNOCKBACK_RESISTANCE, "Grants immunity to knockback");
        addAbilityTooltip(ModAbilities.ATTRIBUTE_MODIFIER, Attributes.MAX_HEALTH, "Increases the wearer's maximum health");
        addAbilityTooltip(ModAbilities.ATTRIBUTE_MODIFIER, ModAttributes.MOUNT_SPEED, "Increases the speed of ridden mounts");
        addAbilityTooltip(ModAbilities.ATTRIBUTE_MODIFIER, Attributes.SAFE_FALL_DISTANCE, "Increases the wearer's maximum safe fall distance");
        addAbilityTooltip(ModAbilities.ATTRIBUTE_MODIFIER, Attributes.SCALE, "Shrinks the wearer");
        addAbilityTooltip(ModAbilities.ATTRIBUTE_MODIFIER, ModAttributes.SLIP_RESISTANCE, "Makes ice less slippery to walk on");
        addAbilityTooltip(ModAbilities.ATTRIBUTE_MODIFIER, ModAttributes.MOVEMENT_SPEED_ON_SNOW, "Increases the wearer's walking speed on snow");
        addAbilityTooltip(ModAbilities.ATTRIBUTE_MODIFIER, ModAttributes.SPRINTING_SPEED, "Increases the wearer's movement speed while sprinting");
        addAbilityTooltip(ModAbilities.ATTRIBUTE_MODIFIER, ModAttributes.SPRINTING_STEP_HEIGHT, "Increases the wearer's step height while sprinting");
        addAbilityTooltip(ModAbilities.ATTRIBUTE_MODIFIER, "generic.swim_speed", "Improves agility in water");
        addAbilityTooltip(ModAbilities.ATTRIBUTE_MODIFIER, Attributes.BLOCK_BREAK_SPEED, "Increases the wearer's mining speed");
        addAbilityTooltip(ModAbilities.ATTRIBUTE_MODIFIER, ModAttributes.ENTITY_EXPERIENCE, "Increases experience dropped by creatures");
        addAbilityTooltip(ModAbilities.ATTRIBUTE_MODIFIER, ModAttributes.VILLAGER_REPUTATION, "Decreases the trading prices of villagers");
        addAbilityTooltip(ModAbilities.DAMAGE_IMMUNITY, ModTags.IS_HOT_FLOOR.location().getPath(), "Grants protection against hot floor damage");
        addAbilityTooltip(ModAbilities.DAMAGE_IMMUNITY, DamageTypeTags.IS_LIGHTNING.location().getPath(), "Grants protection against lightning strikes");
        addAbilityTooltip(ModAbilities.DOUBLE_JUMP, "Allows the wearer to double jump");
        addAbilityTooltip(ModAbilities.ENDER_PEARLS_COST_HUNGER, "free", "Ender Pearls are not consumed when thrown");
        addAbilityTooltip(ModAbilities.ENDER_PEARLS_COST_HUNGER, "cost", "Ender Pearls are not consumed, but cost hunger instead");
        addAbilityTooltip(ModAbilities.GROW_PLANTS_AFTER_EATING, "Plants grow after eating when standing on grass");
        addAbilityTooltip(ModAbilities.INCREASE_ENCHANTMENT_LEVEL, "fortune", "multiple_levels", "Applies %s extra levels of fortune to mined blocks");
        addAbilityTooltip(ModAbilities.INCREASE_ENCHANTMENT_LEVEL, "fortune", "single_level", "Applies an extra level of fortune to mined blocks");
        addAbilityTooltip(ModAbilities.INCREASE_ENCHANTMENT_LEVEL, "looting", "multiple_levels", "Applies %s extra levels of looting to killed entities");
        addAbilityTooltip(ModAbilities.INCREASE_ENCHANTMENT_LEVEL, "looting", "single_level", "Applies an extra level of looting to killed entities");
        addAbilityTooltip(ModAbilities.INCREASE_ENCHANTMENT_LEVEL, "luck_of_the_sea", "multiple_levels", "Applies %s extra levels of Luck of the Sea when fishing");
        addAbilityTooltip(ModAbilities.INCREASE_ENCHANTMENT_LEVEL, "luck_of_the_sea", "single_level", "Applies an extra Luck of the Sea when fishing");
        addAbilityTooltip(ModAbilities.INCREASE_ENCHANTMENT_LEVEL, "lure", "multiple_levels", "Applies %s extra levels of Lure when fishing");
        addAbilityTooltip(ModAbilities.INCREASE_ENCHANTMENT_LEVEL, "lure", "single_level", "Applies an extra level of Lure when fishing");
        addAbilityTooltip(ModAbilities.LIMITED_WATER_BREATHING, "infinite", "Allows the wearer to breathe underwater");
        addAbilityTooltip(ModAbilities.LIMITED_WATER_BREATHING, "limited", "Allows the wearer to breathe underwater for a limited amount of time");
        addAbilityTooltip(ModAbilities.MOB_EFFECT, "invisibility", "Turns the wearer invisible");
        addAbilityTooltip(ModAbilities.NIGHT_VISION, "full", "Allows the wearer to see in the dark");
        addAbilityTooltip(ModAbilities.NIGHT_VISION, "partial", "Allows the wearer to see in the dark slightly");
        addAbilityTooltip(ModAbilities.NULLIFY_ENDER_PEARL_DAMAGE, "Ender Pearls deal no damage");
        addAbilityTooltip(ModAbilities.REMOVE_BAD_EFFECTS, "Greatly reduces the duration of negative effects");
        addAbilityTooltip(ModAbilities.REPLENISH_HUNGER_ON_GRASS, "Slowly replenishes hunger while walking on grass");
        addAbilityTooltip(ModAbilities.SCARE_CREEPERS, "Creepers avoid the wearer");
        addAbilityTooltip(ModAbilities.SET_ATTACKERS_ON_FIRE, "fire_resistance", "Grants fire resistance after lighting an attacker on fire");
        addAbilityTooltip(ModAbilities.SET_ATTACKERS_ON_FIRE, "chance", "Has a chance to light attackers on fire");
        addAbilityTooltip(ModAbilities.SET_ATTACKERS_ON_FIRE, "constant", "Attacking entities are lit on fire");
        addAbilityTooltip(ModAbilities.SINKING, "Allows the wearer to move freely in water");
        addAbilityTooltip(ModAbilities.SMELT_ORES, "Automatically smelts mined ores");
        addAbilityTooltip(ModAbilities.SNEAK_ON_FLUIDS, "lava", "Allows the wearer to stand on lava while sneaking");
        addAbilityTooltip(ModAbilities.SPRINT_ON_FLUIDS, "Allows the wearer to walk on fluids while sprinting");
        addAbilityTooltip(ModAbilities.STRIKE_ATTACKERS_WITH_LIGHTNING, "chance", "Has a chance to strike attackers with lightning");
        addAbilityTooltip(ModAbilities.STRIKE_ATTACKERS_WITH_LIGHTNING, "constant", "Attacking entities are struck by lightning");
        addAbilityTooltip(ModAbilities.SWIM_IN_AIR, "keymapping", "Press %s while in the air to start swimming");
        addAbilityTooltip(ModAbilities.SWIM_IN_AIR, "swimming", "Allows the wearer to swim in the air for a limited period of time");
        addAbilityTooltip(ModAbilities.TELEPORT_ON_DEATH, "chance", "A fatal hit has a chance to teleport you somewhere else instead");
        addAbilityTooltip(ModAbilities.TELEPORT_ON_DEATH, "constant", "A fatal hit teleports you somewhere else instead");
        addAbilityTooltip(ModAbilities.TELEPORT_ON_DEATH, "not_consumed", "Not consumed on use");
        addAbilityTooltip(ModAbilities.THORNS, "chance", "Has a chance to damage attackers");
        addAbilityTooltip(ModAbilities.THORNS, "constant", "Attacking entities are damaged as well");
        addAbilityTooltip(ModAbilities.UPGRADE_TOOL_TIER, "Increases the wearer's base mining level to %s");
        addAbilityTooltip(ModAbilities.WALK_ON_POWDER_SNOW, "Allows the wearer to walk on Powder Snow");
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

    private void addAbilityTooltip(Holder<? extends ArtifactAbility.Type<?>> type, Holder<?> holder, String... s) {
        List<String> list = new java.util.ArrayList<>(List.of(s));
        list.addFirst(holder.unwrapKey().orElseThrow().location().getPath());
        addAbilityTooltip(type, list.toArray(String[]::new));
    }

    private void addAbilityTooltip(Holder<? extends ArtifactAbility.Type<?>> type, String... s) {
        StringBuilder key = new StringBuilder("%s.tooltip.ability.%s");
        for (int i = 0; i < s.length - 1; i++) {
            key.append('.').append(s[i]);
        }
        // TODO use holder.unwrapKey
        // noinspection ConstantConditions
        add(key.toString(), ModAbilities.getRegistry().getKey(type.value()), s[s.length - 1]);
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
