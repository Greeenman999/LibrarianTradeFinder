package de.greenman999.screens;

import com.mojang.blaze3d.systems.RenderSystem;
import de.greenman999.LibrarianTradeFinder;
import de.greenman999.TradeFinder;
import de.greenman999.config.TradeFinderConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.MultilineText;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
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
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.Nullable;


public class ControlUi extends Screen {

    private final Screen parent;
    private EnchantmentsListWidget enchantmentsListWidget;

    public ControlUi(Screen parent) {
        super(Text.literal("Librarian Trade Finder"));
        this.parent = parent;
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        context.drawVerticalLine(this.width / 2, 4, this.height - 5, 0xFFC7C0C0);
        super.renderBackground(context);

        context.fill(this.width / 2 + 6, 5, this.width - 5, 20, 0xAFC7C0C0);
        context.drawTextWithShadow(this.textRenderer, Text.of("Options"), this.width / 2 + 10, 9, 0xFFFFFF);
        super.render(context, mouseX, mouseY, delta);
    }

    @Override
    protected void init() {
        this.addDrawableChild(GrayButtonWidget.builder(Text.of("Save"), (buttonWidget) -> {
                    if (this.client != null) {
                        this.client.setScreen(this.parent);
                    }
                    LibrarianTradeFinder.getConfig().save();
                })
                        .dimensions(this.width / 2 + 6, this.height - 25, width / 2 / 2 - 6 - 3, 20)
                        .color(0x4FC7C0C0)
                .build());
        this.addDrawableChild(GrayButtonWidget.builder(Text.of(Formatting.GREEN + "Start Search"), (buttonWidget) -> TradeFinder.search())
                .dimensions(this.width / 2 + this.width / 2 / 2 + 3, this.height - 25, width / 2 / 2 - 6, 20)
                .color(0x4FC7C0C0)
                .build());

        enchantmentsListWidget = new EnchantmentsListWidget(this.client, this.width / 2 - 10, this.height, 21, this.height - 5, 20);
        this.addDrawableChild(enchantmentsListWidget);

        this.addDrawableChild(GrayButtonWidget.builder(getButtonText("Teleport to the villager", LibrarianTradeFinder.getConfig().tpToVillager), (buttonWidget) -> {
                    LibrarianTradeFinder.getConfig().tpToVillager = !LibrarianTradeFinder.getConfig().tpToVillager;

                    buttonWidget.setMessage(getButtonText("Teleport to the villager", LibrarianTradeFinder.getConfig().tpToVillager));
                })
                .dimensions(this.width / 2 + 6, 25, this.width / 2 - 10, 20)
                .color(0x4FC7C0C0)
                .tooltip(Tooltip.of(Text.of("Teleports you to the villager when the lectern is broken to try to pickup the lecterns dropped there.")))
                .build());
        this.addDrawableChild(GrayButtonWidget.builder(getButtonText("Prevent the axe from breaking", LibrarianTradeFinder.getConfig().preventAxeBreaking), (buttonWidget) -> {
                    LibrarianTradeFinder.getConfig().preventAxeBreaking = !LibrarianTradeFinder.getConfig().preventAxeBreaking;

                    buttonWidget.setMessage(getButtonText("Prevent the axe from breaking", LibrarianTradeFinder.getConfig().preventAxeBreaking));
                })
                .dimensions(this.width / 2 + 6 , 50, this.width / 2 - 10, 20)
                .color(0x4FC7C0C0)
                .tooltip(Tooltip.of(Text.of("Stops the search process when your axe is about to break.")))
                .build());
        this.addDrawableChild(GrayButtonWidget.builder(getButtonText("Legit Mode", LibrarianTradeFinder.getConfig().legitMode), (buttonWidget) -> {
                    LibrarianTradeFinder.getConfig().legitMode = !LibrarianTradeFinder.getConfig().legitMode;

                    buttonWidget.setMessage(getButtonText("Legit Mode", LibrarianTradeFinder.getConfig().legitMode));
                })
                .dimensions(this.width / 2 + 6, 75, this.width / 2 - 10, 20)
                .color(0x4FC7C0C0)
                .tooltip(Tooltip.of(Text.of("When enabled automatically look at the lectern or villager to prevent getting flagged by some anti cheats.")))
                .build());

        super.init();
    }

    public Text getButtonText(String text, boolean enabled) {
        return Text.of(text + ": " + (enabled ? Formatting.GREEN + "Enabled" : Formatting.RED + "Disabled"));
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if(super.keyPressed(keyCode, scanCode, modifiers)) {
            return true;
        }else if (keyCode == 256) {
            LibrarianTradeFinder.getConfig().save();
            if (this.client != null) {
                this.client.setScreen(parent);
            }
            return true;
        }else {
            return false;
        }
    }

