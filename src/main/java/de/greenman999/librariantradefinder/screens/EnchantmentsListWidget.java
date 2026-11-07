package de.greenman999.librariantradefinder.screens;

import com.mojang.blaze3d.platform.InputConstants;
import de.greenman999.librariantradefinder.LibrarianTradeFinder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractSelectionList;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.input.CharacterEvent;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.enchantment.Enchantment;
import org.joml.Matrix3x2fStack;

public class EnchantmentsListWidget extends AbstractSelectionList<EnchantmentEntry> {

    public GrayButtonWidget resetButton;
    public int top;

    public EnchantmentsListWidget(Minecraft client, int width, int height, int top, int itemHeight) {
        super(client, width, height, top, itemHeight);
        this.top = top;

        for(Enchantment enchantment : LibrarianTradeFinder.getConfig().enchantments.keySet()) {
            this.addEntry(new EnchantmentEntry(enchantment));
        }

        this.resetButton = GrayButtonWidget.builder(Component.translatable("tradefinderui.reset"), (buttonWidget) -> {
            for(EnchantmentEntry enchantmentEntry : this.children()) {
                        enchantmentEntry.maxPriceField.setValue("64");
                        enchantmentEntry.levelField.setValue(String.valueOf(enchantmentEntry.enchantment.getMaxLevel()));
                        enchantmentEntry.enabled = false;
                    }
                })
                .color(0x5FC7C0C0)
                .bounds(this.width - 45, 5, 50, 15)
                .tooltip(Tooltip.create(Component.translatable("tradefinderui.reset.tooltip")))
                .build();

    }

    @Override
    protected void extractListBackground(GuiGraphicsExtractor context) {
        this.setSelected(null);
        context.fill(5, 5, this.width + 5, 20, 0xAFC7C0C0);
        context.text(Minecraft.getInstance().font, Component.translatable("tradefinderui.enchantments.title"), 9, 9, 0xFFFFFFFF);
    }

    @Override
    protected void extractListSeparators(GuiGraphicsExtractor context) {
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput builder) {

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
    //protected int getScrollbarPositionX() {
    //    return this.width - 10;
    //}

    @Override
    protected int scrollBarX() {
        return this.width;
    }

    @Override
    public int getRowTop(int index) {
        return this.getY() - (int)this.scrollAmount() + index * this.defaultEntryHeight;
    }

    @Override
    public int getRowLeft() {
        return this.getX() + this.width / 2 - this.getRowWidth() / 2 - 1;
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent click, boolean doubled) {
        resetButton.mouseClicked(click, doubled);
        return super.mouseClicked(click, doubled);
    }

    @Override
    public boolean keyPressed(KeyEvent input) {
        int keyCode = input.input();
        if (!isFieldKey(keyCode)) return false;
        for(EnchantmentEntry enchantmentEntry : children()) {
            if(enchantmentEntry.maxPriceField.isFocused()) return enchantmentEntry.maxPriceField.keyPressed(input);
            if(enchantmentEntry.levelField.isFocused()) return enchantmentEntry.levelField.keyPressed(input);
        }
        return super.keyPressed(input);
    }

    @Override
    public boolean keyReleased(KeyEvent input) {
        int keyCode = input.input();
        if (!isFieldKey(keyCode)) return false;
        for (EnchantmentEntry enchantmentEntry : this.children()) {
            enchantmentEntry.maxPriceField.keyReleased(input);
            enchantmentEntry.levelField.keyReleased(input);
        }
        return super.keyReleased(input);
    }

    private static boolean isFieldKey(int keyCode) {
        return switch (keyCode) {
            case InputConstants.KEY_BACKSPACE, InputConstants.KEY_LEFT, InputConstants.KEY_RIGHT,
                    InputConstants.KEY_0, InputConstants.KEY_1, InputConstants.KEY_2, InputConstants.KEY_3,
                    InputConstants.KEY_4, InputConstants.KEY_5, InputConstants.KEY_6, InputConstants.KEY_7,
                    InputConstants.KEY_8, InputConstants.KEY_9 -> true;
            default -> false;
        };
    }

    @Override
    public boolean charTyped(CharacterEvent input) {
        if (input.codepoint() < '0' || input.codepoint() > '9') return false;
        for (EnchantmentEntry enchantmentEntry : this.children()) {
            enchantmentEntry.maxPriceField.charTyped(input);
            enchantmentEntry.levelField.charTyped(input);
        }
        return super.charTyped(input);
    }
}
