package de.greenman999.screens;

import com.mojang.blaze3d.systems.RenderSystem;
import de.greenman999.LibrarianTradeFinder;
import de.greenman999.config.TradeFinderConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.MultilineText;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.EntryListWidget;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.Nullable;

public class EnchantmentsListWidget extends EntryListWidget<EnchantmentEntry> {

    public GrayButtonWidget resetButton;
    public int top;

    public EnchantmentsListWidget(MinecraftClient client, int width, int height, int top, int itemHeight) {
        super(client, width, height, top, itemHeight);
        this.top = top;

        for(TradeFinderConfig.EnchantmentOption options : LibrarianTradeFinder.getConfig().enchantments.values()) {
            this.addEntry(new EnchantmentEntry(options.enchantment));
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
    protected void drawSelectionHighlight(DrawContext context, int y, int entryWidth, int entryHeight, int borderColor, int fillColor) {
    }

    @Override
    public void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
        this.setSelected(null);
        MatrixStack matrices = context.getMatrices();
        matrices.push();
        RenderSystem.enableDepthTest();
        matrices.translate(0, 0, 100);

        context.fill(5, 5, this.width + 5, 20, 0xAFC7C0C0);
        context.drawTextWithShadow(MinecraftClient.getInstance().textRenderer, Text.translatable("tradefinderui.enchantments.title"), 9, 9, 0xFFFFFF);
        resetButton.render(context, mouseX, mouseY, delta);

        RenderSystem.disableDepthTest();
        matrices.pop();

        super.renderWidget(context, mouseX, mouseY, delta);

        for(EnchantmentEntry enchantmentEntry : this.children()) {
            if(enchantmentEntry.maxPriceField.isActive()) {
                EnchantmentEntry.renderMultilineTooltip(context, MinecraftClient.getInstance().textRenderer, MultilineText.create(MinecraftClient.getInstance().textRenderer,
                        Text.translatable("tradefinderui.enchantments.price.tooltip.1").formatted(Formatting.GRAY),
                        Text.translatable("tradefinderui.enchantments.price.tooltip.2").formatted(Formatting.GRAY),
                        Text.translatable("tradefinderui.enchantments.price.tooltip.3").formatted(Formatting.GRAY),
                        Text.translatable("tradefinderui.enchantments.price.tooltip.4").formatted(Formatting.GRAY),
                        Text.translatable("tradefinderui.enchantments.price.tooltip.5").formatted(Formatting.GRAY)

                ), enchantmentEntry.maxPriceField.getX() + 75, enchantmentEntry.y - 5, enchantmentEntry.y + 20, this.height, 580);
            }

            if(mouseX > enchantmentEntry.x + enchantmentEntry.entryWidth - 21 - 10 - 2 && mouseX < enchantmentEntry.x + enchantmentEntry.entryWidth - 21 - 2 && mouseY > enchantmentEntry.y && mouseY < enchantmentEntry.y + enchantmentEntry.entryHeight && enchantmentEntry.enabled && !enchantmentEntry.maxPriceField.isActive()) {
                EnchantmentEntry.renderMultilineTooltip(context, MinecraftClient.getInstance().textRenderer, MultilineText.create(MinecraftClient.getInstance().textRenderer,
                        Text.translatable("tradefinderui.enchantments.price.tooltip.title").formatted(Formatting.GREEN),
                        Text.empty(),
                        Text.translatable("tradefinderui.enchantments.price.tooltip.1").formatted(Formatting.GRAY),
                        Text.translatable("tradefinderui.enchantments.price.tooltip.2").formatted(Formatting.GRAY),
                        Text.translatable("tradefinderui.enchantments.price.tooltip.3").formatted(Formatting.GRAY),
                        Text.translatable("tradefinderui.enchantments.price.tooltip.4").formatted(Formatting.GRAY),
                        Text.translatable("tradefinderui.enchantments.price.tooltip.5").formatted(Formatting.GRAY)
                ), mouseX + 110, enchantmentEntry.y - 5, enchantmentEntry.y + 20, this.height, 600);
            }
            if(mouseX > enchantmentEntry.x + enchantmentEntry.entryWidth - 21 - 15 - 14 - 23 - 2 && mouseX < enchantmentEntry.x + enchantmentEntry.entryWidth - 21 - 15 - 14 - 2 && mouseY > enchantmentEntry.y && mouseY < enchantmentEntry.y + enchantmentEntry.entryHeight && enchantmentEntry.enabled && !enchantmentEntry.maxPriceField.isActive()) {
                EnchantmentEntry.renderMultilineTooltip(context, MinecraftClient.getInstance().textRenderer, MultilineText.create(MinecraftClient.getInstance().textRenderer,
                        Text.translatable("tradefinderui.enchantments.level.tooltip").formatted(Formatting.GREEN)
                ), mouseX + 110, enchantmentEntry.y - 5, enchantmentEntry.y + 20, this.height, 600);
            }
        }
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

    @Override
    protected int getScrollbarX() {
        return this.width - 1;
    }

    @Override
    public int getRowTop(int index) {
        return this.getY() - (int)this.getScrollAmount() + index * this.itemHeight + this.headerHeight;
    }

    @Override
    public int getRowLeft() {
        return this.getX() + this.width / 2 - this.getRowWidth() / 2 - 1;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        resetButton.mouseClicked(mouseX, mouseY, button);
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if(!(keyCode == InputUtil.GLFW_KEY_BACKSPACE || (keyCode >= InputUtil.GLFW_KEY_0 && keyCode <= InputUtil.GLFW_KEY_9) || keyCode == InputUtil.GLFW_KEY_LEFT || keyCode == InputUtil.GLFW_KEY_RIGHT)) return false;
        for(EnchantmentEntry enchantmentEntry : children()) {
            if(enchantmentEntry.maxPriceField.isFocused()) return enchantmentEntry.maxPriceField.keyPressed(keyCode, scanCode, modifiers);
            if(enchantmentEntry.levelField.isFocused()) return enchantmentEntry.levelField.keyPressed(keyCode, scanCode, modifiers);
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
        if(!(keyCode == InputUtil.GLFW_KEY_BACKSPACE || (keyCode >= InputUtil.GLFW_KEY_0 && keyCode <= InputUtil.GLFW_KEY_9) || keyCode == InputUtil.GLFW_KEY_LEFT || keyCode == InputUtil.GLFW_KEY_RIGHT)) return false;
        for (EnchantmentEntry enchantmentEntry : this.children()) {
            enchantmentEntry.maxPriceField.keyReleased(keyCode, scanCode, modifiers);
            enchantmentEntry.levelField.keyReleased(keyCode, scanCode, modifiers);
        }
        return super.keyReleased(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean charTyped(char chr, int modifiers) {
        if(!Character.isDigit(chr)) return false;
        for (EnchantmentEntry enchantmentEntry : this.children()) {
            enchantmentEntry.maxPriceField.charTyped(chr, modifiers);
            enchantmentEntry.levelField.charTyped(chr, modifiers);
        }
        return super.charTyped(chr, modifiers);
    }

    @Nullable
    @Override
    protected EnchantmentEntry getEntryAtPosition(double x, double y) {
        int i = this.getRowWidth() / 2;
        int j = this.getX() + this.width / 2;
        int k = j - i;
        int l = j + i;
        int m = MathHelper.floor(y - (double)this.top) - this.headerHeight + (int)this.getScrollAmount();
        int n = m / this.itemHeight;
        return x < (double)this.getScrollbarX() && x >= (double)k && x <= (double)l && n >= 0 && m >= 0 && n < this.getEntryCount() ? this.children().get(n) : null;
    }
}