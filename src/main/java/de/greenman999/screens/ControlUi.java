package de.greenman999.screens;

import de.greenman999.LibrarianTradeFinder;
import de.greenman999.TradeFinder;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.text.Text;
import net.minecraft.text.TextColor;
import net.minecraft.util.Formatting;

public class ControlUi extends Screen {

    private final Screen parent;
    private EnchantmentsListWidget enchantmentsListWidget;

    public ControlUi(Screen parent) {
        super(Text.translatable("tradefinderui.screen.title"));
        this.parent = parent;
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        context.drawVerticalLine(this.width / 2, 4, this.height - 5, 0xFFC7C0C0);
        super.renderBackground(context, mouseX, mouseY, delta);

        context.fill(this.width / 2 + 6, 5, this.width - 5, 20, 0xAFC7C0C0);
        context.drawTextWithShadow(this.textRenderer, Text.translatable("tradefinderui.options.title"), this.width / 2 + 10, 9, 0xFFFFFF);
        super.render(context, mouseX, mouseY, delta);
    }

    @Override
    protected void init() {
        this.addDrawableChild(GrayButtonWidget.builder(Text.translatable("tradefinderui.buttons.save"), (buttonWidget) -> {
                    if (this.client != null) {
                        this.client.setScreen(this.parent);
                    }
                    LibrarianTradeFinder.getConfig().save();
                })
                        .dimensions(this.width / 2 + 6, this.height - 25, width / 2 / 2 - 6 - 3, 20)
                        .color(0x4FC7C0C0)
                        .id(0)
                .build());
        this.addDrawableChild(GrayButtonWidget.builder(Text.translatable("tradefinderui.buttons.start").formatted(Formatting.GREEN), (buttonWidget) -> {
                            if((TradeFinder.villager == null || TradeFinder.lecternPos == null) && client != null) {
                                client.inGameHud.getChatHud().addMessage(Text.translatable("commands.tradefinder.start.not-selected").styled(style -> style.withColor(TextColor.fromFormatting(Formatting.RED))));
                                client.setScreen(this.parent);
                            }else {
                                TradeFinder.searchList();
                                if (this.client != null) {
                                    this.client.setScreen(this.parent);
                                }
                                LibrarianTradeFinder.getConfig().save();
                            }
                        })
                .dimensions(this.width / 2 + this.width / 2 / 2 + 3, this.height - 25, width / 2 / 2 - 6, 20)
                .color(0x4FC7C0C0)
                .id(1)
                .build());

        enchantmentsListWidget = new EnchantmentsListWidget(this.client, this.width / 2 - 10, this.height - 30, 25, this.height - 5, 20);
        this.addDrawableChild(enchantmentsListWidget);

        this.addDrawableChild(GrayButtonWidget.builder(getButtonText("tradefinderui.options.tp-to-villager", LibrarianTradeFinder.getConfig().tpToVillager), (buttonWidget) -> {
                    LibrarianTradeFinder.getConfig().tpToVillager = !LibrarianTradeFinder.getConfig().tpToVillager;

                    updateButtonTexts();
                })
                .dimensions(this.width / 2 + 6, 25, this.width / 2 - 10, 20)
                .color(0x4FC7C0C0)
                .id(2)
                .tooltip(Tooltip.of(Text.translatable("tradefinderui.options.tp-to-villager.tooltip")))
                .build());
        this.addDrawableChild(GrayButtonWidget.builder(getButtonText("tradefinderui.options.prevent-axe-break", LibrarianTradeFinder.getConfig().preventAxeBreaking), (buttonWidget) -> {
                    LibrarianTradeFinder.getConfig().preventAxeBreaking = !LibrarianTradeFinder.getConfig().preventAxeBreaking;

                    updateButtonTexts();
                })
                .dimensions(this.width / 2 + 6 , 50, this.width / 2 - 10, 20)
                .color(0x4FC7C0C0)
                .id(3)
                .tooltip(Tooltip.of(Text.translatable("tradefinderui.options.prevent-axe-break.tooltip")))
                .build());
        this.addDrawableChild(GrayButtonWidget.builder(getButtonText("tradefinderui.options.legit-mode", LibrarianTradeFinder.getConfig().legitMode), (buttonWidget) -> {
                    LibrarianTradeFinder.getConfig().legitMode = !LibrarianTradeFinder.getConfig().legitMode;
                    if(!LibrarianTradeFinder.getConfig().legitMode) {
                        LibrarianTradeFinder.getConfig().slowMode = false;
                    }

                    updateButtonTexts();
                })
                .dimensions(this.width / 2 + 6, 75, this.width / 2 - 10, 20)
                .color(0x4FC7C0C0)
                .id(4)
                .tooltip(Tooltip.of(Text.translatable("tradefinderui.options.legit-mode.tooltip")))
                .build());
        this.addDrawableChild(GrayButtonWidget.builder(getButtonText("tradefinderui.options.slow-mode", LibrarianTradeFinder.getConfig().slowMode), (buttonWidget) -> {
                    LibrarianTradeFinder.getConfig().slowMode = !LibrarianTradeFinder.getConfig().slowMode;
                    LibrarianTradeFinder.getConfig().legitMode = LibrarianTradeFinder.getConfig().slowMode || LibrarianTradeFinder.getConfig().legitMode;

                    updateButtonTexts();
                })
                .dimensions(this.width / 2 + 6, 100, this.width / 2 - 10, 20)
                .color(0x4FC7C0C0)
                .id(5)
                .tooltip(Tooltip.of(Text.translatable("tradefinderui.options.slow-mode.tooltip")))
                .build());

        super.init();
    }

    private void updateButtonTexts() {
        for(Element element : this.children()) {
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

    public Text getButtonText(String key, boolean enabled) {
        return Text.translatable(key, (enabled ? Formatting.GREEN + "Enabled" : Formatting.RED + "Disabled"));
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if(super.keyPressed(keyCode, scanCode, modifiers)) {
            return true;
        }else {
            return enchantmentsListWidget.keyPressed(keyCode, scanCode, modifiers);
        }
    }

    @Override
    public boolean charTyped(char chr, int modifiers) {
        return enchantmentsListWidget.charTyped(chr, modifiers);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        for(EnchantmentEntry enchantmentEntry : enchantmentsListWidget.children()) {
            boolean maxPriceFieldSuccess = enchantmentEntry.maxPriceField.mouseClicked(mouseX, mouseY, button);
            boolean levelFieldSuccess = enchantmentEntry.levelField.mouseClicked(mouseX, mouseY, button);
            enchantmentEntry.maxPriceField.setFocused(maxPriceFieldSuccess);
            enchantmentEntry.levelField.setFocused(levelFieldSuccess);
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        for(EnchantmentEntry enchantmentEntry : enchantmentsListWidget.children()) {
            if (enchantmentEntry.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount)) return true;
        }
        return super.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount);
    }

    @Override
    public void close() {
        LibrarianTradeFinder.getConfig().save();
        super.close();
    }
}
