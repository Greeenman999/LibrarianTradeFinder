package de.greenman999.screens;

import com.mojang.blaze3d.systems.RenderSystem;
import de.greenman999.LibrarianTradeFinder;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.MultilineText;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.tooltip.OrderedTextTooltipComponent;
import net.minecraft.client.gui.tooltip.TooltipBackgroundRenderer;
import net.minecraft.client.gui.widget.*;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.MathHelper;
import org.apache.logging.log4j.core.pattern.AbstractStyleNameConverter;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;

import java.util.List;

public class ControlUi extends Screen {

    private final Screen parent;
    private EnchantmentsListWidget enchantmentsListWidget;

    public ControlUi(Screen parent) {
        super(Text.literal("Librarian Trade Finder"));
        this.parent = parent;
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        this.drawVerticalLine(matrices, this.width / 2, 5, this.height - 5, 0xFFC7C0C0);

        //this.renderTooltip(matrices, Text.of("Max Price"), mouseX, mouseY);


        super.renderBackground(matrices);
        super.render(matrices, mouseX, mouseY, delta);
    }

    @Override
    protected void init() {
        this.addDrawableChild(ButtonWidget.builder(ScreenTexts.DONE, (buttonWidget) -> {
                    this.client.setScreen(this.parent);
                    LibrarianTradeFinder.getConfig().save();
                })
                        .dimensions(this.width - 107, this.height - 27, 100, 20)
                .build());
        // create list of buttons with scrollbar
        enchantmentsListWidget = new EnchantmentsListWidget(this.client, this.width / 2 - 10, this.height, 21, this.height - 5, 20);
        this.addDrawableChild(enchantmentsListWidget);
        System.out.println(this.width / 2 - 10);
        System.out.println(this.height);

        super.init();
    }

    @Override
    public void tick() {
        super.tick();
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        for(EnchantmentEntry enchantmentEntry : enchantmentsListWidget.children()) {
            enchantmentEntry.maxPriceField.mouseClicked(mouseX, mouseY, button);
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    public static class EnchantmentsListWidget extends EntryListWidget<EnchantmentEntry> {

        public EnchantmentsListWidget(MinecraftClient client, int width, int height, int top, int bottom, int itemHeight) {
            super(client, width, height, top, bottom, itemHeight);
            setRenderBackground(false);
            setRenderHorizontalShadows(false);

            for(Enchantment enchantment : LibrarianTradeFinder.getConfig().enchantments.keySet()) {
                this.addEntry(new EnchantmentEntry(enchantment));
            }
            //setLeftPos(-2);
        }

        @Override
        public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
            matrices.push();
            RenderSystem.enableDepthTest();
            matrices.translate(0, 0, 100);
            // 0xBF3AA640
            DrawableHelper.fill(matrices, 5, 5, this.width - 5, 20, 0x5FC7C0C0);
            DrawableHelper.drawTextWithShadow(matrices, MinecraftClient.getInstance().textRenderer, Text.of("Enchantments"), 9, 9, 0xFFFFFF);
            RenderSystem.disableDepthTest();
            matrices.pop();

            super.render(matrices, mouseX, mouseY, delta);
        }

        @Override
        public void appendNarrations(NarrationMessageBuilder builder) {

        }

        @Override
        public int getRowWidth() {
            return this.width - 10;
        }

        @Override
        protected int getScrollbarPositionX() {
            return this.width;
        }

        @Override
        public int getRowTop(int index) {
            return this.top + 4 - (int)this.getScrollAmount() + index * this.itemHeight + this.headerHeight;
        }

        @Override
        public int getRowLeft() {
            return this.left + this.width / 2 - this.getRowWidth() / 2;
        }

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            this.updateScrollingState(mouseX, mouseY, button);
            if (!this.isMouseOver(mouseX, mouseY)) {
                return false;
            } else {
                EnchantmentEntry entry = this.getEntryAtPosition(mouseX, mouseY + 4);
                if (entry != null) {
                    if (entry.mouseClicked(mouseX, mouseY, button)) {
                        this.setFocused(entry);
                        this.setDragging(true);
                        return true;
                    }
                } else if (button == 0) {
                    this.clickedHeader((int)(mouseX - (double)(this.left + this.width / 2 - this.getRowWidth() / 2)), (int)(mouseY - (double)this.top) + (int)this.getScrollAmount() - 4);
                    return true;
                }

                return button == 0 && mouseX >= (double)this.getScrollbarPositionX() && mouseX < (double)(this.getScrollbarPositionX() + 6);
            }
        }
    }

    public static class EnchantmentEntry extends EntryListWidget.Entry<EnchantmentEntry> {

        private final Enchantment enchantment;
        public final TextFieldWidget maxPriceField;
        public final OrderedTextTooltipComponent tooltipComponent;
        private int x;
        private int y;
        private int entryWidth;
        private int entryHeight;

        public EnchantmentEntry(Enchantment enchantment) {
            super();
            this.enchantment = enchantment;

            //maxPriceField = new TextFieldWidget(MinecraftClient.getInstance().textRenderer, 0, 0, 50, 20, Text.of("Max Price"));
            maxPriceField = new TextFieldWidget(MinecraftClient.getInstance().textRenderer, 0, 0, 20, 14, Text.of("Max Price"));
            maxPriceField.setMaxLength(2);
            maxPriceField.setText("64");
            //maxPriceField.setDrawsBackground(false);

            tooltipComponent = new OrderedTextTooltipComponent(Text.of("Max Price").asOrderedText());
        }

