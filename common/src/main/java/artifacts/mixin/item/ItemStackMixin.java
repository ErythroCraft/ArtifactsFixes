package artifacts.mixin.item;

import artifacts.Artifacts;
import artifacts.ability.ArtifactAbility;
import artifacts.item.WearableArtifactItem;
import artifacts.util.AbilityHelper;
import artifacts.util.TooltipHelper;
import net.minecraft.ChatFormatting;
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
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

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
            if (AbilityHelper.isCosmetic(stack)) {
                tooltip.add(Component.translatable("%s.tooltip.cosmetic".formatted(Artifacts.MOD_ID)).withStyle(ChatFormatting.ITALIC));
            }
            tooltip.forEach(line -> tooltipList.add(line.withStyle(ChatFormatting.GRAY)));
        }

        List<MutableComponent> tooltip = new ArrayList<>();
        for (TypedDataComponent<?> component : stack.getComponents()) {
            if (component.value() instanceof ArtifactAbility ability) {
                ability.addTooltipIfNonCosmetic(tooltip);
            }
        }
        tooltip.forEach(line -> tooltipList.add(line.withStyle(ChatFormatting.GRAY)));
    }

    // this no longer gets called on NeoForge
    @Inject(method = "addAttributeTooltips", at = @At("TAIL"))
    private void addAttributeTooltips(Consumer<Component> consumer, @Nullable Player player, CallbackInfo info) {
        TooltipHelper.addAttributeTooltips(consumer, (ItemStack) (Object) this);
    }
}
