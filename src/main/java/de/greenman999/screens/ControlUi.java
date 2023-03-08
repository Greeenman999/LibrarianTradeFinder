package de.greenman999.screens;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.systems.RenderCallStorage;
import com.mojang.blaze3d.systems.RenderSystem;
import de.greenman999.LibrarianTradeFinder;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.*;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.List;

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
        this.addDrawableChild(new EnchantmentsListWidget(this.client, this.width / 2 - 10, this.height, 25, this.height - 5, 20));

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
        protected int getScrollbarPositionX() {
            return this.width;
        }
    }

    public static class EnchantmentEntry extends EntryListWidget.Entry<EnchantmentEntry> {

        private final Enchantment enchantment;
        private final int listWidth;
        private final int listHeight;

        private int y;
        private int itemHeight;
        private int index;

        public EnchantmentEntry(Enchantment enchantment, int listWidth, int listHeight) {
            super();
            this.enchantment = enchantment;
            this.listWidth = listWidth;
            this.listHeight = listHeight;
        }

        @Override
        public void render(MatrixStack matrices, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
            this.y = y;
            this.itemHeight = entryHeight;
            this.index = index;
            if(y < 12) return;
            matrices.push();
            RenderSystem.enableDepthTest();
            matrices.translate(0, 0, -100);

            //draw red dots at x y and x + entryWidth y + entryHeight
            DrawableHelper.fill(matrices, x, y, x + 1, y + 1, 0xFFFF0000);
            DrawableHelper.fill(matrices, x + entryWidth, y + entryHeight, x + entryWidth + 1, y + entryHeight + 1, 0xFF0000FF);


            if(LibrarianTradeFinder.getConfig().enchantments.get(enchantment)) {
                DrawableHelper.fill(matrices, 5, y - 5, listWidth - 5, y + entryHeight - 4, 0x3F00FF00);
            }else {
                DrawableHelper.fill(matrices, 5, y - 5, listWidth - 5, y + entryHeight - 4, 0x0FC7C0C0);
            }
            DrawableHelper.drawTextWithShadow(matrices, MinecraftClient.getInstance().textRenderer, enchantment.getName(enchantment.getMaxLevel()).copy().formatted(Formatting.WHITE), 10, y, 0xFFFFFF);

            RenderSystem.disableDepthTest();
            matrices.pop();
        }

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
/*            System.out.println(mouseX + " " + mouseY);
            System.out.println(this.y + " " + this.itemHeight);
            System.out.println(this.y + this.itemHeight - 4);*/

            System.out.println(mouseX > 5);
            System.out.println(mouseX < listWidth - 5);
            System.out.println(mouseY > this.y - 5);
            System.out.println(mouseY < this.y + this.itemHeight - 4);
            System.out.println(this.y + " + " + this.itemHeight + " - 4 = " + (this.y + this.itemHeight - 4));
            System.out.println(button);
            System.out.println(index);

            // check if mouse is in the bounds of the DrawableHelper.fill
            if(mouseX > 5 && mouseX < listWidth - 5 && mouseY > y - 5 && mouseY < y + itemHeight - 4) {
                LibrarianTradeFinder.getConfig().enchantments.put(enchantment, !LibrarianTradeFinder.getConfig().enchantments.get(enchantment));
            }

            return super.mouseClicked(mouseX, mouseY, button);
        }

        private void drawOutline(MatrixStack matrices, int x1, int y1, int x2, int y2, int width, int color) {
            DrawableHelper.fill(matrices, x1, y1, x2, y1 + width, color);
            DrawableHelper.fill(matrices, x2, y1, x2 - width, y2, color);
            DrawableHelper.fill(matrices, x1, y2, x2, y2 - width, color);
            DrawableHelper.fill(matrices, x1, y1, x1 + width, y2, color);
        }
    }

}