        @Override
        public void render(MatrixStack matrices, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
            this.x = x;
            this.y = y;
            this.entryWidth = entryWidth;
            this.entryHeight = entryHeight;
            if(y < 8) return;
            matrices.push();
            RenderSystem.enableDepthTest();
            matrices.translate(0, 0, -100);

            if(LibrarianTradeFinder.getConfig().enchantments.get(enchantment)) {
                maxPriceField.setVisible(true);
                DrawableHelper.fill(matrices, x, y, x + entryWidth, y + entryHeight, 0x3F00FF00);
                DrawableHelper.drawTextWithShadow(matrices, MinecraftClient.getInstance().textRenderer, Text.of("Max Price:"), x + entryWidth - 21 - 52, y + (entryHeight / 2 / 2), 0xFFFFFF);
            }else {
                maxPriceField.setVisible(false);
                DrawableHelper.fill(matrices, x, y, x + entryWidth, y + entryHeight, 0x0FC7C0C0);
            }
            DrawableHelper.drawTextWithShadow(matrices, MinecraftClient.getInstance().textRenderer, enchantment.getName(enchantment.getMaxLevel()).copy().formatted(Formatting.WHITE), 8, y + (entryHeight / 2 / 2), 0xFFFFFF);

            RenderSystem.disableDepthTest();
            matrices.pop();

            matrices.push();
            RenderSystem.enableDepthTest();
            matrices.translate(0, 0, -50);
            maxPriceField.setX(x + entryWidth - 21);
            maxPriceField.setY(y + 1);
            maxPriceField.render(matrices, mouseX, mouseY, tickDelta);
            RenderSystem.disableDepthTest();
            matrices.pop();

            if(mouseX > this.x + entryWidth - 21 - 51 && mouseX < this.x + this.entryWidth - 21 && mouseY > y && mouseY < y + entryHeight) {
                renderMultilineTooltip(matrices, MinecraftClient.getInstance().textRenderer, MultilineText.create(MinecraftClient.getInstance().textRenderer, Text.of("Max Price")), mouseX + 30, mouseY, y, this.entryWidth, this.entryHeight);
            }
        }

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            maxPriceField.mouseClicked(mouseX, mouseY, button);
            int i = 0;
            if(LibrarianTradeFinder.getConfig().enchantments.get(enchantment)) {
                i = 21;
            }
            if(mouseX > this.x && mouseX < this.x + this.entryWidth - i && mouseY > y && mouseY < y + entryHeight) {
                LibrarianTradeFinder.getConfig().enchantments.put(enchantment, !LibrarianTradeFinder.getConfig().enchantments.get(enchantment));
            }

            return true;
        }

        @Override
        public boolean charTyped(char chr, int modifiers) {
            return maxPriceField.charTyped(chr, modifiers);
        }

        @Override
        public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
            return maxPriceField.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
        }

        @Override
        public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
            return maxPriceField.keyPressed(keyCode, scanCode, modifiers);
        }

        @Override
        public void mouseMoved(double mouseX, double mouseY) {
            maxPriceField.mouseMoved(mouseX, mouseY);
        }

        @Override
        public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
            return maxPriceField.keyReleased(keyCode, scanCode, modifiers);
        }

        @Override
        public boolean mouseReleased(double mouseX, double mouseY, int button) {
            return maxPriceField.mouseReleased(mouseX, mouseY, button);
        }

        @Override
        public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
            return maxPriceField.mouseScrolled(mouseX, mouseY, amount);
        }

        private void drawOutline(MatrixStack matrices, int x1, int y1, int x2, int y2, int width, int color) {
            DrawableHelper.fill(matrices, x1, y1, x2, y1 + width, color);
            DrawableHelper.fill(matrices, x2, y1, x2 - width, y2, color);
            DrawableHelper.fill(matrices, x1, y2, x2, y2 - width, color);
            DrawableHelper.fill(matrices, x1, y1, x1 + width, y2, color);
        }

        public static void renderMultilineTooltip(MatrixStack matrices, TextRenderer textRenderer, MultilineText text, int centerX, int yAbove, int yBelow, int screenWidth, int screenHeight) {
            if (text.count() > 0) {
                int maxWidth = text.getMaxWidth();
                int lineHeight = textRenderer.fontHeight + 1;
                int height = text.count() * lineHeight - 1;

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

                matrices.push();
                RenderSystem.enableDepthTest();
                Tessellator tessellator = Tessellator.getInstance();
                BufferBuilder bufferBuilder = tessellator.getBuffer();
                RenderSystem.setShader(GameRenderer::getPositionColorProgram);
                bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
                Matrix4f matrix4f = matrices.peek().getPositionMatrix();
                TooltipBackgroundRenderer.render(
                        DrawableHelper::fillGradient,
                        matrix4f,
                        bufferBuilder,
                        drawX,
                        drawY,
                        maxWidth,
                        height,
                        600
                );
                RenderSystem.enableDepthTest();
                RenderSystem.disableTexture();
                RenderSystem.enableBlend();
                RenderSystem.defaultBlendFunc();
                BufferRenderer.drawWithGlobalProgram(bufferBuilder.end());
                RenderSystem.disableBlend();
                RenderSystem.enableTexture();
                matrices.translate(0.0, 0.0, 610.0);

                text.drawWithShadow(matrices, drawX, drawY, lineHeight, -1);

                matrices.pop();
                RenderSystem.disableDepthTest();
            }
        }
    }

}
