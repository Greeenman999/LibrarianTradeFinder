package de.greenman999.librariantradefinder.screens;

import de.greenman999.librariantradefinder.LibrarianTradeFinder;
import de.greenman999.librariantradefinder.config.TradeFinderConfig;
import org.joml.Matrix3x2fStack;

import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractSelectionList;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.item.enchantment.Enchantment;

public class EnchantmentEntry extends AbstractSelectionList.Entry<EnchantmentEntry> {

    public final Enchantment enchantment;
    public final EditBox maxPriceField;
    public final EditBox levelField;
    public int x;
    public int y;
    public int entryWidth;
    public int entryHeight;

    public boolean enabled;
    public final TradeFinderConfig.EnchantmentOption enchantmentOption;
    private final Component enchantmentText;

    public EnchantmentEntry(Enchantment enchantment) {
        super();
        this.enchantment = enchantment;
        this.enchantmentOption = LibrarianTradeFinder.getConfig().enchantments.get(enchantment);
        this.enabled = this.enchantmentOption.isEnabled();
        this.enchantmentText = enchantment.description();

        maxPriceField = new EditBox(Minecraft.getInstance().font, 0, 0, 20, 14, Component.translatable("tradefinderui.enchantments.price.name"));
        maxPriceField.setMaxLength(2);
        maxPriceField.setValue(String.valueOf(enchantmentOption.getMaxPrice()));

        levelField = new EditBox(Minecraft.getInstance().font, 0, 0, 14, 14, Component.translatable("tradefinderui.enchantments.level.name"));
        levelField.setMaxLength(1);
        levelField.setValue(String.valueOf(enchantmentOption.getLevel()));

    }

