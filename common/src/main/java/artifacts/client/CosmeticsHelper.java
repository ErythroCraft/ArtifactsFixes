package artifacts.client;

import artifacts.Artifacts;
import artifacts.item.ArtifactItem;
import artifacts.registry.ModDataComponents;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public class CosmeticsHelper {

    public static boolean areCosmeticsToggledOffByPlayer(ItemStack stack) {
        Boolean enabled = stack.get(ModDataComponents.COSMETICS_ENABLED.get());
        return enabled != null && !enabled && !isCosmeticOnly(stack);
    }

    public static void toggleCosmetics(ItemStack stack) {
        if (!isCosmeticOnly(stack)) {
            stack.set(ModDataComponents.COSMETICS_ENABLED.get(), areCosmeticsToggledOffByPlayer(stack));
        }
    }

    private static boolean isCosmeticOnly(ItemStack stack) {
        return stack.getItem() instanceof ArtifactItem item && item.isCosmetic();
    }

    public static void addCosmeticToggleTooltip(List<MutableComponent> tooltip, ItemStack stack) {
        if (CosmeticsHelper.areCosmeticsToggledOffByPlayer(stack)) {
            tooltip.add(
                    Component.translatable("%s.tooltip.cosmetics_disabled".formatted(Artifacts.MOD_ID))
                            .withStyle(ChatFormatting.ITALIC)
            );
        } else {
            tooltip.add(
                    Component.translatable("%s.tooltip.cosmetics_enabled".formatted(Artifacts.MOD_ID))
                            .withStyle(ChatFormatting.ITALIC)
            );
        }
    }
}
