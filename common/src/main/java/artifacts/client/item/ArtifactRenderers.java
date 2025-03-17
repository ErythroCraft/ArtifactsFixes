package artifacts.client.item;

import artifacts.client.item.model.*;
import artifacts.client.item.renderer.*;
import artifacts.integration.client.ClientEquipmentIntegrationUtils;
import artifacts.platform.PlatformServices;
import artifacts.registry.ModItems;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.item.Item;

import java.util.function.Supplier;

public class ArtifactRenderers {

    public static void register() {
        // head
        register(ModItems.PLASTIC_DRINKING_HAT.value(), () -> new GenericArtifactRenderer("plastic_drinking_hat", new HeadModel(bakeLayer(ArtifactLayers.DRINKING_HAT))));
        register(ModItems.NOVELTY_DRINKING_HAT.value(), () -> new GenericArtifactRenderer("novelty_drinking_hat", new HeadModel(bakeLayer(ArtifactLayers.DRINKING_HAT))));
        register(ModItems.SNORKEL.value(), () -> new GenericArtifactRenderer("snorkel", new HeadModel(bakeLayer(ArtifactLayers.SNORKEL), RenderType::entityTranslucent)));
        register(ModItems.NIGHT_VISION_GOGGLES.value(), () -> new GlowingArtifactRenderer("night_vision_goggles", new HeadModel(bakeLayer(ArtifactLayers.NIGHT_VISION_GOGGLES))));
        register(ModItems.SUPERSTITIOUS_HAT.value(), () -> new GenericArtifactRenderer("superstitious_hat", new HeadModel(bakeLayer(ArtifactLayers.SUPERSTITIOUS_HAT), RenderType::entityCutoutNoCull)));
        register(ModItems.VILLAGER_HAT.value(), () -> new GenericArtifactRenderer("villager_hat", new HeadModel(bakeLayer(ArtifactLayers.BRIMMED_HAT))));
        register(ModItems.COWBOY_HAT.value(), () -> new GenericArtifactRenderer("cowboy_hat", new HeadModel(bakeLayer(ArtifactLayers.COWBOY_HAT))));
        register(ModItems.ANGLERS_HAT.value(), () -> new GenericArtifactRenderer("anglers_hat", new HeadModel(bakeLayer(ArtifactLayers.ANGLERS_HAT))));

        // necklace
        register(ModItems.LUCKY_SCARF.value(), () -> new GenericArtifactRenderer("lucky_scarf", new ScarfModel(bakeLayer(ArtifactLayers.SCARF), RenderType::entityCutoutNoCull)));
        register(ModItems.SCARF_OF_INVISIBILITY.value(), () -> new GenericArtifactRenderer("scarf_of_invisibility",  new ScarfModel(bakeLayer(ArtifactLayers.SCARF), RenderType::entityTranslucent)));
        register(ModItems.CROSS_NECKLACE.value(), () -> new GenericArtifactRenderer("cross_necklace", new NecklaceModel(bakeLayer(ArtifactLayers.CROSS_NECKLACE))));
        register(ModItems.PANIC_NECKLACE.value(), () -> new GenericArtifactRenderer("panic_necklace", new NecklaceModel(bakeLayer(ArtifactLayers.PANIC_NECKLACE))));
        register(ModItems.SHOCK_PENDANT.value(), () -> new GenericArtifactRenderer("shock_pendant", new NecklaceModel(bakeLayer(ArtifactLayers.PENDANT))));
        register(ModItems.FLAME_PENDANT.value(), () -> new GenericArtifactRenderer("flame_pendant", new NecklaceModel(bakeLayer(ArtifactLayers.PENDANT))));
        register(ModItems.THORN_PENDANT.value(), () -> new GenericArtifactRenderer("thorn_pendant", new NecklaceModel(bakeLayer(ArtifactLayers.PENDANT))));
        register(ModItems.CHARM_OF_SINKING.value(), () -> new GenericArtifactRenderer("charm_of_sinking", new NecklaceModel(bakeLayer(ArtifactLayers.CHARM_OF_SINKING))));
        register(ModItems.CHARM_OF_SHRINKING.value(), () -> new GenericArtifactRenderer("charm_of_shrinking", new NecklaceModel(bakeLayer(ArtifactLayers.CHARM_OF_SHRINKING))));

        // belt
        register(ModItems.CLOUD_IN_A_BOTTLE.value(), () -> new BeltArtifactRenderer("cloud_in_a_bottle", BeltModel.createCloudInABottleModel()));
        register(ModItems.OBSIDIAN_SKULL.value(), () -> new BeltArtifactRenderer("obsidian_skull", BeltModel.createObsidianSkullModel()));
        register(ModItems.ANTIDOTE_VESSEL.value(), () -> new BeltArtifactRenderer("antidote_vessel", BeltModel.createAntidoteVesselModel()));
        register(ModItems.UNIVERSAL_ATTRACTOR.value(), () -> new BeltArtifactRenderer("universal_attractor", BeltModel.createUniversalAttractorModel()));
        register(ModItems.CRYSTAL_HEART.value(), () -> new BeltArtifactRenderer("crystal_heart", BeltModel.createCrystalHeartModel()));
        register(ModItems.HELIUM_FLAMINGO.value(), () -> new GenericArtifactRenderer("helium_flamingo", BeltModel.createHeliumFlamingoModel()));
        register(ModItems.CHORUS_TOTEM.value(), () -> new BeltArtifactRenderer("chorus_totem", BeltModel.createChorusTotemModel()));
        register(ModItems.WARP_DRIVE.value(), () -> new WarpDriveRenderer("warp_drive", BeltModel.createWarpDriveModel()));

        // hands
        register(ModItems.DIGGING_CLAWS.value(), () -> new GloveArtifactRenderer("digging_claws", "digging_claws", ArmsModel::createClawsModel));
        register(ModItems.FERAL_CLAWS.value(), () -> new GloveArtifactRenderer("feral_claws", "feral_claws", ArmsModel::createClawsModel));
        register(ModItems.POWER_GLOVE.value(), () -> new GloveArtifactRenderer("power_glove", ArmsModel::createGloveModel));
        register(ModItems.FIRE_GAUNTLET.value(), () -> new GlowingGloveArtifactRenderer("fire_gauntlet", ArmsModel::createGloveModel));
        register(ModItems.POCKET_PISTON.value(), () -> new GloveArtifactRenderer("pocket_piston", ArmsModel::createPocketPistonModel));
        register(ModItems.VAMPIRIC_GLOVE.value(), () -> new GloveArtifactRenderer("vampiric_glove", ArmsModel::createGloveModel));
        register(ModItems.GOLDEN_HOOK.value(), () -> new GloveArtifactRenderer("golden_hook", ArmsModel::createGoldenHookModel));
        register(ModItems.ONION_RING.value(), () -> new GloveArtifactRenderer("onion_ring", ArmsModel::createOnionRingModel));
        register(ModItems.PICKAXE_HEATER.value(), () -> new GlowingGloveArtifactRenderer("pickaxe_heater", ArmsModel::createPickaxeHeaterModel));
        register(ModItems.WITHERED_BRACELET.value(), () -> new GloveArtifactRenderer("withered_bracelet", ArmsModel::createWitheredBraceletModel));

        // feet
        register(ModItems.AQUA_DASHERS.value(), () -> new BootArtifactRenderer("aqua_dashers", hasArmor -> new LegsModel(bakeLayer(hasArmor ? ArtifactLayers.AQUA_DASHERS_LARGE : ArtifactLayers.AQUA_DASHERS_SMALL))));
        register(ModItems.BUNNY_HOPPERS.value(), () -> new GenericArtifactRenderer("bunny_hoppers", new LegsModel(bakeLayer(ArtifactLayers.BUNNY_HOPPERS))));
        register(ModItems.KITTY_SLIPPERS.value(), () -> new GenericArtifactRenderer("kitty_slippers", new LegsModel(bakeLayer(ArtifactLayers.KITTY_SLIPPERS))));
        register(ModItems.RUNNING_SHOES.value(), () -> new BootArtifactRenderer("running_shoes", hasArmor -> new LegsModel(bakeLayer(hasArmor ? ArtifactLayers.BOOTS_LARGE : ArtifactLayers.BOOTS_SMALL))));
        register(ModItems.SNOWSHOES.value(), () -> new GenericArtifactRenderer("snowshoes", new LegsModel(bakeLayer(ArtifactLayers.SNOWSHOES))));
        register(ModItems.STEADFAST_SPIKES.value(), () -> new GenericArtifactRenderer("steadfast_spikes", new LegsModel(bakeLayer(ArtifactLayers.STEADFAST_SPIKES))));
        register(ModItems.FLIPPERS.value(), () -> new GenericArtifactRenderer("flippers", new LegsModel(bakeLayer(ArtifactLayers.FLIPPERS))));
        register(ModItems.ROOTED_BOOTS.value(), () -> new BootArtifactRenderer("rooted_boots", hasArmor -> new LegsModel(bakeLayer(hasArmor ? ArtifactLayers.BOOTS_LARGE : ArtifactLayers.BOOTS_SMALL))));
        register(ModItems.STRIDER_SHOES.value(), () -> new BootArtifactRenderer("strider_shoes", hasArmor -> new LegsModel(bakeLayer(hasArmor ? ArtifactLayers.BOOTS_LARGE : ArtifactLayers.BOOTS_SMALL))));

        // curio
        register(ModItems.WHOOPEE_CUSHION.value(), () -> new GenericArtifactRenderer("whoopee_cushion", new HeadModel(bakeLayer(ArtifactLayers.WHOOPEE_CUSHION))));
    }

    public static ModelPart bakeLayer(ModelLayerLocation layerLocation) {
        return Minecraft.getInstance().getEntityModels().bakeLayer(layerLocation);
    }

    public static void register(Item item, Supplier<ArtifactRenderer> rendererSupplier) {
        ClientEquipmentIntegrationUtils.registerArtifactRenderer(item, rendererSupplier);
    }
}
