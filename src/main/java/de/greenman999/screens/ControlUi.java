package de.greenman999.screens;

import de.greenman999.LibrarianTradeFinder;
import de.greenman999.TradeFinder;
import net.minecraft.client.gui.DrawContext;
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
        super.renderBackground(context);

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
                .build());

        enchantmentsListWidget = new EnchantmentsListWidget(this.client, this.width / 2 - 10, this.height, 25, this.height - 5, 20);
        this.addDrawableChild(enchantmentsListWidget);

        this.addDrawableChild(GrayButtonWidget.builder(getButtonText("tradefinderui.options.tp-to-villager", LibrarianTradeFinder.getConfig().tpToVillager), (buttonWidget) -> {
                    LibrarianTradeFinder.getConfig().tpToVillager = !LibrarianTradeFinder.getConfig().tpToVillager;

                    buttonWidget.setMessage(getButtonText("tradefinderui.options.tp-to-villager", LibrarianTradeFinder.getConfig().tpToVillager));
                })
                .dimensions(this.width / 2 + 6, 25, this.width / 2 - 10, 20)
                .color(0x4FC7C0C0)
                .tooltip(Tooltip.of(Text.translatable("tradefinderui.options.tp-to-villager.tooltip")))
                .build());
        this.addDrawableChild(GrayButtonWidget.builder(getButtonText("tradefinderui.options.prevent-axe-break", LibrarianTradeFinder.getConfig().preventAxeBreaking), (buttonWidget) -> {
                    LibrarianTradeFinder.getConfig().preventAxeBreaking = !LibrarianTradeFinder.getConfig().preventAxeBreaking;

                    buttonWidget.setMessage(getButtonText("tradefinderui.options.prevent-axe-break", LibrarianTradeFinder.getConfig().preventAxeBreaking));
                })
                .dimensions(this.width / 2 + 6 , 50, this.width / 2 - 10, 20)
                .color(0x4FC7C0C0)
                .tooltip(Tooltip.of(Text.translatable("tradefinderui.options.prevent-axe-break.tooltip")))
                .build());
        this.addDrawableChild(GrayButtonWidget.builder(getButtonText("tradefinderui.options.legit-mode", LibrarianTradeFinder.getConfig().legitMode), (buttonWidget) -> {
                    LibrarianTradeFinder.getConfig().legitMode = !LibrarianTradeFinder.getConfig().legitMode;

                    buttonWidget.setMessage(getButtonText("tradefinderui.options.legit-mode", LibrarianTradeFinder.getConfig().legitMode));
                })
                .dimensions(this.width / 2 + 6, 75, this.width / 2 - 10, 20)
                .color(0x4FC7C0C0)
                .tooltip(Tooltip.of(Text.translatable("tradefinderui.options.legit-mode.tooltip")))
                .build());

        super.init();
    }

    public Text getButtonText(String key, boolean enabled) {
        return Text.translatable(key, (enabled ? Formatting.GREEN + "Enabled" : Formatting.RED + "Disabled"));
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
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        for(EnchantmentEntry enchantmentEntry : enchantmentsListWidget.children()) {
            boolean maxPriceFieldSuccess = enchantmentEntry.maxPriceField.mouseClicked(mouseX, mouseY, button);
            boolean levelFieldSuccess = enchantmentEntry.levelField.mouseClicked(mouseX, mouseY, button);
            enchantmentEntry.maxPriceField.setFocused(maxPriceFieldSuccess);
            enchantmentEntry.levelField.setFocused(levelFieldSuccess);
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }
}
