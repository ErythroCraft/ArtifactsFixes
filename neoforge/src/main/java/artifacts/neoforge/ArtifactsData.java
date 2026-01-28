package artifacts.neoforge;

import artifacts.Artifacts;
import artifacts.neoforge.data.*;
import artifacts.neoforge.data.tags.*;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.DatapackBuiltinEntriesProvider;
import net.neoforged.neoforge.data.event.GatherDataEvent;

import java.util.Set;
import java.util.concurrent.CompletableFuture;

public class ArtifactsData {

    public static void gatherData(GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();
        PackOutput packOutput = event.getGenerator().getPackOutput();
        CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();

        LootModifiers lootModifiers = new LootModifiers(packOutput, lookupProvider);

        generator.addProvider(event.includeServer(), new BlockTags(packOutput, lookupProvider));
        generator.addProvider(event.includeServer(), new ItemTags(packOutput, lookupProvider));
        generator.addProvider(event.includeServer(), lootModifiers);
        generator.addProvider(event.includeServer(), new LootTables(packOutput, lootModifiers, lookupProvider));
        generator.addProvider(event.includeServer(), new EntityTypeTags(packOutput, lookupProvider));
        generator.addProvider(event.includeServer(), new MobEffectTags(packOutput, lookupProvider));
        generator.addProvider(event.includeServer(), new DamageTypeTags(packOutput, lookupProvider));
        generator.addProvider(event.includeServer(), new GameEventTags(packOutput, lookupProvider));
        generator.addProvider(event.includeServer(), new SoundDefinitions(packOutput));
        generator.addProvider(event.includeServer(), new Advancements(packOutput, lookupProvider));
        generator.addProvider(event.includeServer(), new DataMaps(packOutput, lookupProvider));

        generator.addProvider(event.includeClient(), new Language(packOutput));
        generator.addProvider(event.includeClient(), new ItemModels(packOutput));

        generator.addProvider(event.includeServer(), new DatapackBuiltinEntriesProvider(generator.getPackOutput(), event.getLookupProvider(), createLevelProvider(), Set.of(Artifacts.MOD_ID)));
    }

    public static RegistrySetBuilder createLevelProvider() {
        RegistrySetBuilder builder = new RegistrySetBuilder();
        builder.add(Registries.CONFIGURED_FEATURE, ConfiguredFeatures::create);
        builder.add(Registries.PLACED_FEATURE, PlacedFeatures::create);
        return builder;
    }
}
