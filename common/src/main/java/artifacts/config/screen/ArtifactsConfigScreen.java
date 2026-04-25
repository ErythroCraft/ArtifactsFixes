package artifacts.config.screen;

import artifacts.Artifacts;
import artifacts.config.ConfigManager;
import artifacts.config.value.ConfigValue;
import me.shedaniel.clothconfig2.api.AbstractConfigListEntry;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.gui.entries.SubCategoryListEntry;
import me.shedaniel.clothconfig2.impl.builders.AbstractFieldBuilder;
import me.shedaniel.clothconfig2.impl.builders.DropdownMenuBuilder;
import me.shedaniel.clothconfig2.impl.builders.FieldBuilder;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

import java.util.*;

public class ArtifactsConfigScreen {

    private final ConfigBuilder builder;

    private final Map<String, List<AbstractConfigListEntry<?>>> subCategories = new HashMap<>();

    public ArtifactsConfigScreen(Screen parent) {
        builder = ConfigBuilder.create()
                .setParentScreen(parent)
                .setTitle(Component.translatable("%s.config.title".formatted(Artifacts.MOD_ID)))
                .setSavingRunnable(() -> {
                    for (ConfigManager config : Artifacts.CONFIG.configs) {
                        config.onConfigChanged();
                    }
                });
    }

    public Screen build() {
        for (ConfigManager config : Artifacts.CONFIG.configs) {
            addConfigs(config);
        }

        // Add nested subcategories into their parents
        subCategories.keySet().stream().sorted(Comparator.reverseOrder()).forEach(key -> {
            List<String> keyParts = splitKey(key);
            String parentKey = String.join(".", keyParts.subList(0, keyParts.size() - 1));
            if (subCategories.containsKey(parentKey)) {
                subCategories.get(parentKey).add(buildSubCategory(key));
            }
        });

        // Add top-level subcategories to their categories
        subCategories.keySet().stream().sorted().forEach(key -> {
            List<String> keyParts = splitKey(key);
            String parentKey = String.join(".", keyParts.subList(0, keyParts.size() - 1));
            if (!subCategories.containsKey(parentKey)) {
                SubCategoryListEntry subCategory = (SubCategoryListEntry) buildSubCategory(key);
                appendSearchTagsToSubCategories(List.of(), subCategory.getFieldName(), subCategory.getValue());
                builder.getOrCreateCategory(getTitle(keyParts.getFirst())).addEntry(subCategory);
            }
        });

        return builder.build();
    }

    private AbstractConfigListEntry<?> buildSubCategory(String key) {
        String name = splitKey(key).getLast();
        List<AbstractConfigListEntry<?>> entries = subCategories.get(key);
        if (isItem(name)) {
            return new ItemSubCategoryListEntry(BuiltInRegistries.ITEM.getValue(Artifacts.id(name)), entries);
        }
        return builder.entryBuilder().startSubCategory(getTitle(key), List.copyOf(entries)).build();
    }

    @SuppressWarnings({"unchecked", "rawtypes"}) // Dumbass api uses raw types
    private static void appendSearchTagsToSubCategories(List<String> parentSearchTags, Component fieldName, List<AbstractConfigListEntry> entries) {
        List<String> searchTags = new ArrayList<>(parentSearchTags);
        searchTags.addAll(List.of(fieldName.getString().split(" ")));
        entries.forEach(value -> {
            // noinspection unchecked
            value.appendSearchTags(searchTags);
            if (value instanceof SubCategoryListEntry entry) {
                appendSearchTagsToSubCategories(searchTags, value.getFieldName(), entry.getValue());
            }
        });
    }

