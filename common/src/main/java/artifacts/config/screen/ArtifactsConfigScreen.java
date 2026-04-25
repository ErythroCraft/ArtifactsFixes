package artifacts.config.screen;

import artifacts.Artifacts;
import artifacts.config.ConfigManager;
import artifacts.config.value.ConfigValue;
import me.shedaniel.clothconfig2.api.AbstractConfigListEntry;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.impl.builders.AbstractFieldBuilder;
import me.shedaniel.clothconfig2.impl.builders.DropdownMenuBuilder;
import me.shedaniel.clothconfig2.impl.builders.FieldBuilder;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

import java.util.*;

// TODO cleanup this mess
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

        subCategories.keySet().stream().sorted().forEach(key -> {
            List<String> keyParts = splitKey(key);
            ConfigCategory category = builder.getOrCreateCategory(getTitle(keyParts.getFirst()));
            AbstractConfigListEntry<?> subCategory;
            String name = keyParts.getLast();
            if (Identifier.isValidPath(name) && BuiltInRegistries.ITEM.containsKey(Artifacts.id(name))) {
                subCategory = new ItemSubCategoryListEntry(BuiltInRegistries.ITEM.getValue(Artifacts.id(name)), subCategories.get(key));
            } else {
                subCategory = builder.entryBuilder().startSubCategory(getTitle(key), List.copyOf(subCategories.get(key))).build();
            }
            category.addEntry(subCategory);
        });

        return builder.build();
    }

    private void addConfigs(ConfigManager config) {
        ConfigCategory configBuilder = builder.getOrCreateCategory(getTitle(config.getName()));
        config.getValues().keySet()
                .stream()
                // Sort fields alphabetically, and move nested fields to bottom
                .sorted()
                // Move generateAsLoot config options to top
                .sorted(Comparator.comparing(key -> !key.endsWith("generateAsLoot")))
                .forEach(key -> {
            List<String> keyParts = splitKey(key);
            ConfigValue<?> value = config.getValues().get(key);
            AbstractConfigListEntry<?> field = createField(config, config.getName(), key, value, config.getDescription(key).size());
            if (keyParts.size() == 1) {
                configBuilder.addEntry(field);
            } else {
                String subCategoryKey = config.getName() + '.' + keyParts.getFirst();
                List<AbstractConfigListEntry<?>> subCategory = getOrCreateSubCategory(subCategoryKey);
                subCategory.add(field);
            }
        });
    }

    private List<AbstractConfigListEntry<?>> getOrCreateSubCategory(String key) {
        if (subCategories.containsKey(key)) {
            return subCategories.get(key);
        }
        subCategories.put(key, new ArrayList<>());
        return subCategories.get(key);
    }

    private AbstractConfigListEntry<?> createField(ConfigManager config, String categoryName, String key, ConfigValue<?> value, int tooltipCount) {
        key = categoryName + '.' + key;
        String name = splitKey(key).getLast();
        name = name.equals("cooldown") || name.equals("enabled") || name.equals("generateAsLoot") ? name : key;
        Component[] tooltips = getTooltips(key, tooltipCount);
        FieldBuilder<?, ?, ?> configEntry = createConfigEntry(config, value, getTitle(name));
        if (configEntry instanceof AbstractFieldBuilder<?,?,?> fieldBuilder) {
            fieldBuilder.setTooltip(tooltips);
        } else if (configEntry instanceof DropdownMenuBuilder<?> dropdownBuilder) {
            dropdownBuilder.setTooltip(tooltips);
        }
        return configEntry.build();
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
        return new ArrayList<>(Arrays.asList(key.split("\\.")));
    }

    private static Component getTitle(String categoryKey) {
        String name = categoryKey.substring(categoryKey.lastIndexOf('.') + 1);
        if (Identifier.isValidPath(name) && BuiltInRegistries.ITEM.containsKey(Artifacts.id(name))) {
            return BuiltInRegistries.ITEM.getValue(Artifacts.id(name)).getDefaultInstance().getItemName();
        }
        return Component.translatable("%s.config.%s.title".formatted(Artifacts.MOD_ID, categoryKey));
    }

    private static Component[] getTooltips(String name, int count) {
        Component[] tooltips = new Component[count];
        if (count > 1) {
            for (int i = 0; i < tooltips.length; i++) {
                tooltips[i] = Component.translatable("%s.config.%s.description.%s".formatted(Artifacts.MOD_ID, name, i));
            }
        } else {
            if (name.endsWith("generateAsLoot")) {
                tooltips[0] = Component.translatable("%s.config.%s.description".formatted(Artifacts.MOD_ID, "generateAsLoot"));
            } else {
                tooltips[0] = Component.translatable("%s.config.%s.description".formatted(Artifacts.MOD_ID, name));
            }
        }
        return tooltips;
    }
}
