package artifacts.config.screen;

import me.shedaniel.clothconfig2.api.AbstractConfigListEntry;
import me.shedaniel.clothconfig2.gui.entries.SubCategoryListEntry;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public class ItemSubCategoryListEntry extends SubCategoryListEntry {

    private final ItemStack stack;

    @SuppressWarnings("deprecation")
    public ItemSubCategoryListEntry(Item item, List<AbstractConfigListEntry<?>> entries) {
        super(item.getDefaultInstance().getItemName(), List.copyOf(entries), false);
        this.stack = new ItemStack(item);
        List<String> searchTags = List.of(getFieldName().getString().split(" "));
        // noinspection unchecked
        getValue().forEach(value -> value.appendSearchTags(searchTags));
    }

    // https://github.com/shedaniel/cloth-config/issues/153
    @Override
    @SuppressWarnings("rawtypes")
    public boolean isRequiresRestart() {
        for (AbstractConfigListEntry entry : getValue()) {
            if (entry.isRequiresRestart() && entry.isEdited()) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void lateRender(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float delta) {
        super.lateRender(graphics, mouseX, mouseY, delta);

        graphics.item(stack, -4, 2);
        graphics.text(Minecraft.getInstance().font, this.getActualDisplayedFieldName().getVisualOrderText(), 16, 6, -1);
    }

    @Override
    public Component getDisplayedFieldName() {
        return CommonComponents.EMPTY;
    }

    public Component getActualDisplayedFieldName() {
        MutableComponent text = this.getFieldName().copy();
        boolean hasError = this.getConfigError().isPresent();
        boolean isEdited = this.isEdited();
        if (hasError) {
            text.withStyle(ChatFormatting.RED);
        }

        if (isEdited) {
            text.withStyle(ChatFormatting.ITALIC);
        }

        if (!hasError && !isEdited) {
            text.withStyle(ChatFormatting.GRAY);
        }

        if (!this.isEnabled()) {
            text.withStyle(ChatFormatting.DARK_GRAY);
        }

        return text;
    }
}
