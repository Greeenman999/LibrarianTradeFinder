package de.greenman999.librariantradefinder.screens;

import de.greenman999.librariantradefinder.LibrarianTradeFinder;
import de.greenman999.librariantradefinder.TradeFinder;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.CharacterEvent;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextColor;

public class ControlUi extends Screen {

    private final Screen parent;
    private EnchantmentsListWidget enchantmentsListWidget;

    public ControlUi(Screen parent) {
        super(Component.translatable("tradefinderui.screen.title"));
        this.parent = parent;
    }

    @Override
    public void render(GuiGraphics context, int mouseX, int mouseY, float delta) {

        this.renderBackground(context, mouseX, mouseY, delta);
        context.vLine(this.width / 2, 4, this.height - 5, 0xFFC7C0C0);

        context.fill(this.width / 2 + 6, 5, this.width - 5, 20, 0x3FC7C0C0);
        context.drawString(Minecraft.getInstance().font, Component.translatable("tradefinderui.options.title"), this.width / 2 + 10, 9, 0xFFFFFFFF);
        for (Renderable drawable : this.renderables) {
            drawable.render(context, mouseX, mouseY, delta);
        }
    }

    @Override
    public void renderBackground(GuiGraphics context, int mouseX, int mouseY, float deltaTicks) {
        if (this.minecraft != null && this.minecraft.level == null) {
            this.renderPanorama(context, deltaTicks);
        } else {
            context.fill(0, 0, this.width, this.height, 0x90202020);
        }

        this.renderMenuBackground(context);
    }

    @Override
    protected void init() {
        this.addRenderableWidget(GrayButtonWidget.builder(Component.translatable("tradefinderui.buttons.save"), (buttonWidget) -> {
                    if (this.minecraft != null) {
                        this.minecraft.setScreen(this.parent);
                    }
                    LibrarianTradeFinder.getConfig().save();
                })
                        .bounds(this.width / 2 + 6, this.height - 25, width / 2 / 2 - 6 - 3, 20)
                        .color(0x4FC7C0C0)
                        .id(0)
                .build());
        this.addRenderableWidget(GrayButtonWidget.builder(Component.translatable("tradefinderui.buttons.start").withStyle(ChatFormatting.GREEN), (buttonWidget) -> {
                            if((TradeFinder.villager == null || TradeFinder.lecternPos == null) && minecraft != null) {
                                minecraft.gui.getChat().addMessage(Component.translatable("commands.tradefinder.start.not-selected").withStyle(style -> style.withColor(TextColor.fromLegacyFormat(ChatFormatting.RED))));
                                minecraft.setScreen(this.parent);
                            }else {
                                TradeFinder.searchList();
                                if (this.minecraft != null) {
                                    this.minecraft.setScreen(this.parent);
                                }
                                LibrarianTradeFinder.getConfig().save();
                            }
                        })
                .bounds(this.width / 2 + this.width / 2 / 2 + 3, this.height - 25, width / 2 / 2 - 6, 20)
                .color(0x4FC7C0C0)
                .id(1)
                .build());

        enchantmentsListWidget = new EnchantmentsListWidget(this.minecraft, this.width / 2 - 10, this.height - 30, 25, 20);
        this.addRenderableWidget(enchantmentsListWidget);
        this.addRenderableWidget(enchantmentsListWidget.resetButton);

        this.addRenderableWidget(GrayButtonWidget.builder(getButtonText("tradefinderui.options.tp-to-villager", LibrarianTradeFinder.getConfig().tpToVillager), (buttonWidget) -> {
                    LibrarianTradeFinder.getConfig().tpToVillager = !LibrarianTradeFinder.getConfig().tpToVillager;

                    updateButtonTexts();
                })
                .bounds(this.width / 2 + 6, 25, this.width / 2 - 10, 20)
                .color(0x4FC7C0C0)
                .id(2)
                .tooltip(Tooltip.create(Component.translatable("tradefinderui.options.tp-to-villager.tooltip")))
                .build());
        this.addRenderableWidget(GrayButtonWidget.builder(getButtonText("tradefinderui.options.prevent-axe-break", LibrarianTradeFinder.getConfig().preventAxeBreaking), (buttonWidget) -> {
                    LibrarianTradeFinder.getConfig().preventAxeBreaking = !LibrarianTradeFinder.getConfig().preventAxeBreaking;

                    updateButtonTexts();
                })
                .bounds(this.width / 2 + 6 , 50, this.width / 2 - 10, 20)
                .color(0x4FC7C0C0)
                .id(3)
                .tooltip(Tooltip.create(Component.translatable("tradefinderui.options.prevent-axe-break.tooltip")))
                .build());
        this.addRenderableWidget(GrayButtonWidget.builder(getButtonText("tradefinderui.options.legit-mode", LibrarianTradeFinder.getConfig().legitMode), (buttonWidget) -> {
                    LibrarianTradeFinder.getConfig().legitMode = !LibrarianTradeFinder.getConfig().legitMode;
                    if(!LibrarianTradeFinder.getConfig().legitMode) {
                        LibrarianTradeFinder.getConfig().slowMode = false;
                    }

                    updateButtonTexts();
                })
                .bounds(this.width / 2 + 6, 75, this.width / 2 - 10, 20)
                .color(0x4FC7C0C0)
                .id(4)
                .tooltip(Tooltip.create(Component.translatable("tradefinderui.options.legit-mode.tooltip")))
                .build());
        this.addRenderableWidget(GrayButtonWidget.builder(getButtonText("tradefinderui.options.slow-mode", LibrarianTradeFinder.getConfig().slowMode), (buttonWidget) -> {
                    LibrarianTradeFinder.getConfig().slowMode = !LibrarianTradeFinder.getConfig().slowMode;
                    LibrarianTradeFinder.getConfig().legitMode = LibrarianTradeFinder.getConfig().slowMode || LibrarianTradeFinder.getConfig().legitMode;

                    updateButtonTexts();
                })
                .bounds(this.width / 2 + 6, 100, this.width / 2 - 10, 20)
                .color(0x4FC7C0C0)
                .id(5)
                .tooltip(Tooltip.create(Component.translatable("tradefinderui.options.slow-mode.tooltip")))
                .build());

        super.init();
    }

