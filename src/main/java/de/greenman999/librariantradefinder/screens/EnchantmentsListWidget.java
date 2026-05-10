package de.greenman999.librariantradefinder.screens;

import com.mojang.blaze3d.platform.InputConstants;
import de.greenman999.librariantradefinder.LibrarianTradeFinder;
import de.greenman999.librariantradefinder.config.TradeFinderConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractSelectionList;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.input.CharacterEvent;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.EnchantmentTags;
import net.minecraft.world.item.enchantment.Enchantment;
import org.joml.Matrix3x2fStack;

public class EnchantmentsListWidget extends AbstractSelectionList<EnchantmentEntry> {

    public GrayButtonWidget resetButton;
    public GrayButtonWidget setMinPricesButton;
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

        this.setMinPricesButton = GrayButtonWidget.builder(Component.translatable("tradefinderui.buttons.set-min-prices"), (buttonWidget) -> {
                    if (Minecraft.getInstance().level == null) return;
                    Registry<Enchantment> registry = TradeFinderConfig.getEnchantmentRegistry();
                    for (EnchantmentEntry enchantmentEntry : this.children()) {
                        Integer minPrice = 2 + Integer.valueOf(enchantmentEntry.levelField.getValue()) * 3;
                        ResourceKey<Enchantment> key = registry.getResourceKey(enchantmentEntry.enchantment).orElseThrow();

                        boolean doublePrice = registry.getOrThrow(key).is(EnchantmentTags.DOUBLE_TRADE_PRICE);
                        if (doublePrice) {
                            minPrice *= 2;
                        }
                        if (minPrice > 64) {
                            minPrice = 64;
                        }
                        enchantmentEntry.maxPriceField.setValue(minPrice.toString());
                    }
                })
                .bounds(this.width - 97, 5, 50, 15)
                .color(0x5FC7C0C0)
                .tooltip(Tooltip.create(Component.translatable("tradefinderui.options.set-min-prices.tooltip")))
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
        setMinPricesButton.mouseClicked(click, doubled);
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