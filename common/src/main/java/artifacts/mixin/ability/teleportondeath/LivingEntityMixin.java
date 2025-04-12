package artifacts.mixin.ability.teleportondeath;

import artifacts.component.ability.TeleportOnDeathAbility;
import artifacts.network.ChorusTotemUsedPacket;
import artifacts.network.NetworkHandler;
import artifacts.registry.ModDataComponents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public class LivingEntityMixin {

    @SuppressWarnings("UnreachableCode")
    @Inject(method = "checkTotemDeathProtection", at = @At("HEAD"), cancellable = true)
    private void checkTotemDeathProtection(DamageSource damageSource, CallbackInfoReturnable<Boolean> cir) {
        LivingEntity entity = (LivingEntity) (Object) this;
        ItemStack totem = TeleportOnDeathAbility.findTotem(entity);
        if (!totem.isEmpty()
                && entity.level() instanceof ServerLevel level
                && !damageSource.is(DamageTypeTags.BYPASSES_INVULNERABILITY)
        ) {
            if (totem.get(ModDataComponents.TELEPORT_ON_DEATH.get()) instanceof TeleportOnDeathAbility ability) {
                if (ability.teleportationChance().get() > entity.getRandom().nextDouble()) {
                    TeleportOnDeathAbility.teleport(entity, level);
                    if (ability.consumedOnUse().get()) {
                        totem.shrink(1);
                    } else if (entity instanceof Player player) {
                        player.getCooldowns().addCooldown(totem.getItem(), ability.cooldown().get() * 20);
                    }
                    entity.setHealth(Math.min(entity.getMaxHealth(), Math.max(1, ability.healthRestored().get())));
                    if (entity instanceof ServerPlayer player) {
                        entity.level().playSound(player, player.getX(), player.getY(), player.getZ(), SoundEvents.TOTEM_USE, SoundSource.PLAYERS, 1, 1);
                        NetworkHandler.sendToPlayer(player, new ChorusTotemUsedPacket());
                    }
                    cir.setReturnValue(true); // early return intended!
                }
            }
        }
    }
}