    private void addConfigEntry(ConfigCategory category, ConfigManager config, String key) {
        List<String> keyParts = splitKey(key);
        AbstractConfigListEntry<?> field = createField(config, config.getName(), key, config.getValues().get(key), config.getDescription(key).size());
        if (keyParts.size() == 1) {
            category.addEntry(field);
        } else {
            // Ensure all intermediate subcategories exist
            for (int i = 1; i < keyParts.size() - 1; i++) {
                String intermediateKey = config.getName() + '.' + String.join(".", keyParts.subList(0, i));
                getOrCreateSubCategory(intermediateKey);
            }
            String parentKey = config.getName() + '.' + String.join(".", keyParts.subList(0, keyParts.size() - 1));
            getOrCreateSubCategory(parentKey).add(field);
        }
    }

    private void addConfigs(ConfigManager config) {
        ConfigCategory category = builder.getOrCreateCategory(getTitle(config.getName()));
        config.getValues().keySet()
                .stream()
                .sorted(Comparator.comparing((String key) -> !key.endsWith("generateAsLoot"))
                        .thenComparing(Comparator.naturalOrder()))
                .forEach(key -> addConfigEntry(category, config, key));
    }

    private List<AbstractConfigListEntry<?>> getOrCreateSubCategory(String key) {
        return subCategories.computeIfAbsent(key, _ -> new ArrayList<>());
    }

    private AbstractConfigListEntry<?> createField(ConfigManager config, String categoryName, String key, ConfigValue<?> value, int tooltipCount) {
        String fullKey = categoryName + '.' + key;
        String name = splitKey(fullKey).getLast();
        if (!name.equals("cooldown") && !name.equals("enabled") && !name.equals("generateAsLoot")) {
            name = fullKey;
        }
        Component[] tooltips = getTooltips(fullKey, tooltipCount);
        FieldBuilder<?, ?, ?> configEntry = createConfigEntry(config, value, getTitle(name));
        applyTooltip(configEntry, tooltips);
        return configEntry.build();
    }

    private static void applyTooltip(FieldBuilder<?, ?, ?> builder, Component[] tooltips) {
        if (builder instanceof AbstractFieldBuilder<?, ?, ?> b) {
            b.setTooltip(tooltips);
        } else if (builder instanceof DropdownMenuBuilder<?> b) {
            b.setTooltip(tooltips);
        }
    }

    /* Config entries read and write from the config file directly when saved,
     * rather than modifying the in-memory config and then saving it to disk.
     * This allows connected clients to display and modify their own config options,
     * without overriding settings received from the server.
     */
    private <T> FieldBuilder<?, ?, ?> createConfigEntry(ConfigManager config, ConfigValue<T> value, Component title) {
        FieldBuilder<?, ?, ?> configEntry = value.type().getConfigEntryFactory().createConfigEntry(config, builder.entryBuilder(), title, value);
        configEntry.requireRestart(value.requiresRestart());
        return configEntry;
    }

    private static List<String> splitKey(String key) {
        return Arrays.asList(key.split("\\."));
    }

    private static Component getTitle(String categoryKey) {
        String name = categoryKey.substring(categoryKey.lastIndexOf('.') + 1);
        return isItem(name)
                ? BuiltInRegistries.ITEM.getValue(Artifacts.id(name)).getDefaultInstance().getItemName()
                : Component.translatable("%s.config.%s.title".formatted(Artifacts.MOD_ID, categoryKey));
    }

    private static boolean isItem(String name) {
        return Identifier.isValidPath(name) && BuiltInRegistries.ITEM.containsKey(Artifacts.id(name));
    }

    private static Component[] getTooltips(String name, int count) {
        if (count > 1) {
            Component[] tooltips = new Component[count];
            for (int i = 0; i < count; i++) {
                tooltips[i] = Component.translatable("%s.config.%s.description.%s".formatted(Artifacts.MOD_ID, name, i));
            }
            return tooltips;
        }
        // TODO: cleanup shared descriptions/titles
        String tooltipKey = name.endsWith("generateAsLoot") ? "generateAsLoot" : name;
        return new Component[] {
                Component.translatable("%s.config.%s.description".formatted(Artifacts.MOD_ID, tooltipKey))
        };
    }
}
