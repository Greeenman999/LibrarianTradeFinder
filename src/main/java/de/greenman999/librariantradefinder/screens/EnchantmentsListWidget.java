package de.greenman999.librariantradefinder.screens;

import de.greenman999.librariantradefinder.LibrarianTradeFinder;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.EntryListWidget;
import net.minecraft.client.input.CharInput;
import net.minecraft.client.input.KeyInput;
import net.minecraft.client.util.InputUtil;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix3x2fStack;

public class EnchantmentsListWidget extends EntryListWidget<EnchantmentEntry> {

    public GrayButtonWidget resetButton;
    public int top;

    public EnchantmentsListWidget(MinecraftClient client, int width, int height, int top, int itemHeight) {
        super(client, width, height, top, itemHeight);
        this.top = top;

        for(Enchantment enchantment : LibrarianTradeFinder.getConfig().enchantments.keySet()) {
            this.addEntry(new EnchantmentEntry(enchantment));
        }

        this.resetButton = GrayButtonWidget.builder(Text.translatable("tradefinderui.reset"), (buttonWidget) -> {
            for(EnchantmentEntry enchantmentEntry : this.children()) {
                        enchantmentEntry.maxPriceField.setText("64");
                        enchantmentEntry.levelField.setText(String.valueOf(enchantmentEntry.enchantment.getMaxLevel()));
                        enchantmentEntry.enabled = false;
                    }
                })
                .color(0x5FC7C0C0)
                .dimensions(this.width - 45, 5, 50, 15)
                .tooltip(Tooltip.of(Text.translatable("tradefinderui.reset.tooltip")))
                .build();

    }

    @Override
    protected void drawSelectionHighlight(DrawContext context, EnchantmentEntry entry, int color) {
    }

    @Override
    public void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
        this.setSelected(null);
        Matrix3x2fStack matrices = context.getMatrices();
        matrices.pushMatrix();
        matrices.translate(0.0F, 0.0F);

        context.fill(5, 5, this.width + 5, 20, 0xAFC7C0C0);
        context.drawTextWithShadow(MinecraftClient.getInstance().textRenderer, Text.translatable("tradefinderui.enchantments.title"), 9, 9, 0xFFFFFFFF);

        matrices.popMatrix();

        super.renderWidget(context, mouseX, mouseY, delta);
    }

    @Override
    protected void drawMenuListBackground(DrawContext context) {

    }

    @Override
    protected void drawHeaderAndFooterSeparators(DrawContext context) {

    }

    @Override
    protected void appendClickableNarrations(NarrationMessageBuilder builder) {

    }

    @Override
    public int getRowWidth() {
        return this.width - 12;
    }

    @Override
    public int getRight() {
        return this.width + 7;
    }

    //@Override
    /*protected int getScrollbarPositionX() {
        return this.width - 10;
    }*/

    @Override
    protected int getScrollbarX() {
        return this.width;
    }

    @Override
    public int getRowTop(int index) {
        return this.getY() - (int)this.getScrollY() + index * this.itemHeight + this.headerHeight;
    }

    @Override
    public int getRowLeft() {
        return this.getX() + this.width / 2 - this.getRowWidth() / 2 - 1;
    }

    @Override
    public boolean mouseClicked(Click click, boolean doubled) {
        resetButton.mouseClicked(click, doubled);
        return super.mouseClicked(click, doubled);
    }

    @Override
    public boolean keyPressed(KeyInput input) {
        int keyCode = input.getKeycode();
        if(!(keyCode == InputUtil.GLFW_KEY_BACKSPACE || (keyCode >= InputUtil.GLFW_KEY_0 && keyCode <= InputUtil.GLFW_KEY_9) || keyCode == InputUtil.GLFW_KEY_LEFT || keyCode == InputUtil.GLFW_KEY_RIGHT)) return false;
        for(EnchantmentEntry enchantmentEntry : children()) {
            if(enchantmentEntry.maxPriceField.isFocused()) return enchantmentEntry.maxPriceField.keyPressed(keyCode, scanCode, modifiers);
            if(enchantmentEntry.levelField.isFocused()) return enchantmentEntry.levelField.keyPressed(keyCode, scanCode, modifiers);
        }
        return super.keyPressed(input);
    }

    @Override
    public boolean keyReleased(KeyInput input) {
        int keyCode = input.getKeycode();
        if(!(keyCode == InputUtil.GLFW_KEY_BACKSPACE || (keyCode >= InputUtil.GLFW_KEY_0 && keyCode <= InputUtil.GLFW_KEY_9) || keyCode == InputUtil.GLFW_KEY_LEFT || keyCode == InputUtil.GLFW_KEY_RIGHT)) return false;
        for (EnchantmentEntry enchantmentEntry : this.children()) {
            enchantmentEntry.maxPriceField.keyReleased(input);
            enchantmentEntry.levelField.keyReleased(input);
        }
        return super.keyReleased(input);
    }

    @Override
    public boolean charTyped(CharInput input) {
        if(!input.isValidChar()) return false;
        for (EnchantmentEntry enchantmentEntry : this.children()) {
            enchantmentEntry.maxPriceField.charTyped(input);
            enchantmentEntry.levelField.charTyped(input);
        }
        return super.charTyped(input);
    }

    @Nullable
    @Override
    protected EnchantmentEntry getEntryAtPosition(double x, double y) {
        int i = this.getRowWidth() / 2;
        int j = this.getX() + this.width / 2;
        int k = j - i;
        int l = j + i;
        int m = MathHelper.floor(y - (double)this.top) - this.headerHeight + (int)this.getScrollY();
        int n = m / this.itemHeight;
        return x < (double)this.getScrollbarX() && x >= (double)k && x <= (double)l && n >= 0 && m >= 0 && n < this.getEntryCount() ? this.children().get(n) : null;
    }
}