package artifacts.config.screen;

import me.shedaniel.clothconfig2.api.AbstractConfigListEntry;
import me.shedaniel.clothconfig2.gui.entries.SubCategoryListEntry;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
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
        super(item.getDescription(), List.copyOf(entries), false);
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
    public void render(GuiGraphics graphics, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean isHovered, float delta) {
        super.render(graphics, index, y, x, entryWidth, entryHeight, mouseX, mouseY, isHovered, delta);

        graphics.renderItem(stack, x - 4, y + 2);
        graphics.drawString(Minecraft.getInstance().font, this.getActualDisplayedFieldName().getVisualOrderText(), x + 16, y + 6, -1);
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
            text = text.withStyle(ChatFormatting.RED);
        }

        if (isEdited) {
            text = text.withStyle(ChatFormatting.ITALIC);
        }

        if (!hasError && !isEdited) {
            text = text.withStyle(ChatFormatting.GRAY);
        }

        if (!this.isEnabled()) {
            text = text.withStyle(ChatFormatting.DARK_GRAY);
        }

        return text;
    }
}
