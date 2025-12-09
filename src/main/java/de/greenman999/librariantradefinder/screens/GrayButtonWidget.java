package de.greenman999.librariantradefinder.screens;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix3x2fStack;

public class GrayButtonWidget extends Button {
    int color;
    int id;

    protected GrayButtonWidget(int x, int y, int width, int height, net.minecraft.network.chat.Component message, OnPress onPress, CreateNarration narrationSupplier, int color, int id) {
        super(x, y, width, height, message, onPress, narrationSupplier);
        this.color = color;
        this.id = id;
    }

    @Override
    protected void renderContents(GuiGraphics context, int mouseX, int mouseY, float deltaTicks) {
        this.setFocused(false);
        Matrix3x2fStack matrices = context.pose();
        matrices.pushMatrix();
        matrices.translate(0, 0);
        context.fill(this.getX(), this.getY(), this.getX() + this.getWidth(), this.getY() + this.getHeight(), color);
        int j = this.active ? 16777215 : 10526880;
        context.drawCenteredString(Minecraft.getInstance().font, this.getMessage(), this.getX() + this.width / 2, this.getY() + (this.height - 7) / 2, j | Mth.ceil(this.alpha * 255.0F) << 24);
        matrices.popMatrix();
    }

    @Override
    public boolean keyPressed(KeyEvent input) {
        int keyCode = input.input();
        if(keyCode == InputConstants.KEY_RETURN || keyCode == InputConstants.KEY_SPACE || keyCode == InputConstants.KEY_UP || keyCode == InputConstants.KEY_DOWN || keyCode == InputConstants.KEY_LEFT || keyCode == InputConstants.KEY_RIGHT) {
            return false;
        }
        return super.keyPressed(input);
    }

    public static GrayButtonWidget.Builder builder(net.minecraft.network.chat.Component message, OnPress onPress) {
        return new GrayButtonWidget.Builder(message, onPress);
    }

    public int getId() {
        return id;
    }

    public static class Builder extends Button.Builder {
        private final net.minecraft.network.chat.Component message;
        private final OnPress onPress;
        @Nullable
        private Tooltip tooltip;
        private int x;
        private int y;
        private int width = 150;
        private int height = 20;
        private CreateNarration narrationSupplier;
        private int color;
        private int id;

        public Builder(net.minecraft.network.chat.Component message, OnPress onPress) {
            super(message, onPress);
            this.narrationSupplier = Button.DEFAULT_NARRATION;
            this.message = message;
            this.onPress = onPress;
        }

        public GrayButtonWidget.Builder pos(int x, int y) {
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

        public GrayButtonWidget.Builder bounds(int x, int y, int width, int height) {
            return this.pos(x, y).size(width, height);
        }

        public GrayButtonWidget.Builder tooltip(@Nullable Tooltip tooltip) {
            this.tooltip = tooltip;
            return this;
        }

        public GrayButtonWidget.Builder createNarration(CreateNarration narrationSupplier) {
            this.narrationSupplier = narrationSupplier;
            return this;
        }

        public GrayButtonWidget.Builder color(int color) {
            this.color = color;
            return this;
        }

        public GrayButtonWidget.Builder id(int id) {
            this.id = id;
            return this;
        }

        public GrayButtonWidget build() {
            GrayButtonWidget buttonWidget = new GrayButtonWidget(this.x, this.y, this.width, this.height, this.message, this.onPress, this.narrationSupplier, this.color, this.id);
            buttonWidget.setTooltip(this.tooltip);
            return buttonWidget;
        }
    }
}