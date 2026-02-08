package artifacts.client.item;

import artifacts.client.item.model.*;
import artifacts.client.item.renderer.*;
import artifacts.equipment.client.EquipmentRenderingManager;
import artifacts.registry.ModItems;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.world.item.Item;

import java.util.function.Supplier;

public class ArtifactRenderers {

    public static void register() {
        // head
        register(ModItems.PLASTIC_DRINKING_HAT.value(), () -> SimpleArtifactRenderer.create("plastic_drinking_hat", new HeadModel(bakeLayer(ArtifactLayers.DRINKING_HAT))));
        register(ModItems.NOVELTY_DRINKING_HAT.value(), () -> SimpleArtifactRenderer.create("novelty_drinking_hat", new HeadModel(bakeLayer(ArtifactLayers.DRINKING_HAT))));
        register(ModItems.SNORKEL.value(), () -> SimpleArtifactRenderer.create("snorkel", new HeadModel(bakeLayer(ArtifactLayers.SNORKEL), RenderTypes::entityTranslucent)));
        register(ModItems.NIGHT_VISION_GOGGLES.value(), () -> SimpleArtifactRenderer.createGlowing("night_vision_goggles", new HeadModel(bakeLayer(ArtifactLayers.NIGHT_VISION_GOGGLES))));
        register(ModItems.SUPERSTITIOUS_HAT.value(), () -> SimpleArtifactRenderer.create("superstitious_hat", new HeadModel(bakeLayer(ArtifactLayers.SUPERSTITIOUS_HAT), RenderTypes::entityCutoutNoCull)));
        register(ModItems.VILLAGER_HAT.value(), () -> SimpleArtifactRenderer.create("villager_hat", new HeadModel(bakeLayer(ArtifactLayers.BRIMMED_HAT))));
        register(ModItems.COWBOY_HAT.value(), () -> SimpleArtifactRenderer.create("cowboy_hat", new HeadModel(bakeLayer(ArtifactLayers.COWBOY_HAT))));
        register(ModItems.ANGLERS_HAT.value(), () -> SimpleArtifactRenderer.create("anglers_hat", new HeadModel(bakeLayer(ArtifactLayers.ANGLERS_HAT))));

        // necklace
        register(ModItems.LUCKY_SCARF.value(), () -> SimpleArtifactRenderer.create("lucky_scarf", new ScarfModel(bakeLayer(ArtifactLayers.SCARF), RenderTypes::entityCutoutNoCull)));
        register(ModItems.SCARF_OF_INVISIBILITY.value(), () -> SimpleArtifactRenderer.create("scarf_of_invisibility",  new ScarfModel(bakeLayer(ArtifactLayers.SCARF), RenderTypes::entityTranslucent)));
        register(ModItems.CROSS_NECKLACE.value(), () -> SimpleArtifactRenderer.create("cross_necklace", new NecklaceModel(bakeLayer(ArtifactLayers.CROSS_NECKLACE))));
        register(ModItems.PANIC_NECKLACE.value(), () -> SimpleArtifactRenderer.create("panic_necklace", new NecklaceModel(bakeLayer(ArtifactLayers.PANIC_NECKLACE))));
        register(ModItems.SHOCK_PENDANT.value(), () -> SimpleArtifactRenderer.create("shock_pendant", new NecklaceModel(bakeLayer(ArtifactLayers.PENDANT))));
        register(ModItems.FLAME_PENDANT.value(), () -> SimpleArtifactRenderer.create("flame_pendant", new NecklaceModel(bakeLayer(ArtifactLayers.PENDANT))));
        register(ModItems.THORN_PENDANT.value(), () -> SimpleArtifactRenderer.create("thorn_pendant", new NecklaceModel(bakeLayer(ArtifactLayers.PENDANT))));
        register(ModItems.CHARM_OF_SINKING.value(), () -> SimpleArtifactRenderer.create("charm_of_sinking", new NecklaceModel(bakeLayer(ArtifactLayers.CHARM_OF_SINKING))));
        register(ModItems.CHARM_OF_SHRINKING.value(), () -> SimpleArtifactRenderer.create("charm_of_shrinking", new NecklaceModel(bakeLayer(ArtifactLayers.CHARM_OF_SHRINKING))));

        // belt
        register(ModItems.CLOUD_IN_A_BOTTLE.value(), () -> new BeltArtifactRenderer("cloud_in_a_bottle", BeltModel.createCloudInABottleModel()));
        register(ModItems.OBSIDIAN_SKULL.value(), () -> new BeltArtifactRenderer("obsidian_skull", BeltModel.createObsidianSkullModel()));
        register(ModItems.ANTIDOTE_VESSEL.value(), () -> new BeltArtifactRenderer("antidote_vessel", BeltModel.createAntidoteVesselModel()));
        register(ModItems.UNIVERSAL_ATTRACTOR.value(), () -> new BeltArtifactRenderer("universal_attractor", BeltModel.createUniversalAttractorModel()));
        register(ModItems.CRYSTAL_HEART.value(), () -> new BeltArtifactRenderer("crystal_heart", BeltModel.createCrystalHeartModel()));
        register(ModItems.HELIUM_FLAMINGO.value(), () -> SimpleArtifactRenderer.create("helium_flamingo", BeltModel.createHeliumFlamingoModel()));
        register(ModItems.CHORUS_TOTEM.value(), () -> new BeltArtifactRenderer("chorus_totem", BeltModel.createChorusTotemModel()));
        register(ModItems.WARP_DRIVE.value(), () -> new WarpDriveRenderer("warp_drive", BeltModel.createWarpDriveModel()));

        // hands
        register(ModItems.DIGGING_CLAWS.value(), () -> GloveArtifactRenderer.create("digging_claws", "digging_claws", ArmsModel::createClawsModel));
        register(ModItems.FERAL_CLAWS.value(), () -> GloveArtifactRenderer.create("feral_claws", "feral_claws", ArmsModel::createClawsModel));
        register(ModItems.POWER_GLOVE.value(), () -> GloveArtifactRenderer.create("power_glove", ArmsModel::createGloveModel));
        register(ModItems.FIRE_GAUNTLET.value(), () -> GloveArtifactRenderer.create("fire_gauntlet", ArmsModel::createGloveModel));
        register(ModItems.POCKET_PISTON.value(), () -> GloveArtifactRenderer.create("pocket_piston", ArmsModel::createPocketPistonModel));
        register(ModItems.VAMPIRIC_GLOVE.value(), () -> GloveArtifactRenderer.create("vampiric_glove", ArmsModel::createGloveModel));
        register(ModItems.GOLDEN_HOOK.value(), () -> GloveArtifactRenderer.create("golden_hook", ArmsModel::createGoldenHookModel));
        register(ModItems.ONION_RING.value(), () -> GloveArtifactRenderer.create("onion_ring", ArmsModel::createOnionRingModel));
        register(ModItems.PICKAXE_HEATER.value(), () -> GloveArtifactRenderer.create("pickaxe_heater", ArmsModel::createPickaxeHeaterModel));
        register(ModItems.WITHERED_BRACELET.value(), () -> GloveArtifactRenderer.create("withered_bracelet", ArmsModel::createWitheredBraceletModel));

        // feet
        register(ModItems.AQUA_DASHERS.value(), () -> new BootArtifactRenderer("aqua_dashers", hasArmor -> new LegsModel(bakeLayer(hasArmor ? ArtifactLayers.AQUA_DASHERS_LARGE : ArtifactLayers.AQUA_DASHERS_SMALL))));
        register(ModItems.BUNNY_HOPPERS.value(), () -> SimpleArtifactRenderer.create("bunny_hoppers", new LegsModel(bakeLayer(ArtifactLayers.BUNNY_HOPPERS))));
        register(ModItems.KITTY_SLIPPERS.value(), () -> SimpleArtifactRenderer.create("kitty_slippers", new LegsModel(bakeLayer(ArtifactLayers.KITTY_SLIPPERS))));
        register(ModItems.RUNNING_SHOES.value(), () -> new BootArtifactRenderer("running_shoes", hasArmor -> new LegsModel(bakeLayer(hasArmor ? ArtifactLayers.BOOTS_LARGE : ArtifactLayers.BOOTS_SMALL))));
        register(ModItems.SNOWSHOES.value(), () -> SimpleArtifactRenderer.create("snowshoes", new LegsModel(bakeLayer(ArtifactLayers.SNOWSHOES))));
        register(ModItems.STEADFAST_SPIKES.value(), () -> SimpleArtifactRenderer.create("steadfast_spikes", new LegsModel(bakeLayer(ArtifactLayers.STEADFAST_SPIKES))));
        register(ModItems.FLIPPERS.value(), () -> SimpleArtifactRenderer.create("flippers", new LegsModel(bakeLayer(ArtifactLayers.FLIPPERS))));
        register(ModItems.ROOTED_BOOTS.value(), () -> new BootArtifactRenderer("rooted_boots", hasArmor -> new LegsModel(bakeLayer(hasArmor ? ArtifactLayers.BOOTS_LARGE : ArtifactLayers.BOOTS_SMALL))));
        register(ModItems.STRIDER_SHOES.value(), () -> new BootArtifactRenderer("strider_shoes", hasArmor -> new LegsModel(bakeLayer(hasArmor ? ArtifactLayers.BOOTS_LARGE : ArtifactLayers.BOOTS_SMALL))));

        // curio
        register(ModItems.WHOOPEE_CUSHION.value(), () -> SimpleArtifactRenderer.create("whoopee_cushion", new HeadModel(bakeLayer(ArtifactLayers.WHOOPEE_CUSHION))));
    }

    public static ModelPart bakeLayer(ModelLayerLocation layerLocation) {
        return Minecraft.getInstance().getEntityModels().bakeLayer(layerLocation);
    }

    public static void register(Item item, Supplier<ArtifactRenderer> rendererFactory) {
        EquipmentRenderingManager.registerArtifactRenderer(item, rendererFactory);
    }
}
