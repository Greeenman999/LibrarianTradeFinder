package de.greenman999.librariantradefinder.screens;

import de.greenman999.librariantradefinder.LibrarianTradeFinder;
import de.greenman999.librariantradefinder.config.TradeFinderConfig;
import org.joml.Matrix3x2fStack;

import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
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
    public TradeFinderConfig.EnchantmentOption enchantmentOption;

    public EnchantmentEntry(Enchantment enchantment) {
        super();
        this.enchantment = enchantment;
        this.enabled = LibrarianTradeFinder.getConfig().enchantments.get(enchantment).isEnabled();
        this.enchantmentOption = LibrarianTradeFinder.getConfig().enchantments.get(enchantment);

        //maxPriceField = new TextFieldWidget(MinecraftClient.getInstance().textRenderer, 0, 0, 50, 20, Text.of("Max Price"));
        maxPriceField = new EditBox(Minecraft.getInstance().font, 0, 0, 20, 14, Component.translatable("tradefinderui.enchantments.price.name"));
        maxPriceField.setMaxLength(2);
        maxPriceField.setValue(String.valueOf(enchantmentOption.getMaxPrice()));
        //maxPriceField.setDrawsBackground(false);

        levelField = new EditBox(Minecraft.getInstance().font, 0, 0, 14, 14, Component.translatable("tradefinderui.enchantments.level.name"));
        levelField.setMaxLength(1);
        levelField.setValue(String.valueOf(enchantmentOption.getLevel()));

    }

    @Override
    public void renderContent(GuiGraphics context, int mouseX, int mouseY, boolean hovered, float deltaTicks) {
        Matrix3x2fStack matrices = context.pose();
        this.x = super.getX();
        this.y = super.getY();
        this.entryWidth = super.getWidth();
        this.entryHeight = super.getHeight();

        Font textRenderer = Minecraft.getInstance().font;
        Component enchantmentText = enchantment.description();

        // Per-enchant clamp: only fires on focus loss (incl. Tab to next field, Enter from our key handler,
        // or click elsewhere). Computes valid bounds for THIS enchant at the current level using the same
        // vanilla formula MiniHUD uses (with the DOUBLE_TRADE_PRICE tag handled).
        if (!levelField.getValue().isEmpty() && !levelField.canConsumeInput()) {
            int parsedLevel = Integer.parseInt(levelField.getValue());
            int clampedLevel = Math.max(1, Math.min(parsedLevel, enchantment.getMaxLevel()));
            if (clampedLevel != parsedLevel) {
                levelField.setValue(String.valueOf(clampedLevel));
            }
        }
        if (!maxPriceField.getValue().isEmpty() && !maxPriceField.canConsumeInput()) {
            int currentLevel = !levelField.getValue().isEmpty()
                    ? Integer.parseInt(levelField.getValue())
                    : enchantment.getMaxLevel();
            int min = TradeFinderConfig.computedMinPrice(enchantment, currentLevel);
            int max = TradeFinderConfig.computedMaxPrice(enchantment, currentLevel);
            int parsed = Integer.parseInt(maxPriceField.getValue());
            int clamped = Math.max(min, Math.min(parsed, max));
            if (clamped != parsed) {
                maxPriceField.setValue(String.valueOf(clamped));
            }
        }

        enchantmentOption.setEnabled(enabled);
        enchantmentOption.setMaxPrice(!maxPriceField.getValue().isEmpty() ? Integer.parseInt(maxPriceField.getValue()) : 64);
        enchantmentOption.setLevel(!levelField.getValue().isEmpty() ? Integer.parseInt(levelField.getValue()) : enchantment.getMaxLevel());

        if (y < 8) return;

        maxPriceField.setVisible(enabled);
        levelField.setVisible(enabled);

        int maxPriceX = x + entryWidth - 21;
        int levelX = maxPriceX - 15 - 14;

        if (enabled) {
            context.fill(x, y, x + entryWidth, y + entryHeight - 4, 0x3F00FF00);

            context.drawString(textRenderer, Component.nullToEmpty("$:"), maxPriceX - 10, y + 4, 0xFFFFFFFF);
            context.drawString(textRenderer, Component.nullToEmpty("LVL:"), levelX - 23, y + 4, 0xFFFFFFFF);
        } else {
            context.fill(x, y, x + entryWidth, y + entryHeight - 4, 0x1AC7C0C0);
        }

        context.drawString(textRenderer, enchantmentText, 8, y + 4, 0xFFFFFFFF);

        matrices.pushMatrix();
        matrices.translate(0, 0);
        maxPriceField.setX(maxPriceX);
        maxPriceField.setY(y + 1);
        maxPriceField.render(context, mouseX, mouseY, deltaTicks);

        levelField.setX(levelX);
        levelField.setY(y + 1);
        levelField.render(context, mouseX, mouseY, deltaTicks);
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
        if(mouseX > this.x && mouseX < this.x + this.entryWidth - i && mouseY > y && mouseY < y + entryHeight - 4) {
            enabled = !enabled;
            return true;
        } else if(mouseX > this.x + entryWidth - 21 - 10 - 4 && mouseX < this.x + this.entryWidth - 21 && mouseY > y && mouseY < y + entryHeight - 4 && enabled) {
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
        // Scroll-over-field adjusts maxPrice only. Level was deliberately removed: scrolling
        // the enchantment list passes the cursor through level fields for one frame each,
        // silently knocking levels down to 1 and corrupting search criteria. Click + type to
        // change level instead.
        if (maxPriceField.isMouseOver(mouseX, mouseY)){
            enchantmentOption.setMaxPrice(Mth.clamp((int) (enchantmentOption.getMaxPrice() + verticalAmount), 5, 64));
            maxPriceField.setValue(String.valueOf(enchantmentOption.getMaxPrice()));
            return true;
        }
        return false;
    }
}
