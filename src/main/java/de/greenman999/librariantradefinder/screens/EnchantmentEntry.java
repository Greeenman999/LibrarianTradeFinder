package de.greenman999.librariantradefinder.screens;

import de.greenman999.librariantradefinder.LibrarianTradeFinder;
import de.greenman999.librariantradefinder.config.TradeFinderConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.MultilineText;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.tooltip.TooltipBackgroundRenderer;
import net.minecraft.client.gui.widget.EntryListWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.MathHelper;
import org.joml.Matrix3x2fStack;

import java.util.List;

public class EnchantmentEntry extends EntryListWidget.Entry<EnchantmentEntry> {

    public final Enchantment enchantment;
    public final TextFieldWidget maxPriceField;
    public final TextFieldWidget levelField;
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
        maxPriceField = new TextFieldWidget(MinecraftClient.getInstance().textRenderer, 0, 0, 20, 14, Text.translatable("tradefinderui.enchantments.price.name"));
        maxPriceField.setMaxLength(2);
        maxPriceField.setText(String.valueOf(enchantmentOption.getMaxPrice()));
        //maxPriceField.setDrawsBackground(false);

        levelField = new TextFieldWidget(MinecraftClient.getInstance().textRenderer, 0, 0, 14, 14, Text.translatable("tradefinderui.enchantments.level.name"));
        levelField.setMaxLength(1);
        levelField.setText(String.valueOf(enchantmentOption.getLevel()));

    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, boolean hovered, float deltaTicks) {
        Matrix3x2fStack matrices = context.getMatrices();
        this.x = super.getX();
        this.y = super.getY();
        this.entryWidth = super.getWidth();
        this.entryHeight = super.getHeight();

        TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;
        Text enchantmentText = enchantment.description();

        if (!maxPriceField.getText().isEmpty() && !maxPriceField.isActive() &&
                (Integer.parseInt(maxPriceField.getText()) > 64 || Integer.parseInt(maxPriceField.getText()) < 5)) {
            maxPriceField.setText("64");
        }
        if (!levelField.getText().isEmpty() && !levelField.isActive() &&
                (Integer.parseInt(levelField.getText()) > enchantment.getMaxLevel() || Integer.parseInt(levelField.getText()) < 1)) {
            levelField.setText(String.valueOf(enchantment.getMaxLevel()));
        }

        enchantmentOption.setEnabled(enabled);
        enchantmentOption.setMaxPrice(!maxPriceField.getText().isEmpty() ? Integer.parseInt(maxPriceField.getText()) : 64);
        enchantmentOption.setLevel(!levelField.getText().isEmpty() ? Integer.parseInt(levelField.getText()) : enchantment.getMaxLevel());

        if (y < 8) return;

        maxPriceField.setVisible(enabled);
        levelField.setVisible(enabled);

        int maxPriceX = x + entryWidth - 21;
        int levelX = maxPriceX - 15 - 14;

        if (enabled) {
            context.fill(x, y, x + entryWidth, y + entryHeight - 4, 0x3F00FF00);

            context.drawTextWithShadow(textRenderer, Text.of("$:"), maxPriceX - 10, y + 4, 0xFFFFFFFF);
            context.drawTextWithShadow(textRenderer, Text.of("LVL:"), levelX - 23, y + 4, 0xFFFFFFFF);
        } else {
            context.fill(x, y, x + entryWidth, y + entryHeight - 4, 0x1AC7C0C0);
        }

        context.drawTextWithShadow(textRenderer, enchantmentText, 8, y + 4, 0xFFFFFFFF);

        matrices.pushMatrix();
        matrices.translate(0, 0);
        maxPriceField.setX(maxPriceX);
        maxPriceField.setY(y + 1);
        maxPriceField.render(context, mouseX, mouseY, deltaTicks);

        levelField.setX(levelX);
        levelField.setY(y + 1);
        levelField.render(context, mouseX, mouseY, deltaTicks);
        matrices.popMatrix();

        if (maxPriceField.isActive()) {
            context.drawTooltip(MinecraftClient.getInstance().textRenderer, List.of(Text.translatable("tradefinderui.enchantments.price.tooltip.1").formatted(Formatting.GRAY),
                    Text.translatable("tradefinderui.enchantments.price.tooltip.2").formatted(Formatting.GRAY),
                    Text.translatable("tradefinderui.enchantments.price.tooltip.3").formatted(Formatting.GRAY),
                    Text.translatable("tradefinderui.enchantments.price.tooltip.4").formatted(Formatting.GRAY),
                    Text.translatable("tradefinderui.enchantments.price.tooltip.5").formatted(Formatting.GRAY)), maxPriceField.getX() - 8, y + 32);
        }

        if(mouseX > x + entryWidth - 21 - 10 - 2 && mouseX < x + entryWidth - 21 - 2 && mouseY > y && mouseY < y + entryHeight && enabled && !maxPriceField.isActive()) {
            context.drawTooltip(MinecraftClient.getInstance().textRenderer, List.of(Text.translatable("tradefinderui.enchantments.price.tooltip.title").formatted(Formatting.GREEN),
                    Text.empty(),
                    Text.translatable("tradefinderui.enchantments.price.tooltip.1").formatted(Formatting.GRAY),
                    Text.translatable("tradefinderui.enchantments.price.tooltip.2").formatted(Formatting.GRAY),
                    Text.translatable("tradefinderui.enchantments.price.tooltip.3").formatted(Formatting.GRAY),
                    Text.translatable("tradefinderui.enchantments.price.tooltip.4").formatted(Formatting.GRAY),
                    Text.translatable("tradefinderui.enchantments.price.tooltip.5").formatted(Formatting.GRAY)), mouseX, y + 32);
        }
        if(mouseX > x + entryWidth - 21 - 15 - 14 - 23 - 2 && mouseX < x + entryWidth - 21 - 15 - 14 - 2 && mouseY > y && mouseY < y + entryHeight && enabled && !maxPriceField.isActive()) {
            context.drawTooltip(MinecraftClient.getInstance().textRenderer, List.of(Text.translatable("tradefinderui.enchantments.level.tooltip").formatted(Formatting.GREEN)), mouseX, y + 32);
        }
    }

    @Override
    public boolean mouseClicked(Click click, boolean doubled) {
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
    public boolean mouseReleased(Click click) {
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
            enchantmentOption.setMaxPrice(MathHelper.clamp((int) (enchantmentOption.getMaxPrice() + verticalAmount), 5, 64));
            maxPriceField.setText(String.valueOf(enchantmentOption.getMaxPrice()));
            return true;
        }
        else if (levelField.isMouseOver(mouseX, mouseY)){
            enchantmentOption.setLevel(MathHelper.clamp((int) (enchantmentOption.getLevel() + verticalAmount), 1, enchantment.getMaxLevel()));
            levelField.setText(String.valueOf(enchantmentOption.getLevel()));
            return true;
        }
        return false;
    }

    public static void renderMultilineTooltip(DrawContext context, TextRenderer textRenderer, MultilineText text, int centerX, int yAbove, int yBelow, int screenHeight) {
        Matrix3x2fStack matrices = context.getMatrices();
        if (text.getLineCount() > 0) {
            int maxWidth = text.getMaxWidth();
            int lineHeight = textRenderer.fontHeight + 1;
            int height = text.getLineCount() * lineHeight - 1;

            int belowY = yBelow + 12;
            int aboveY = yAbove - height + 12;
            int maxBelow = screenHeight - (belowY + height);
            int minAbove = aboveY - height;
            int y = belowY;
            if (maxBelow < -8)
                y = maxBelow > minAbove ? belowY : aboveY;

            int x = Math.max(centerX - text.getMaxWidth() / 2 - 12, -6);

            int drawX = x + 12;
            int drawY = y - 12;

            matrices.pushMatrix();
            TooltipBackgroundRenderer.render(
                    context,
                    drawX,
                    drawY,
                    maxWidth,
                    height,
                    null
            );
            matrices.translate(0.0F, 0.0F);
            //TODO: idk what the boolean bl is for, i just put true
            text.draw(context, MultilineText.Alignment.LEFT, drawX, drawY, lineHeight, false,-1);

            matrices.popMatrix();
        }
    }
}
