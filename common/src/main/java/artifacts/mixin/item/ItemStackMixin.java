package artifacts.mixin.item;

import artifacts.Artifacts;
import artifacts.client.ToggleKeyHandlers;
import artifacts.component.ability.EquipmentAbility;
import artifacts.item.WearableArtifactItem;
import artifacts.registry.ModDataComponents;
import artifacts.util.TooltipHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.function.Consumer;
import java.util.function.Supplier;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin {

    @Inject(method = "addDetailsToTooltip", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/Item;appendHoverText(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/item/Item$TooltipContext;Lnet/minecraft/world/item/component/TooltipDisplay;Ljava/util/function/Consumer;Lnet/minecraft/world/item/TooltipFlag;)V"))
    private void getTooltipLines(Item.TooltipContext context, TooltipDisplay display, @Nullable Player player, TooltipFlag tooltipFlag, Consumer<Component> tooltip, CallbackInfo ci) {
        if (!Artifacts.CONFIG.client.showTooltips.get()) {
            return;
        }

        ItemStack stack = (ItemStack) (Object) this;

        if (stack.getItem() instanceof WearableArtifactItem) {
            if (TooltipHelper.isCosmetic(stack)) {
                tooltip.accept(Component.translatable("%s.tooltip.cosmetic".formatted(Artifacts.MOD_ID))
                        .withStyle(ChatFormatting.ITALIC).withStyle(ChatFormatting.GRAY));
            }
        }

        TooltipHelper.getComponentIfVisible(ModDataComponents.ABILITY_LORE.get(), stack, display).ifPresent(lore ->
                lore.addToTooltip(context, tooltip, tooltipFlag, stack)
        );

        for (Supplier<? extends DataComponentType<? extends EquipmentAbility>> type : ModDataComponents.TOOLTIP_ORDER) {
            TooltipHelper.getAbilityIfVisible(type.get(), stack, display).ifPresent(ability ->
                    ability.addToTooltip(new EquipmentAbility.TooltipWriter(type.get(), tooltip, context, stack))
            );
        }

        TooltipHelper.getComponentIfVisible(ModDataComponents.TOGGLE_KEY.get(), stack, display).ifPresent(toggleKey -> {
            // TODO check if this works
            if (!TooltipHelper.isCosmetic(stack) && player != null && player.level().isClientSide()) {
                ToggleKeyHandlers.addTooltip(toggleKey, stack.has(ModDataComponents.DISABLED_BY_TOGGLE.get()), tooltip);
            }
        });
    }

    /*
     * On NeoForge the call to addAttributeTooltips is replaced with a call to AttributeUtil.addAttributeTooltips, this is only injected on Fabric.
     * An AddAttributeTooltipsEvent listener calls TooltipHelper.addAttributeTooltips on NeoForge
     */
    @Inject(method = "addDetailsToTooltip", require = 0, locals = LocalCapture.CAPTURE_FAILHARD, at = @At(value = "INVOKE", shift = At.Shift.AFTER, target = "Lnet/minecraft/world/item/ItemStack;addAttributeTooltips(Ljava/util/function/Consumer;Lnet/minecraft/world/item/component/TooltipDisplay;Lnet/minecraft/world/entity/player/Player;)V"))
    private void addAttributeTooltips(Item.TooltipContext context, TooltipDisplay display, @Nullable Player player, TooltipFlag tooltipFlag, Consumer<Component> tooltip, CallbackInfo ci) {
        TooltipHelper.addAttributeTooltips(tooltip, (ItemStack) (Object) this, context, display);
    }
}
