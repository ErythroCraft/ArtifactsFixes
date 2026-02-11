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

    public static void gatherServerData(GatherDataEvent.Server event) {
        DataGenerator generator = event.getGenerator();
        PackOutput packOutput = generator.getPackOutput();
        CompletableFuture<HolderLookup.Provider> registries = event.getLookupProvider();
        boolean includeServer = true;

        LootModifiers lootModifiers = new LootModifiers(packOutput, registries);

        generator.addProvider(includeServer, new BlockTags(packOutput, registries));
        generator.addProvider(includeServer, new ItemTags(packOutput, registries));
        generator.addProvider(includeServer, lootModifiers);
        generator.addProvider(includeServer, new LootTables(packOutput, lootModifiers, registries));
        generator.addProvider(includeServer, new EntityTypeTags(packOutput, registries));
        generator.addProvider(includeServer, new MobEffectTags(packOutput, registries));
        generator.addProvider(includeServer, new DamageTypeTags(packOutput, registries));
        generator.addProvider(includeServer, new GameEventTags(packOutput, registries));
        generator.addProvider(includeServer, new SoundDefinitions(packOutput));
        generator.addProvider(includeServer, new Advancements(packOutput, registries));
        generator.addProvider(includeServer, new DataMaps(packOutput, registries));

        var worldGen = new DatapackBuiltinEntriesProvider(packOutput, registries, createLevelProvider(), Set.of(Artifacts.MOD_ID));
        generator.addProvider(includeServer, worldGen);
    }

    public static void gatherClientData(GatherDataEvent.Client event) {
        DataGenerator generator = event.getGenerator();
        PackOutput packOutput = generator.getPackOutput();
        boolean includeClient = true;

        generator.addProvider(includeClient, new Language(packOutput));
        generator.addProvider(includeClient, new ItemModels(packOutput));

    }

    public static RegistrySetBuilder createLevelProvider() {
        RegistrySetBuilder builder = new RegistrySetBuilder();
        builder.add(Registries.CONFIGURED_FEATURE, ConfiguredFeatures::create);
        builder.add(Registries.PLACED_FEATURE, PlacedFeatures::create);
        return builder;
    }
}