    @Override
    public void tick() {
        super.tick();
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        for(EnchantmentEntry enchantmentEntry : enchantmentsListWidget.children()) {
            enchantmentEntry.maxPriceField.mouseClicked(mouseX, mouseY, button);
            enchantmentEntry.levelField.mouseClicked(mouseX, mouseY, button);
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    public static class EnchantmentsListWidget extends EntryListWidget<EnchantmentEntry> {

        public GrayButtonWidget resetButton;

        public EnchantmentsListWidget(MinecraftClient client, int width, int height, int top, int bottom, int itemHeight) {
            super(client, width, height, top, bottom, itemHeight);
            setRenderBackground(false);
            setRenderHorizontalShadows(false);

            for(Enchantment enchantment : LibrarianTradeFinder.getConfig().enchantments.keySet()) {
                this.addEntry(new EnchantmentEntry(enchantment));
            }

            this.resetButton = GrayButtonWidget.builder(Text.of("Reset All"), (buttonWidget) -> {
                for(EnchantmentEntry enchantmentEntry : this.children()) {
                    enchantmentEntry.maxPriceField.setText("64");
                    enchantmentEntry.levelField.setText(String.valueOf(enchantmentEntry.enchantment.getMaxLevel()));
                    enchantmentEntry.enabled = false;
                }
            })
                    .color(0x5FC7C0C0)
                    .dimensions(this.width - 45, 5, 50, 15)
                    .tooltip(Tooltip.of(Text.of("Reset all enchantments to default values")))
                    .build();

            //setLeftPos(-2);
        }

        @Override
        public void render(DrawContext context, int mouseX, int mouseY, float delta) {
            MatrixStack matrices = context.getMatrices();
            matrices.push();
            RenderSystem.enableDepthTest();
            matrices.translate(0, 0, 100);
            // 0xBF3AA640
            context.fill(5, 5, this.width + 5, 20, 0xAFC7C0C0);
            context.drawTextWithShadow(MinecraftClient.getInstance().textRenderer, Text.of("Enchantments"), 9, 9, 0xFFFFFF);
            resetButton.render(context, mouseX, mouseY, delta);
            RenderSystem.disableDepthTest();
            matrices.pop();

            this.renderBackground(context);
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
            RenderSystem.disableBlend();

            for(EnchantmentEntry enchantmentEntry : this.children()) {
                if(enchantmentEntry.maxPriceField.isActive()) {
                    EnchantmentEntry.renderMultilineTooltip(context, MinecraftClient.getInstance().textRenderer, MultilineText.create(MinecraftClient.getInstance().textRenderer,
                            Text.literal("Enchantment Level 1: 5 - 19").formatted(Formatting.GRAY),
                            Text.literal("Enchantment Level 2: 8 - 32").formatted(Formatting.GRAY),
                            Text.literal("Enchantment Level 3: 11 - 45").formatted(Formatting.GRAY),
                            Text.literal("Enchantment Level 4: 14 - 58").formatted(Formatting.GRAY),
                            Text.literal("Enchantment Level 5: 17 - 64").formatted(Formatting.GRAY)

                    ), enchantmentEntry.maxPriceField.getX() + 75, enchantmentEntry.y - 5, enchantmentEntry.y + 20, this.height, 580);
                }

                if(mouseX > enchantmentEntry.x + enchantmentEntry.entryWidth - 21 - 10 - 2 && mouseX < enchantmentEntry.x + enchantmentEntry.entryWidth - 21 - 2 && mouseY > enchantmentEntry.y && mouseY < enchantmentEntry.y + enchantmentEntry.entryHeight && enchantmentEntry.enabled && !enchantmentEntry.maxPriceField.isActive()) {
                    EnchantmentEntry.renderMultilineTooltip(context, MinecraftClient.getInstance().textRenderer, MultilineText.create(MinecraftClient.getInstance().textRenderer,
                            Text.literal("Set the maximum price for this enchantment.").formatted(Formatting.GREEN),
                            Text.literal(""),
                            Text.literal("Enchantment Level 1: 5-19").formatted(Formatting.GRAY),
                            Text.literal("Enchantment Level 2: 8-32").formatted(Formatting.GRAY),
                            Text.literal("Enchantment Level 3: 11-45").formatted(Formatting.GRAY),
                            Text.literal("Enchantment Level 4: 14-58").formatted(Formatting.GRAY),
                            Text.literal("Enchantment Level 5: 17-64").formatted(Formatting.GRAY)
                    ), mouseX + 110, enchantmentEntry.y - 5, enchantmentEntry.y + 20, this.height, 600);
                }
                if(mouseX > enchantmentEntry.x + enchantmentEntry.entryWidth - 21 - 15 - 14 - 23 - 2 && mouseX < enchantmentEntry.x + enchantmentEntry.entryWidth - 21 - 15 - 14 - 2 && mouseY > enchantmentEntry.y && mouseY < enchantmentEntry.y + enchantmentEntry.entryHeight && enchantmentEntry.enabled && !enchantmentEntry.maxPriceField.isActive()) {
                    EnchantmentEntry.renderMultilineTooltip(context, MinecraftClient.getInstance().textRenderer, MultilineText.create(MinecraftClient.getInstance().textRenderer,
                            Text.literal("Set the level for this enchantment.").formatted(Formatting.GREEN)
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
                        //this.setFocused(entry);
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
        private int x;
        private int y;
        private int entryWidth;
        private int entryHeight;

        public boolean enabled;
        public TradeFinderConfig.EnchantmentOption enchantmentOption;

        public EnchantmentEntry(Enchantment enchantment) {
            super();
            this.enchantment = enchantment;
            this.enabled = LibrarianTradeFinder.getConfig().enchantments.get(enchantment).isEnabled();
            this.enchantmentOption = LibrarianTradeFinder.getConfig().enchantments.get(enchantment);

            //maxPriceField = new TextFieldWidget(MinecraftClient.getInstance().textRenderer, 0, 0, 50, 20, Text.of("Max Price"));
            maxPriceField = new TextFieldWidget(MinecraftClient.getInstance().textRenderer, 0, 0, 20, 14, Text.of("Max Price"));
            maxPriceField.setMaxLength(2);
            maxPriceField.setText(String.valueOf(enchantmentOption.getMaxPrice()));
            //maxPriceField.setDrawsBackground(false);

            levelField = new TextFieldWidget(MinecraftClient.getInstance().textRenderer, 0, 0, 14, 14, Text.of("Level"));
            levelField.setMaxLength(1);
            levelField.setText(String.valueOf(enchantmentOption.getLevel()));

        }

        @Override
        public void render(DrawContext context, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
            MatrixStack matrices = context.getMatrices();
            this.x = x;
            this.y = y;
            this.entryWidth = entryWidth;
            this.entryHeight = entryHeight;

            if(!maxPriceField.getText().equals("") && !maxPriceField.isActive() && (Integer.parseInt(maxPriceField.getText()) > 64 || Integer.parseInt(maxPriceField.getText()) < 5)) maxPriceField.setText("64");
            if(!levelField.getText().equals("") && !levelField.isActive() && (Integer.parseInt(levelField.getText()) > enchantment.getMaxLevel() || Integer.parseInt(levelField.getText()) < 1)) levelField.setText(String.valueOf(enchantment.getMaxLevel()));

            enchantmentOption.setEnabled(enabled);
            enchantmentOption.setMaxPrice(!maxPriceField.getText().equals("") ? Integer.parseInt(maxPriceField.getText()) : 64);
            enchantmentOption.setLevel(!levelField.getText().equals("") ? Integer.parseInt(levelField.getText()) : enchantment.getMaxLevel());

            if(y < 8) return;
            matrices.push();
            RenderSystem.enableDepthTest();
            matrices.translate(0, 0, -100);

            maxPriceField.setVisible(enabled);
            levelField.setVisible(enabled);
            if(enabled) {
                context.fill(x, y, x + entryWidth, y + entryHeight, 0x3F00FF00);
                context.drawTextWithShadow(MinecraftClient.getInstance().textRenderer, Text.of("$:"), x + entryWidth - 21 - 10, y + (entryHeight / 2 / 2), 0xFFFFFF);
                context.drawTextWithShadow(MinecraftClient.getInstance().textRenderer, Text.of("LVL:"), x + entryWidth - 21 - 15 - 14 - 23, y + (entryHeight / 2 / 2), 0xFFFFFF);
            }else {
                context.fill(x, y, x + entryWidth, y + entryHeight, 0x1AC7C0C0);
            }
            context.drawTextWithShadow(MinecraftClient.getInstance().textRenderer, Text.translatable(enchantment.getTranslationKey()), 8, y + (entryHeight / 2 / 2), 0xFFFFFF);

            //RenderSystem.disableDepthTest();
            matrices.pop();

            matrices.push();
            RenderSystem.enableDepthTest();
            matrices.translate(0, 0, 50);
            maxPriceField.setX(x + entryWidth - 21);
            maxPriceField.setY(y + 1);
            maxPriceField.render(context, mouseX, mouseY, tickDelta);

            RenderSystem.enableDepthTest();
            levelField.setX(x + entryWidth - 21 - 15 - 14);
            levelField.setY(y + 1);
            levelField.render(context, mouseX, mouseY, tickDelta);
            RenderSystem.disableDepthTest();
            matrices.pop();
        }

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            boolean maxPriceFieldReturn = maxPriceField.mouseClicked(mouseX, mouseY, button);
            boolean levelFieldReturn = levelField.mouseClicked(mouseX, mouseY, button);
            int i = 0;
            if(enabled) {
                i = 21 + 15 + 14;
            }
            if(mouseX > this.x && mouseX < this.x + this.entryWidth - i && mouseY > y && mouseY < y + entryHeight) {
                enabled = !enabled;
                return true;
            } else if(mouseX > this.x + entryWidth - 21 - 10 - 4 && mouseX < this.x + this.entryWidth - 21 && mouseY > y && mouseY < y + entryHeight && enabled) {
                enabled = false;
                return true;
            }
            return maxPriceFieldReturn || levelFieldReturn;
        }

        @Override
        public boolean charTyped(char chr, int modifiers) {
            System.out.println(chr);
            if(!Character.isDigit(chr)) return false;
            boolean maxPriceFieldReturn = maxPriceField.charTyped(chr, modifiers);
            boolean levelFieldReturn = levelField.charTyped(chr, modifiers);
            return maxPriceFieldReturn || levelFieldReturn;
        }

        @Override
        public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
            boolean maxPriceFieldReturn = maxPriceField.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
            boolean levelFieldReturn = levelField.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
            return maxPriceFieldReturn || levelFieldReturn;
        }

        @Override
        public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
            System.out.println(keyCode);
            if(keyCode != 259 && (keyCode < 320 || keyCode > 329)) return false;
            boolean maxPriceFieldReturn = maxPriceField.keyPressed(keyCode, scanCode, modifiers);
            boolean levelFieldReturn = levelField.keyPressed(keyCode, scanCode, modifiers);
            return maxPriceFieldReturn || levelFieldReturn;
        }

        @Override
        public void mouseMoved(double mouseX, double mouseY) {
            maxPriceField.mouseMoved(mouseX, mouseY);
            levelField.mouseMoved(mouseX, mouseY);
        }

        @Override
        public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
            if(keyCode != 259 && (keyCode < 320 || keyCode > 329)) return false;
            boolean maxPriceFieldReturn = maxPriceField.keyReleased(keyCode, scanCode, modifiers);
            boolean levelFieldReturn = levelField.keyReleased(keyCode, scanCode, modifiers);
            return maxPriceFieldReturn || levelFieldReturn;
        }

        @Override
        public boolean mouseReleased(double mouseX, double mouseY, int button) {
            boolean maxPriceFieldReturn = maxPriceField.mouseReleased(mouseX, mouseY, button);
            boolean levelFieldReturn = levelField.mouseReleased(mouseX, mouseY, button);
            return maxPriceFieldReturn || levelFieldReturn;
        }

        @Override
        public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
            boolean maxPriceFieldReturn = maxPriceField.mouseScrolled(mouseX, mouseY, amount);
            boolean levelFieldReturn = levelField.mouseScrolled(mouseX, mouseY, amount);
            return maxPriceFieldReturn || levelFieldReturn;
        }

        public static void renderMultilineTooltip(DrawContext context, TextRenderer textRenderer, MultilineText text, int centerX, int yAbove, int yBelow, int screenHeight, int z) {
            MatrixStack matrices = context.getMatrices();
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
                TooltipBackgroundRenderer.render(
                        context,
                        drawX,
                        drawY,
                        maxWidth,
                        height,
                        z
                );
                RenderSystem.enableDepthTest();
                RenderSystem.enableBlend();
                RenderSystem.defaultBlendFunc();
                BufferRenderer.drawWithGlobalProgram(bufferBuilder.end());
                RenderSystem.disableBlend();
                matrices.translate(0.0, 0.0, z + 10.0);

                text.drawWithShadow(context, drawX, drawY, lineHeight, -1);

                matrices.pop();
                RenderSystem.disableDepthTest();
            }
        }
    }

    public static class GrayButtonWidget extends ButtonWidget {
        int color;

        protected GrayButtonWidget(int x, int y, int width, int height, Text message, PressAction onPress, NarrationSupplier narrationSupplier, int color) {
            super(x, y, width, height, message, onPress, narrationSupplier);
            this.color = color;
        }

        @Override
        public void renderButton(DrawContext context, int mouseX, int mouseY, float delta) {
            MatrixStack matrices = context.getMatrices();
            matrices.push();
            RenderSystem.enableDepthTest();
            matrices.translate(0, 0, 200);
            context.fill(this.getX(), this.getY(), this.getX() + this.getWidth(), this.getY() + this.getHeight(), color);
            int j = this.active ? 16777215 : 10526880;
            context.drawCenteredTextWithShadow(MinecraftClient.getInstance().textRenderer, this.getMessage(), this.getX() + this.width / 2, this.getY() + (this.height - 7) / 2, j | MathHelper.ceil(this.alpha * 255.0F) << 24);
            matrices.pop();
            RenderSystem.disableDepthTest();
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
