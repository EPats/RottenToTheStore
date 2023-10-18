package io.github.epats.rottentothestore.client.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import io.github.epats.rottentothestore.common.inventory.tooltip.CustomBundleTooltip;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

public class ClientCustomBundleTooltip implements ClientTooltipComponent {
    public static final ResourceLocation TEXTURE_LOCATION = new ResourceLocation("textures/gui/container/bundle.png");
    private final NonNullList<ItemStack> items;
    private final int weight;
    private final int maxWeight;
    private final int size;

    public ClientCustomBundleTooltip(CustomBundleTooltip tooltip) {
        this.items = tooltip.getItems();
        this.weight = tooltip.getWeight();
        this.maxWeight = tooltip.getMaxWeight();
        this.size = tooltip.getSize();
    }

    public int getHeight() {
        return gridSizeY() * 20 + 2 + 4;
    }

    public int getWidth(Font font) {
        return gridSizeX() * 18 + 2;
    }

    public void renderImage(Font font, int x, int y, GuiGraphics guiGraphics) {
        int columns = gridSizeX();
        int rows = gridSizeY();
        boolean isBlocked = weight >= maxWeight;
        int slot = 0;

        for (int row = 0; row < rows; ++row) {
            for (int col = 0; col < columns; ++col) {
                int xPos = x + col * 18 + 1;
                int yPos = y + row * 20 + 1;
                renderSlot(xPos, yPos, slot++, isBlocked || row * columns + col + 1 > size, font, guiGraphics);
            }
        }

        drawBorder(x, y, columns, rows, guiGraphics);
    }

    private void renderSlot(int x, int y, int slot, boolean isBlocked, Font font, GuiGraphics guiGraphics) {
        if (slot >= items.size()) {
            blit(guiGraphics, x, y, isBlocked ? Texture.BLOCKED_SLOT : Texture.SLOT);
        } else {
            ItemStack itemStack = items.get(slot);
            blit(guiGraphics, x, y, Texture.SLOT);
            guiGraphics.renderItem(itemStack, x + 1, y + 1, slot);
            guiGraphics.renderItemDecorations(font, itemStack, x + 1, y + 1);
            if (slot == 0) {
                AbstractContainerScreen.renderSlotHighlight(guiGraphics, x + 1, y + 1, 0);
            }
        }
    }

    private void drawBorder(int x, int y, int columns, int rows, GuiGraphics guiGraphics) {
        blit(guiGraphics, x, y, Texture.BORDER_CORNER_TOP);
        blit(guiGraphics, x + columns * 18 + 1, y, Texture.BORDER_CORNER_TOP);

        for (int col = 0; col < columns; ++col) {
            blit(guiGraphics, x + 1 + col * 18, y, Texture.BORDER_HORIZONTAL_TOP);
            blit(guiGraphics, x + 1 + col * 18, y + rows * 20, Texture.BORDER_HORIZONTAL_BOTTOM);
        }

        for (int row = 0; row < rows; ++row) {
            blit(guiGraphics, x, y + row * 20 + 1, Texture.BORDER_VERTICAL);
            blit(guiGraphics, x + columns * 18 + 1, y + row * 20 + 1, Texture.BORDER_VERTICAL);
        }

        blit(guiGraphics, x, y + rows * 20, Texture.BORDER_CORNER_BOTTOM);
        blit(guiGraphics, x + columns * 18 + 1, y + rows * 20, Texture.BORDER_CORNER_BOTTOM);
    }

    private void blit(GuiGraphics guiGraphics, int x, int y, Texture texture) {
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, TEXTURE_LOCATION);
        guiGraphics.blit(TEXTURE_LOCATION, x, y, 0, (float) texture.x, (float) texture.y, texture.w, texture.h, 128, 128);
    }

    private int gridSizeX() {
        return (int) Math.ceil(Math.sqrt(size));
    }

    private int gridSizeY() {
        return (int) Math.ceil((float) size / gridSizeX());
    }

    enum Texture {
        SLOT(0, 0, 18, 20),
        BLOCKED_SLOT(0, 40, 18, 20),
        BORDER_VERTICAL(0, 18, 1, 20),
        BORDER_HORIZONTAL_TOP(0, 20, 18, 1),
        BORDER_HORIZONTAL_BOTTOM(0, 60, 18, 1),
        BORDER_CORNER_TOP(0, 20, 1, 1),
        BORDER_CORNER_BOTTOM(0, 60, 1, 1);

        public final int x;
        public final int y;
        public final int w;
        public final int h;

        Texture(int x, int y, int w, int h) {
            this.x = x;
            this.y = y;
            this.w = w;
            this.h = h;
        }
    }
}