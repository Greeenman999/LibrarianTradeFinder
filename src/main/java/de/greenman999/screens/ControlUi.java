package de.greenman999.screens;

import com.mojang.blaze3d.systems.RenderSystem;
import de.greenman999.LibrarianTradeFinder;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.MultilineText;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.tooltip.TooltipBackgroundRenderer;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.EntryListWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;

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

        super.init();
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        return super.keyPressed(keyCode, scanCode, modifiers);
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

        private EnchantmentEntry hoveredEntry;
        public GrayButtonWidget resetButton;

        public EnchantmentsListWidget(MinecraftClient client, int width, int height, int top, int bottom, int itemHeight) {
            super(client, width, height, top, bottom, itemHeight);
            setRenderBackground(false);
            setRenderHorizontalShadows(false);

            for(Enchantment enchantment : LibrarianTradeFinder.getConfig().enchantments.keySet()) {
                this.addEntry(new EnchantmentEntry(enchantment, this.width, this.height));
            }

            this.resetButton = GrayButtonWidget.builder(Text.of("Reset All"), (buttonWidget) -> {
                for(EnchantmentEntry enchantmentEntry : this.children()) {
                    enchantmentEntry.maxPriceField.setText("64");
                    enchantmentEntry.levelField.setText(enchantmentEntry.enchantment.getMaxLevel() + "");
                    enchantmentEntry.enabled = false;
                }
            })
                    .color(0xAF000000)
                    .dimensions(this.width - 55, 5, 50, 15)
                    .tooltip(Tooltip.of(Text.of("Reset all enchantments to default values")))
                    .build();

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
            resetButton.render(matrices, mouseX, mouseY, delta);
            RenderSystem.disableDepthTest();
            matrices.pop();

            this.renderBackground(matrices);
            int i = this.getScrollbarPositionX();
            int j = i + 6;
            Tessellator tessellator = Tessellator.getInstance();
            BufferBuilder bufferBuilder = tessellator.getBuffer();
            RenderSystem.setShader(GameRenderer::getPositionTexColorProgram);
            this.hoveredEntry = this.isMouseOver((double)mouseX, (double)mouseY) ? this.getEntryAtPosition((double)mouseX, (double)mouseY) : null;

            int o = this.getMaxScroll();
            if (o > 0) {
                RenderSystem.disableTexture();
                RenderSystem.setShader(GameRenderer::getPositionColorProgram);
                int m = (int)((float)((this.bottom - this.top) * (this.bottom - this.top)) / (float)this.getMaxPosition());
                m = MathHelper.clamp(m, 32, this.bottom - this.top - 8);
                int n = (int)this.getScrollAmount() * (this.bottom - this.top - m) / o + this.top;
                if (n < this.top) {
                    n = this.top;
                }

                bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
                bufferBuilder.vertex((double)i, (double)this.bottom, 0.0).color(0, 0, 0, 100).next();
                bufferBuilder.vertex((double)j, (double)this.bottom, 0.0).color(0, 0, 0, 100).next();
                bufferBuilder.vertex((double)j, (double)this.top, 0.0).color(0, 0, 0, 100).next();
                bufferBuilder.vertex((double)i, (double)this.top, 0.0).color(0, 0, 0, 100).next();
                bufferBuilder.vertex((double)i, (double)(n + m), 0.0).color(128, 128, 128, 255).next();
                bufferBuilder.vertex((double)j, (double)(n + m), 0.0).color(128, 128, 128, 255).next();
                bufferBuilder.vertex((double)j, (double)n, 0.0).color(128, 128, 128, 255).next();
                bufferBuilder.vertex((double)i, (double)n, 0.0).color(128, 128, 128, 255).next();
                bufferBuilder.vertex((double)i, (double)(n + m - 1), 0.0).color(192, 192, 192, 255).next();
                bufferBuilder.vertex((double)(j - 1), (double)(n + m - 1), 0.0).color(192, 192, 192, 255).next();
                bufferBuilder.vertex((double)(j - 1), (double)n, 0.0).color(192, 192, 192, 255).next();
                bufferBuilder.vertex((double)i, (double)n, 0.0).color(192, 192, 192, 255).next();
                tessellator.draw();
            }
            this.renderList(matrices, mouseX, mouseY, delta);

            this.renderDecorations(matrices, mouseX, mouseY);
            RenderSystem.enableTexture();
            RenderSystem.disableBlend();
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
            resetButton.mouseClicked(mouseX, mouseY, button);
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
        public final TextFieldWidget levelField;
        private final int width;
        private final int height;
        private int x;
        private int y;
        private int entryWidth;
        private int entryHeight;

        public boolean enabled = false;

        public EnchantmentEntry(Enchantment enchantment, int width, int height) {
            super();
            this.enchantment = enchantment;
            this.width = width;
            this.height = height;
            this.enabled = LibrarianTradeFinder.getConfig().enchantments.get(enchantment);

            //maxPriceField = new TextFieldWidget(MinecraftClient.getInstance().textRenderer, 0, 0, 50, 20, Text.of("Max Price"));
            maxPriceField = new TextFieldWidget(MinecraftClient.getInstance().textRenderer, 0, 0, 20, 14, Text.of("Max Price"));
            maxPriceField.setMaxLength(2);
            maxPriceField.setText("64");
            //maxPriceField.setDrawsBackground(false);

            levelField = new TextFieldWidget(MinecraftClient.getInstance().textRenderer, 0, 0, 14, 14, Text.of("Level"));
            levelField.setMaxLength(1);
            levelField.setText(enchantment.getMaxLevel() + "");

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

            maxPriceField.setVisible(enabled);
            levelField.setVisible(enabled);
            if(enabled) {
                DrawableHelper.fill(matrices, x, y, x + entryWidth, y + entryHeight, 0x3F00FF00);
                DrawableHelper.drawTextWithShadow(matrices, MinecraftClient.getInstance().textRenderer, Text.of("$:"), x + entryWidth - 21 - 10, y + (entryHeight / 2 / 2), 0xFFFFFF);
                DrawableHelper.drawTextWithShadow(matrices, MinecraftClient.getInstance().textRenderer, Text.of("LVL:"), x + entryWidth - 21 - 15 - 14 - 23, y + (entryHeight / 2 / 2), 0xFFFFFF);
            }else {
                DrawableHelper.fill(matrices, x, y, x + entryWidth, y + entryHeight, 0x0FC7C0C0);
            }
            DrawableHelper.drawTextWithShadow(matrices, MinecraftClient.getInstance().textRenderer, Text.translatable(enchantment.getTranslationKey()), 8, y + (entryHeight / 2 / 2), 0xFFFFFF);

            RenderSystem.disableDepthTest();
            matrices.pop();

            matrices.push();
            RenderSystem.enableDepthTest();
            matrices.translate(0, 0, -50);
            maxPriceField.setX(x + entryWidth - 21);
            maxPriceField.setY(y + 1);
            maxPriceField.render(matrices, mouseX, mouseY, tickDelta);

            levelField.setX(x + entryWidth - 21 - 15 - 14);
            levelField.setY(y + 1);
            levelField.render(matrices, mouseX, mouseY, tickDelta);
            RenderSystem.disableDepthTest();
            matrices.pop();

            if(mouseX > this.x + entryWidth - 21 - 10 && mouseX < this.x + this.entryWidth - 21 && mouseY > y && mouseY < y + entryHeight && enabled) {
                renderMultilineTooltip(matrices, MinecraftClient.getInstance().textRenderer, MultilineText.create(MinecraftClient.getInstance().textRenderer,
                        Text.literal("Set the maximum price for this enchantment.").formatted(Formatting.GREEN),
                        Text.literal(""),
                        Text.literal("Enchantment Level 1: 5-19").formatted(Formatting.GRAY),
                        Text.literal("Enchantment Level 2: 8-32").formatted(Formatting.GRAY),
                        Text.literal("Enchantment Level 3: 11-45").formatted(Formatting.GRAY),
                        Text.literal("Enchantment Level 4: 14-58").formatted(Formatting.GRAY),
                        Text.literal("Enchantment Level 5: 17-64").formatted(Formatting.GRAY)
                ), mouseX + 110, y - 5, y + 20, this.width, this.height);
            }
            if(mouseX > this.x + entryWidth - 21 - 15 - 14 - 23 && mouseX < this.x + this.entryWidth - 21 - 15 - 14 && mouseY > y && mouseY < y + entryHeight && enabled) {
                renderMultilineTooltip(matrices, MinecraftClient.getInstance().textRenderer, MultilineText.create(MinecraftClient.getInstance().textRenderer,
                        Text.literal("Set the level for this enchantment.").formatted(Formatting.GREEN)
                ), mouseX + 110, y - 5, y + 20, this.width, this.height);
            }
        }

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            maxPriceField.mouseClicked(mouseX, mouseY, button);
            int i = 0;
            if(enabled) {
                i = 21;
            }
            if(mouseX > this.x && mouseX < this.x + this.entryWidth - i && mouseY > y && mouseY < y + entryHeight) {
                enabled = !enabled;
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

    public static class GrayButtonWidget extends ButtonWidget {
        int color;

        protected GrayButtonWidget(int x, int y, int width, int height, Text message, PressAction onPress, NarrationSupplier narrationSupplier, int color) {
            super(x, y, width, height, message, onPress, narrationSupplier);
            color = color;
        }

        @Override
        public void renderButton(MatrixStack matrices, int mouseX, int mouseY, float delta) {
            DrawableHelper.fill(matrices, this.getX(), this.getY(), this.getX() + this.getWidth(), this.getY() + this.getHeight(), color);
            int j = this.active ? 16777215 : 10526880;
            drawCenteredText(matrices, MinecraftClient.getInstance().textRenderer, this.getMessage(), this.getX() + this.width / 2, this.getY() + (this.height - 7) / 2, j | MathHelper.ceil(this.alpha * 255.0F) << 24);
        }


        public static GrayButtonWidget.Builder builder(Text message, PressAction onPress) {
            return new GrayButtonWidget.Builder(message, onPress);
        }

        public static class Builder extends ButtonWidget.Builder {
            private final Text message;
            private final PressAction onPress;
            @Nullable
            private Tooltip tooltip;
            private int x;
            private int y;
            private int width = 150;
            private int height = 20;
            private NarrationSupplier narrationSupplier;
            private int color;

            public Builder(Text message, PressAction onPress) {
                super(message, onPress);
                this.narrationSupplier = ButtonWidget.DEFAULT_NARRATION_SUPPLIER;
                this.message = message;
                this.onPress = onPress;
            }

            public GrayButtonWidget.Builder position(int x, int y) {
                this.x = x;
                this.y = y;
                return this;
            }

            public GrayButtonWidget.Builder width(int width) {
                this.width = width;
                return this;
            }

            public GrayButtonWidget.Builder size(int width, int height) {
                this.width = width;
                this.height = height;
                return this;
            }

            public GrayButtonWidget.Builder dimensions(int x, int y, int width, int height) {
                return this.position(x, y).size(width, height);
            }

            public GrayButtonWidget.Builder tooltip(@Nullable Tooltip tooltip) {
                this.tooltip = tooltip;
                return this;
            }

            public GrayButtonWidget.Builder narrationSupplier(NarrationSupplier narrationSupplier) {
                this.narrationSupplier = narrationSupplier;
                return this;
            }

            public GrayButtonWidget.Builder color(int color) {
                this.color = color;
                return this;
            }

            public GrayButtonWidget build() {
                GrayButtonWidget buttonWidget = new GrayButtonWidget(this.x, this.y, this.width, this.height, this.message, this.onPress, this.narrationSupplier, this.color);
                buttonWidget.setTooltip(this.tooltip);
                return buttonWidget;
            }
        }
    }

}
