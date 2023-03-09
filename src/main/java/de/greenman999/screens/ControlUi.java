package de.greenman999.screens;

import com.mojang.blaze3d.systems.RenderSystem;
import de.greenman999.LibrarianTradeFinder;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.Nullable;

public class ControlUi extends Screen {

    private final Screen parent;

    public ControlUi(Screen parent) {
        super(Text.literal("Librarian Trade Finder"));
        this.parent = parent;
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        this.drawVerticalLine(matrices, this.width / 2, 5, this.height - 5, 0xFFC7C0C0);

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
        this.addDrawableChild(new EnchantmentsListWidget(this.client, this.width / 2 - 10, this.height, 21, this.height - 5, 20));
        System.out.println(this.width / 2 - 10);
        System.out.println(this.height);

        super.init();
    }

    @Override
    public void tick() {
        super.tick();
    }

    public static class EnchantmentsListWidget extends EntryListWidget<EnchantmentEntry> {

        public EnchantmentsListWidget(MinecraftClient client, int width, int height, int top, int bottom, int itemHeight) {
            super(client, width, height, top, bottom, itemHeight);
            setRenderBackground(false);
            setRenderHorizontalShadows(false);

            for(Enchantment enchantment : LibrarianTradeFinder.getConfig().enchantments.keySet()) {
                this.addEntry(new EnchantmentEntry(enchantment, width, height));
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

        public EnchantmentEntry(Enchantment enchantment, int listWidth, int listHeight) {
            super();
            this.enchantment = enchantment;
        }

        @Override
        public void render(MatrixStack matrices, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
            if(y < 8) return;
            matrices.push();
            RenderSystem.enableDepthTest();
            matrices.translate(0, 0, -100);

            if(LibrarianTradeFinder.getConfig().enchantments.get(enchantment)) {
                DrawableHelper.fill(matrices, x, y, x + entryWidth, y + entryHeight, 0x3F00FF00);
            }else {
                DrawableHelper.fill(matrices, x, y, x + entryWidth, y + entryHeight, 0x0FC7C0C0);
            }
            DrawableHelper.drawTextWithShadow(matrices, MinecraftClient.getInstance().textRenderer, enchantment.getName(enchantment.getMaxLevel()).copy().formatted(Formatting.WHITE), 8, y + (entryHeight / 2 / 2), 0xFFFFFF);

            RenderSystem.disableDepthTest();
            matrices.pop();
        }

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
/*            if(mouseX > this.x && mouseX < this.x + this.entryWidth && mouseY > y && mouseY < y + entryHeight) {
            }*/

            LibrarianTradeFinder.getConfig().enchantments.put(enchantment, !LibrarianTradeFinder.getConfig().enchantments.get(enchantment));
            return true;
        }

        private void drawOutline(MatrixStack matrices, int x1, int y1, int x2, int y2, int width, int color) {
            DrawableHelper.fill(matrices, x1, y1, x2, y1 + width, color);
            DrawableHelper.fill(matrices, x2, y1, x2 - width, y2, color);
            DrawableHelper.fill(matrices, x1, y2, x2, y2 - width, color);
            DrawableHelper.fill(matrices, x1, y1, x1 + width, y2, color);
        }
    }

}
