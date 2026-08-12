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

public class EnchantmentsListWidget extends AbstractSelectionList<EnchantmentEntry> {

    public final GrayButtonWidget resetButton;

    @SuppressWarnings("this-escape")
    public EnchantmentsListWidget(Minecraft client, int width, int height, int top, int itemHeight) {
        super(client, width, height, top, itemHeight);

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
        if(!(keyCode == InputConstants.KEY_BACKSPACE || (keyCode >= InputConstants.KEY_0 && keyCode <= InputConstants.KEY_9) || keyCode == InputConstants.KEY_LEFT || keyCode == InputConstants.KEY_RIGHT)) return false;
        for(EnchantmentEntry enchantmentEntry : children()) {
            if(enchantmentEntry.maxPriceField.isFocused()) return enchantmentEntry.maxPriceField.keyPressed(input);
            if(enchantmentEntry.levelField.isFocused()) return enchantmentEntry.levelField.keyPressed(input);
        }
        return super.keyPressed(input);
    }

    @Override
    public boolean keyReleased(KeyEvent input) {
        int keyCode = input.input();
        if(!(keyCode == InputConstants.KEY_BACKSPACE || (keyCode >= InputConstants.KEY_0 && keyCode <= InputConstants.KEY_9) || keyCode == InputConstants.KEY_LEFT || keyCode == InputConstants.KEY_RIGHT)) return false;
        for (EnchantmentEntry enchantmentEntry : this.children()) {
            enchantmentEntry.maxPriceField.keyReleased(input);
            enchantmentEntry.levelField.keyReleased(input);
        }
        return super.keyReleased(input);
    }

    @Override
    public boolean charTyped(CharacterEvent input) {
        if(!input.isAllowedChatCharacter()) return false;
        for (EnchantmentEntry enchantmentEntry : this.children()) {
            enchantmentEntry.maxPriceField.charTyped(input);
            enchantmentEntry.levelField.charTyped(input);
        }
        return super.charTyped(input);
    }
}