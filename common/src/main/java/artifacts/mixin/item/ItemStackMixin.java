package artifacts.mixin.item;

import artifacts.component.ability.SimpleAbility;
import artifacts.config.value.Value;
import artifacts.registry.ModDataComponents;
import artifacts.util.ItemDamageUtil;
import artifacts.util.TooltipHelper;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Consumer;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin {

    @ModifyReturnValue(method = "processDurabilityChange", at = @At(value = "RETURN"))
    private int processDurabilityChange(int original) {
        return ItemDamageUtil.processDurabilityChange((ItemStack) (Object) this, original);
    }

    @Inject(method = "addDetailsToTooltip", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/Item;appendHoverText(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/item/Item$TooltipContext;Lnet/minecraft/world/item/component/TooltipDisplay;Ljava/util/function/Consumer;Lnet/minecraft/world/item/TooltipFlag;)V"))
    private void getTooltipLines(Item.TooltipContext context, TooltipDisplay display, @Nullable Player player, TooltipFlag tooltipFlag, Consumer<Component> builder, CallbackInfo ci) {
        ItemStack stack = (ItemStack) (Object) this;
        TooltipHelper.addAbilityDescriptions(builder, stack, context, display, tooltipFlag, player);
    }

    /*
     * On NeoForge the call to addAttributeTooltips is replaced with a call to AttributeUtil.addAttributeTooltips, this is only injected on Fabric.
     * An AddAttributeTooltipsEvent listener calls TooltipHelper.addAttributeTooltips on NeoForge
     */
    @Inject(method = "addDetailsToTooltip", require = 0, at = @At(value = "INVOKE", shift = At.Shift.AFTER, target = "Lnet/minecraft/world/item/ItemStack;addAttributeTooltips(Ljava/util/function/Consumer;Lnet/minecraft/world/item/component/TooltipDisplay;Lnet/minecraft/world/entity/player/Player;)V"))
    private void addAttributeTooltips(Item.TooltipContext context, TooltipDisplay display, @Nullable Player player, TooltipFlag tooltipFlag, Consumer<Component> builder, CallbackInfo ci) {
        TooltipHelper.addAttributeTooltips(builder, (ItemStack) (Object) this, context, display);
    }

    @Inject(method = "setDamageValue", at = @At(value = "RETURN"))
    private void onItemDamageUpdated(int value, CallbackInfo ci) {
        ItemStack stack = (ItemStack) (Object) this;
        if (ItemDamageUtil.isIndestructible(stack)
                && stack.get(ModDataComponents.INFINITE_CONSUMABLE.get()) instanceof SimpleAbility(Value<Boolean> enabled)
                && enabled.get()
        ) {
            if (stack.nextDamageWillBreak()) {
                artifacts$moveComponent(stack, DataComponents.CONSUMABLE, ModDataComponents.DISABLED_CONSUMABLE.get());
            } else {
                artifacts$moveComponent(stack, ModDataComponents.DISABLED_CONSUMABLE.get(), DataComponents.CONSUMABLE);
            }
        }
    }

    @Unique
    private <T> void artifacts$moveComponent(ItemStack stack, DataComponentType<T> source, DataComponentType<T> receiver) {
        T component = stack.remove(source);
        if (component != null) {
            stack.set(receiver, component);
        }
    }
}