    @Override
    public void extractContent(GuiGraphicsExtractor context, int mouseX, int mouseY, boolean hovered, float deltaTicks) {
        Matrix3x2fStack matrices = context.pose();
        int x = this.getX();
        int y = this.getY();
        this.x = x;
        this.y = y;
        this.entryWidth = this.getWidth();
        this.entryHeight = this.getHeight();

        Font textRenderer = Minecraft.getInstance().font;

        String maxPriceValue = maxPriceField.getValue();
        String levelValue = levelField.getValue();
        boolean editingMaxPrice = maxPriceField.canConsumeInput();
        boolean editingLevel = levelField.canConsumeInput();

        int maxPrice = 64;
        int level = this.enchantment.getMaxLevel();
        if (!maxPriceValue.isEmpty() && maxPriceValue.matches("[0-9]+")) {
            maxPrice = Integer.parseInt(maxPriceValue);
            if (!editingMaxPrice && (maxPrice > 64 || maxPrice < 5)) {
                maxPriceField.setValue("64");
                maxPrice = 64;
            }
        }
        if (!levelValue.isEmpty() && levelValue.matches("[0-9]+")) {
            level = Integer.parseInt(levelValue);
            if (!editingLevel && (level > this.enchantment.getMaxLevel() || level < 1)) {
                levelField.setValue(String.valueOf(this.enchantment.getMaxLevel()));
                level = this.enchantment.getMaxLevel();
            }
        }

        enchantmentOption.setEnabled(enabled);
        enchantmentOption.setMaxPrice(maxPrice);
        enchantmentOption.setLevel(level);

        if (y < 8) return;

        maxPriceField.setVisible(enabled);
        levelField.setVisible(enabled);

        int maxPriceX = x + entryWidth - 21;
        int levelX = maxPriceX - 15 - 14;

        if (enabled) {
            context.fill(x, y, x + entryWidth, y + entryHeight - 4, 0x3F00FF00);

            context.text(textRenderer, Component.nullToEmpty("$:"), maxPriceX - 10, y + 4, 0xFFFFFFFF);
            context.text(textRenderer, Component.nullToEmpty("LVL:"), levelX - 23, y + 4, 0xFFFFFFFF);
        } else {
            context.fill(x, y, x + entryWidth, y + entryHeight - 4, 0x1AC7C0C0);
        }

        context.text(textRenderer, enchantmentText, 8, y + 4, 0xFFFFFFFF);

        matrices.pushMatrix();
        matrices.translate(0, 0);
        maxPriceField.setX(maxPriceX);
        maxPriceField.setY(y + 1);
        maxPriceField.extractRenderState(context, mouseX, mouseY, deltaTicks);

        levelField.setX(levelX);
        levelField.setY(y + 1);
        levelField.extractRenderState(context, mouseX, mouseY, deltaTicks);
        matrices.popMatrix();

        if (maxPriceField.canConsumeInput()) {
            context.setComponentTooltipForNextFrame(Minecraft.getInstance().font, List.of(Component.translatable("tradefinderui.enchantments.price.tooltip.1").withStyle(ChatFormatting.GRAY),
                    Component.translatable("tradefinderui.enchantments.price.tooltip.2").withStyle(ChatFormatting.GRAY),
                    Component.translatable("tradefinderui.enchantments.price.tooltip.3").withStyle(ChatFormatting.GRAY),
                    Component.translatable("tradefinderui.enchantments.price.tooltip.4").withStyle(ChatFormatting.GRAY),
                    Component.translatable("tradefinderui.enchantments.price.tooltip.5").withStyle(ChatFormatting.GRAY)), maxPriceField.getX() - 8, y + 32);
        }

        if(mouseX > x + entryWidth - 21 - 10 - 2 && mouseX < x + entryWidth - 21 - 2 && mouseY > y && mouseY < y + entryHeight && enabled && !maxPriceField.canConsumeInput()) {
            context.setComponentTooltipForNextFrame(Minecraft.getInstance().font, List.of(Component.translatable("tradefinderui.enchantments.price.tooltip.title").withStyle(ChatFormatting.GREEN),
                    Component.empty(),
                    Component.translatable("tradefinderui.enchantments.price.tooltip.1").withStyle(ChatFormatting.GRAY),
                    Component.translatable("tradefinderui.enchantments.price.tooltip.2").withStyle(ChatFormatting.GRAY),
                    Component.translatable("tradefinderui.enchantments.price.tooltip.3").withStyle(ChatFormatting.GRAY),
                    Component.translatable("tradefinderui.enchantments.price.tooltip.4").withStyle(ChatFormatting.GRAY),
                    Component.translatable("tradefinderui.enchantments.price.tooltip.5").withStyle(ChatFormatting.GRAY)), mouseX, y + 32);
        }
        if(mouseX > x + entryWidth - 21 - 15 - 14 - 23 - 2 && mouseX < x + entryWidth - 21 - 15 - 14 - 2 && mouseY > y && mouseY < y + entryHeight && enabled && !maxPriceField.canConsumeInput()) {
            context.setComponentTooltipForNextFrame(Minecraft.getInstance().font, List.of(Component.translatable("tradefinderui.enchantments.level.tooltip").withStyle(ChatFormatting.GREEN)), mouseX, y + 32);
        }
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent click, boolean doubled) {
        double mouseX = click.x();
        double mouseY = click.y();

        int i = 0;
        if(enabled) {
            i = 21 + 15 + 14;
        }
        if(mouseX > this.x && mouseX < this.x + this.entryWidth - i
                && mouseY > this.y && mouseY < this.y + entryHeight - 4) {
            enabled = !enabled;
            return true;
        } else if(mouseX > this.x + entryWidth - 21 - 10 - 4 && mouseX < this.x + this.entryWidth - 21
                && mouseY > this.y && mouseY < this.y + entryHeight - 4 && enabled) {
            enabled = false;
            return true;
        }
        return false;
    }

    @Override
    public void mouseMoved(double mouseX, double mouseY) {
        maxPriceField.mouseMoved(mouseX, mouseY);
        levelField.mouseMoved(mouseX, mouseY);
    }

    @Override
    public boolean mouseReleased(MouseButtonEvent click) {
        boolean maxPriceFieldReturn = maxPriceField.mouseReleased(click);
        boolean levelFieldReturn = levelField.mouseReleased(click);
        return maxPriceFieldReturn || levelFieldReturn;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        // Adjust options by hovering the mouse over the text field and scrolling
        // 'amount' is +1.0 or -1.0, sometimes +2.0 or +3.0 for mouse wheels that (physically) snap to positions.
        // There are also mouse wheels that scroll smoothly; the current implementation maybe doesn't work properly with them
        if (maxPriceField.isMouseOver(mouseX, mouseY)){
            enchantmentOption.setMaxPrice(Mth.clamp((int) (enchantmentOption.getMaxPrice() + verticalAmount), 5, 64));
            maxPriceField.setValue(String.valueOf(enchantmentOption.getMaxPrice()));
            return true;
        }
        else if (levelField.isMouseOver(mouseX, mouseY)){
            enchantmentOption.setLevel(Mth.clamp((int) (enchantmentOption.getLevel() + verticalAmount), 1, enchantment.getMaxLevel()));
            levelField.setValue(String.valueOf(enchantmentOption.getLevel()));
            return true;
        }
        return false;
    }
}
