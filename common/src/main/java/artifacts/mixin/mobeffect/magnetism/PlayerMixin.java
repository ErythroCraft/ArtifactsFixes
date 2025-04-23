package artifacts.mixin.mobeffect.magnetism;

import artifacts.extensions.mobeffect.magnetism.ItemEntityExtensions;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Player.class)
public class PlayerMixin {

    @Inject(method = "drop(Lnet/minecraft/world/item/ItemStack;ZZ)Lnet/minecraft/world/entity/item/ItemEntity;", at = @At("RETURN"))
    private void setItemThrower(ItemStack stack, boolean bl, boolean setThrower, CallbackInfoReturnable<ItemEntity> cir) {
        if (setThrower && cir.getReturnValue() != null) {
            ((ItemEntityExtensions) cir.getReturnValue()).artifacts$setThrower((Player) (Object) this);
        }
    }
}