    private void updateButtonTexts() {
        for(GuiEventListener element : this.children()) {
            if(!(element instanceof GrayButtonWidget buttonWidget)) continue;
            switch (buttonWidget.getId()) {
                case 2 ->
                        buttonWidget.setMessage(getButtonText("tradefinderui.options.tp-to-villager", LibrarianTradeFinder.getConfig().tpToVillager));
                case 3 ->
                        buttonWidget.setMessage(getButtonText("tradefinderui.options.prevent-axe-break", LibrarianTradeFinder.getConfig().preventAxeBreaking));
                case 4 ->
                        buttonWidget.setMessage(getButtonText("tradefinderui.options.legit-mode", LibrarianTradeFinder.getConfig().legitMode));
                case 5 ->
                        buttonWidget.setMessage(getButtonText("tradefinderui.options.slow-mode", LibrarianTradeFinder.getConfig().slowMode));
            }
        }
    }

    public Component getButtonText(String key, boolean enabled) {
        return Component.translatable(key, (enabled ? ChatFormatting.GREEN + "Enabled" : ChatFormatting.RED + "Disabled"));
    }

    @Override
    public boolean keyPressed(KeyEvent input) {
        if(super.keyPressed(input)) {
            return true;
        }else {
            return enchantmentsListWidget.keyPressed(input);
        }
    }

    @Override
    public boolean charTyped(CharacterEvent input) {
        return enchantmentsListWidget.charTyped(input);
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent click, boolean doubled) {
        for(EnchantmentEntry enchantmentEntry : enchantmentsListWidget.children()) {
            boolean maxPriceFieldSuccess = enchantmentEntry.maxPriceField.mouseClicked(click, doubled);
            boolean levelFieldSuccess = enchantmentEntry.levelField.mouseClicked(click, doubled);
            enchantmentEntry.maxPriceField.setFocused(maxPriceFieldSuccess);
            enchantmentEntry.levelField.setFocused(levelFieldSuccess);
        }
        return super.mouseClicked(click, doubled);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        for(EnchantmentEntry enchantmentEntry : enchantmentsListWidget.children()) {
            if (enchantmentEntry.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount)) return true;
        }
        return super.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount);
    }

    @Override
    public void onClose() {
        LibrarianTradeFinder.getConfig().save();
        super.onClose();
    }

}
