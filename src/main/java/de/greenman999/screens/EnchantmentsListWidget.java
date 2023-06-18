package de.greenman999.screens;

import com.mojang.blaze3d.systems.RenderSystem;
import de.greenman999.LibrarianTradeFinder;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.MultilineText;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.EntryListWidget;
import net.minecraft.client.render.*;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.MathHelper;

public class EnchantmentsListWidget extends EntryListWidget<EnchantmentEntry> {

    public GrayButtonWidget resetButton;

    public EnchantmentsListWidget(MinecraftClient client, int width, int height, int top, int bottom, int itemHeight) {
        super(client, width, height, top, bottom, itemHeight);
        setRenderBackground(false);
        setRenderSelection(false);
        setRenderHorizontalShadows(false);

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

        //setLeftPos(-2);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.setSelected(null);
        MatrixStack matrices = context.getMatrices();
        matrices.push();
        RenderSystem.enableDepthTest();
        matrices.translate(0, 0, 100);
        // 0xBF3AA640
        context.fill(5, 5, this.width + 5, 20, 0xAFC7C0C0);
        context.drawTextWithShadow(MinecraftClient.getInstance().textRenderer, Text.translatable("tradefinderui.enchantments.title"), 9, 9, 0xFFFFFF);
        resetButton.render(context, mouseX, mouseY, delta);
        RenderSystem.disableDepthTest();
        matrices.pop();

        /*this.renderBackground(context);
        int i = this.getScrollbarPositionX();
        int j = i + 6;
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferBuilder = tessellator.getBuffer();
        RenderSystem.setShader(GameRenderer::getPositionTexColorProgram);

        int o = this.getMaxScroll();
        if (o > 0) {
            RenderSystem.setShader(GameRenderer::getPositionColorProgram);
            int m = (int)((float)((this.bottom - this.top) * (this.bottom - this.top)) / (float)this.getMaxPosition());
            m = MathHelper.clamp(m, 32, this.bottom - this.top - 8);
            int n = (int)this.getScrollAmount() * (this.bottom - this.top - m) / o + this.top;
            if (n < this.top) {
                n = this.top;
            }

            bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
            bufferBuilder.vertex(i - 1, this.bottom, 0.0).color(0xFF3A3A3A).next();
            bufferBuilder.vertex(j - 1, this.bottom, 0.0).color(0xFF3A3A3A).next();
            bufferBuilder.vertex(j - 1, this.top + 4, 0.0).color(0xFF3A3A3A).next();
            bufferBuilder.vertex(i - 1, this.top + 4, 0.0).color(0xFF3A3A3A).next();
            bufferBuilder.vertex(i - 1, n + m, 0.0).color(128, 128, 128, 255).next();
            bufferBuilder.vertex(j - 1, n + m, 0.0).color(128, 128, 128, 255).next();
            bufferBuilder.vertex(j - 1, n + 4, 0.0).color(128, 128, 128, 255).next();
            bufferBuilder.vertex(i - 1, n + 4, 0.0).color(128, 128, 128, 255).next();
            bufferBuilder.vertex(i - 1, n + m - 1, 0.0).color(192, 192, 192, 255).next();
            bufferBuilder.vertex(j - 1 - 1, n + m - 1, 0.0).color(192, 192, 192, 255).next();
            bufferBuilder.vertex(j - 1 - 1, n + 4, 0.0).color(192, 192, 192, 255).next();
            bufferBuilder.vertex(i - 1, n + 4, 0.0).color(192, 192, 192, 255).next();
            tessellator.draw();
        }
        this.renderList(context, mouseX, mouseY, delta);

        this.renderDecorations(context, mouseX, mouseY);
        RenderSystem.disableBlend();*/
        super.render(context, mouseX, mouseY, delta);

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
    public void appendNarrations(NarrationMessageBuilder builder) {

    }

    @Override
    public int getRowWidth() {
        return this.width - 12;
    }

    @Override
    protected int getScrollbarPositionX() {
        return this.width - 1;
    }

    @Override
    public int getRowTop(int index) {
        return this.top - (int)this.getScrollAmount() + index * this.itemHeight + this.headerHeight;
    }

    @Override
    public int getRowLeft() {
        return this.left + this.width / 2 - this.getRowWidth() / 2 - 1;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        resetButton.mouseClicked(mouseX, mouseY, button);

        return super.mouseClicked(mouseX, mouseY, button);
        /*this.updateScrollingState(mouseX, mouseY, button);
        if (!this.isMouseOver(mouseX, mouseY)) {
            return false;
        } else {
            EnchantmentEntry entry = this.getEntryAtPosition(mouseX, mouseY + 4);
            if (entry != null) {
                if (entry.mouseClicked(mouseX, mouseY, button)) {
                    //this.setFocused(entry);
                    this.setDragging(true);
                    return true;
                }
            } else if (button == 0) {
                this.clickedHeader((int)(mouseX - (double)(this.left + this.width / 2 - this.getRowWidth() / 2)), (int)(mouseY - (double)this.top) + (int)this.getScrollAmount() - 4);
                return true;
            }

            return button == 0 && mouseX >= (double)this.getScrollbarPositionX() && mouseX < (double)(this.getScrollbarPositionX() + 6);
        }*/
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if(!(keyCode == InputUtil.GLFW_KEY_BACKSPACE || (keyCode >= InputUtil.GLFW_KEY_0 && keyCode <= InputUtil.GLFW_KEY_9) || keyCode == InputUtil.GLFW_KEY_LEFT || keyCode == InputUtil.GLFW_KEY_RIGHT)) return false;
        for (EnchantmentEntry enchantmentEntry : this.children()) {
            enchantmentEntry.maxPriceField.keyPressed(keyCode, scanCode, modifiers);
            enchantmentEntry.levelField.keyPressed(keyCode, scanCode, modifiers);
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
}