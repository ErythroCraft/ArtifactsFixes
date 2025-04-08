package artifacts.neoforge.event;

import artifacts.ability.UpgradeToolTierAbility;
import artifacts.component.AbilityToggles;
import artifacts.event.ArtifactHooks;
import artifacts.platform.PlatformServices;
import artifacts.registry.ModAbilities;
import artifacts.registry.ModTags;
import artifacts.util.AbilityHelper;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.living.LivingChangeTargetEvent;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.living.LivingEntityUseItemEvent;
import net.neoforged.neoforge.event.entity.living.LivingExperienceDropEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.level.BlockDropsEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import org.apache.commons.lang3.mutable.MutableInt;

import java.util.List;

public class ArtifactEventsNeoForge {

    public static void register() {
        NeoForge.EVENT_BUS.addListener(EventPriority.LOW, ArtifactEventsNeoForge::onLivingDamage);
        NeoForge.EVENT_BUS.addListener(ArtifactEventsNeoForge::onLivingUpdate);
        NeoForge.EVENT_BUS.addListener(ArtifactEventsNeoForge::onDrinkingHatItemUse);
        NeoForge.EVENT_BUS.addListener(ArtifactEventsNeoForge::onGoldenHookExperienceDrop);
        NeoForge.EVENT_BUS.addListener(ArtifactEventsNeoForge::onKittySlippersChangeTarget);
        NeoForge.EVENT_BUS.addListener(ArtifactEventsNeoForge::onDiggingClawsHarvestCheck);
        NeoForge.EVENT_BUS.addListener(ArtifactEventsNeoForge::onPlayerTick);
        NeoForge.EVENT_BUS.addListener(ArtifactEventsNeoForge::onBlockDrops);
    }

    private static void onPlayerTick(PlayerTickEvent.Post event) {
        AbilityToggles abilityToggles = PlatformServices.platformHelper.getAbilityToggles(event.getEntity());
        if (event.getEntity() instanceof ServerPlayer serverPlayer && abilityToggles != null && serverPlayer.tickCount % 40 == 0) {
            abilityToggles.sendToClient(serverPlayer);
        }
    }

    private static void onLivingDamage(LivingDamageEvent.Post event) {
        ArtifactHooks.onLivingDamaged(event.getEntity(), event.getSource(), event.getNewDamage());
    }

    private static void onLivingUpdate(EntityTickEvent.Pre event) {
        if (event.getEntity() instanceof LivingEntity entity) {
            onKittySlippersLivingUpdate(entity);
            ArtifactHooks.livingUpdate(entity);
        }
    }

    private static void onDrinkingHatItemUse(LivingEntityUseItemEvent.Start event) {
        event.setDuration(ArtifactHooks.modifyUseDuration(event.getDuration(), event.getItem(), event.getEntity()));
    }

    private static void onGoldenHookExperienceDrop(LivingExperienceDropEvent event) {
        int droppedXp = event.getDroppedExperience();
        int modifiedXp = ArtifactHooks.modifyExperience(droppedXp, event.getEntity(), event.getAttackingPlayer());
        event.setDroppedExperience(modifiedXp);
    }

    private static void onKittySlippersChangeTarget(LivingChangeTargetEvent event) {
        LivingEntity target = event.getNewAboutToBeSetTarget();
        if (event.getEntity().getType().is(ModTags.CREEPERS)
                && AbilityHelper.hasAbilityActive(ModAbilities.SCARE_CREEPERS.value(), target)
        ) {
            event.setCanceled(true);
        }
    }

    private static void onKittySlippersLivingUpdate(LivingEntity entity) {
        if (entity.getLastHurtByMob() != null
                && AbilityHelper.hasAbilityActive(ModAbilities.SCARE_CREEPERS.value(), entity.getLastHurtByMob())
                && entity.getType().is(ModTags.CREEPERS)
        ) {
            entity.setLastHurtByMob(null);
        }
    }

    private static void onDiggingClawsHarvestCheck(PlayerEvent.HarvestCheck event) {
        event.setCanHarvest(event.canHarvest() || UpgradeToolTierAbility.canHarvestWithTier(event.getEntity(), event.getTargetBlock()));
    }

    private static void onBlockDrops(BlockDropsEvent event) {
        List<ItemEntity> drops = event.getDrops();
        MutableInt experience = new MutableInt(0);
        drops.forEach(itemEntity -> itemEntity.setItem(ArtifactHooks.applySmeltOresAbility(
                itemEntity.getItem(),
                event.getBreaker(),
                event.getState(),
                experience::add
        )));
        event.setDroppedExperience(event.getDroppedExperience() + experience.getValue());
    }
}
