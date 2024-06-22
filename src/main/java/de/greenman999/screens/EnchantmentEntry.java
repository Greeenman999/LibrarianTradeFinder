package de.greenman999.screens;

import com.mojang.blaze3d.systems.RenderSystem;
import de.greenman999.LibrarianTradeFinder;
import de.greenman999.config.TradeFinderConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.MultilineText;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.tooltip.TooltipBackgroundRenderer;
import net.minecraft.client.gui.widget.EntryListWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;

public class EnchantmentEntry extends EntryListWidget.Entry<EnchantmentEntry> {

    public final Enchantment enchantment;
    public final TextFieldWidget maxPriceField;
    public final TextFieldWidget levelField;
    public int x;
    public int y;
    public int entryWidth;
    public int entryHeight;

    public boolean enabled;
    public TradeFinderConfig.EnchantmentOption enchantmentOption;

    public EnchantmentEntry(RegistryEntry<Enchantment> enchantment) {
        super();
        this.enchantment = enchantment.value();
        this.enabled = LibrarianTradeFinder.getConfig().enchantments.get(enchantment.getIdAsString()).isEnabled();
        this.enchantmentOption = LibrarianTradeFinder.getConfig().enchantments.get(enchantment.getIdAsString());

        //maxPriceField = new TextFieldWidget(MinecraftClient.getInstance().textRenderer, 0, 0, 50, 20, Text.of("Max Price"));
        maxPriceField = new TextFieldWidget(MinecraftClient.getInstance().textRenderer, 0, 0, 20, 14, Text.translatable("tradefinderui.enchantments.price.name"));
        maxPriceField.setMaxLength(2);
        maxPriceField.setText(String.valueOf(enchantmentOption.getMaxPrice()));
        //maxPriceField.setDrawsBackground(false);

        levelField = new TextFieldWidget(MinecraftClient.getInstance().textRenderer, 0, 0, 14, 14, Text.translatable("tradefinderui.enchantments.level.name"));
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

        if(!maxPriceField.getText().isEmpty() && !maxPriceField.isActive() && (Integer.parseInt(maxPriceField.getText()) > 64 || Integer.parseInt(maxPriceField.getText()) < 5)) maxPriceField.setText("64");
        if(!levelField.getText().isEmpty() && !levelField.isActive() && (Integer.parseInt(levelField.getText()) > enchantment.getMaxLevel() || Integer.parseInt(levelField.getText()) < 1)) levelField.setText(String.valueOf(enchantment.getMaxLevel()));

        enchantmentOption.setEnabled(enabled);
        enchantmentOption.setMaxPrice(!maxPriceField.getText().isEmpty() ? Integer.parseInt(maxPriceField.getText()) : 64);
        enchantmentOption.setLevel(!levelField.getText().isEmpty() ? Integer.parseInt(levelField.getText()) : enchantment.getMaxLevel());

        if(y < 8) return;

        maxPriceField.setVisible(enabled);
        levelField.setVisible(enabled);
        if(enabled) {
            context.fill(x, y, x + entryWidth, y + entryHeight, 0x3F00FF00);
            context.drawTextWithShadow(MinecraftClient.getInstance().textRenderer, Text.of("$:"), x + entryWidth - 21 - 10, y + (entryHeight / 2 / 2), 0xFFFFFF);
            context.drawTextWithShadow(MinecraftClient.getInstance().textRenderer, Text.of("LVL:"), x + entryWidth - 21 - 15 - 14 - 23, y + (entryHeight / 2 / 2), 0xFFFFFF);
        }else {
            context.fill(x, y, x + entryWidth, y + entryHeight, 0x1AC7C0C0);
        }
        context.drawTextWithShadow(MinecraftClient.getInstance().textRenderer, enchantment.description(), 8, y + (entryHeight / 2 / 2), 0xFFFFFF);

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
        return false;
    }

    @Override
    public void mouseMoved(double mouseX, double mouseY) {
        maxPriceField.mouseMoved(mouseX, mouseY);
        levelField.mouseMoved(mouseX, mouseY);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        boolean maxPriceFieldReturn = maxPriceField.mouseReleased(mouseX, mouseY, button);
        boolean levelFieldReturn = levelField.mouseReleased(mouseX, mouseY, button);
        return maxPriceFieldReturn || levelFieldReturn;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        // Adjust options by hovering the mouse over the text field and scrolling
        // 'amount' is +1.0 or -1.0, sometimes +2.0 or +3.0 for mouse wheels that (physically) snap to positions.
        // There are also mouse wheels that scroll smoothly; the current implementation maybe doesn't work properly with them
        if (maxPriceField.isMouseOver(mouseX, mouseY)){
            enchantmentOption.setMaxPrice(MathHelper.clamp((int) (enchantmentOption.getMaxPrice() + verticalAmount), 5, 64));
            maxPriceField.setText(String.valueOf(enchantmentOption.getMaxPrice()));
            return true;
        }
        else if (levelField.isMouseOver(mouseX, mouseY)){
            enchantmentOption.setLevel(MathHelper.clamp((int) (enchantmentOption.getLevel() + verticalAmount), 1, enchantment.getMaxLevel()));
            levelField.setText(String.valueOf(enchantmentOption.getLevel()));
            return true;
        }
        return false;
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
            //BufferBuilder bufferBuilder = tessellator.getBuffer();
            RenderSystem.setShader(GameRenderer::getPositionColorProgram);
            //bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
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
            //BufferRenderer.drawWithGlobalProgram(bufferBuilder.end());
            RenderSystem.disableBlend();
            matrices.translate(0.0, 0.0, z + 10.0);

            text.drawWithShadow(context, drawX, drawY, lineHeight, -1);

            matrices.pop();
            RenderSystem.disableDepthTest();
        }
    }
}
