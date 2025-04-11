package artifacts.mixin.item;

import artifacts.Artifacts;
import artifacts.ability.EquipmentAbility;
import artifacts.client.ToggleKeyHandlers;
import artifacts.component.ToggleIdentifier;
import artifacts.item.WearableArtifactItem;
import artifacts.registry.ModDataComponents;
import artifacts.util.TooltipHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.client.KeyMapping;
import net.minecraft.core.component.TypedDataComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.ArrayList;
import java.util.List;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin {

    @Inject(method = "getTooltipLines", locals = LocalCapture.CAPTURE_FAILHARD, at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/Item;appendHoverText(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/item/Item$TooltipContext;Ljava/util/List;Lnet/minecraft/world/item/TooltipFlag;)V"))
    private void getTooltipLines(Item.TooltipContext tooltipContext, @Nullable Player player, TooltipFlag tooltipFlag, CallbackInfoReturnable<List<Component>> cir, List<Component> tooltipList) {
        if (!Artifacts.CONFIG.client.showTooltips.get()) {
            return;
        }

        // noinspection ConstantConditions
        ItemStack stack = (ItemStack) (Object) this;

        if (stack.getItem() instanceof WearableArtifactItem) {
            List<MutableComponent> tooltip = new ArrayList<>();
            if (TooltipHelper.isCosmetic(stack)) {
                tooltip.add(Component.translatable("%s.tooltip.cosmetic".formatted(Artifacts.MOD_ID)).withStyle(ChatFormatting.ITALIC));
            }
            tooltip.forEach(line -> tooltipList.add(line.withStyle(ChatFormatting.GRAY)));
        }

        List<MutableComponent> tooltip = new ArrayList<>();
        for (TypedDataComponent<?> component : stack.getComponents()) {
            if (component.value() instanceof EquipmentAbility ability) {
                ability.addTooltipIfNonCosmetic(tooltip);
            }
        }
        ToggleIdentifier toggleKey = stack.get(ModDataComponents.TOGGLE_KEY.get());
        if (toggleKey != null) {
            KeyMapping key = ToggleKeyHandlers.getKeyMapping(toggleKey);
            if (key != null && player != null && (!key.isUnbound() || stack.has(ModDataComponents.DISABLED_BY_TOGGLE.get()))) {
                tooltip.add(Component.translatable("%s.tooltip.toggle_keymapping".formatted(Artifacts.MOD_ID), key.getTranslatedKeyMessage()));
            }
        }
        tooltip.forEach(line -> tooltipList.add(line.withStyle(ChatFormatting.GRAY)));
    }

    /*
     * On NeoForge the call to addAttributeTooltips is replaced with a call to AttributeUtil.addAttributeTooltips, this is only injected on Fabric.
     * An AddAttributeTooltipsEvent listener calls TooltipHelper.addAttributeTooltips on NeoForge
     */
    @Inject(method = "getTooltipLines", require = 0, locals = LocalCapture.CAPTURE_FAILHARD, at = @At(value = "INVOKE", shift = At.Shift.AFTER, target = "Lnet/minecraft/world/item/ItemStack;addAttributeTooltips(Ljava/util/function/Consumer;Lnet/minecraft/world/entity/player/Player;)V"))
    private void addAttributeTooltips(Item.TooltipContext context, Player player, TooltipFlag tooltipFlag, CallbackInfoReturnable<List<Component>> cir, List<Component> tooltip) {
        TooltipHelper.addAttributeTooltips(tooltip::add, (ItemStack) (Object) this, context);
    }
}
