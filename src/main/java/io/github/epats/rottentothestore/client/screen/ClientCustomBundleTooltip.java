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
    private static final int SLOT_WIDTH = 18;
    private static final int SLOT_HEIGHT = 20;
    private static final int BORDER_SIZE = 1;
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
        return gridRows() * SLOT_HEIGHT + 2 + 4;
    }

    public int getWidth(Font font) {
        return gridColumns() * SLOT_WIDTH + 2;
    }

    public void renderImage(Font font, int x, int y, GuiGraphics guiGraphics) {
        int columns = gridColumns();
        int rows = gridRows();
        boolean isBlocked = weight >= maxWeight;
        int slot = 0;

        for (int row = 0; row < rows; ++row) {
            for (int col = 0; col < columns; ++col) {
                int xPos = x + col * SLOT_WIDTH + BORDER_SIZE;
                int yPos = y + row * SLOT_HEIGHT + BORDER_SIZE;
                renderSlot(xPos, yPos, slot++, isBlocked || row * columns + col + BORDER_SIZE > size, font, guiGraphics);
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
            guiGraphics.renderItem(itemStack, x + BORDER_SIZE, y + BORDER_SIZE, slot);
            guiGraphics.renderItemDecorations(font, itemStack, x + BORDER_SIZE, y + BORDER_SIZE);
            if (slot == 0) {
                AbstractContainerScreen.renderSlotHighlight(guiGraphics, x + BORDER_SIZE, y + BORDER_SIZE, 0);
            }
        }
    }

    private void drawBorder(int x, int y, int columns, int rows, GuiGraphics guiGraphics) {
        blit(guiGraphics, x, y, Texture.BORDER_CORNER_TOP);
        blit(guiGraphics, x + columns * SLOT_WIDTH + BORDER_SIZE, y, Texture.BORDER_CORNER_TOP);

        for (int col = 0; col < columns; ++col) {
            blit(guiGraphics, x + BORDER_SIZE + col * SLOT_WIDTH, y, Texture.BORDER_HORIZONTAL_TOP);
            blit(guiGraphics, x + BORDER_SIZE + col * SLOT_WIDTH, y + rows * SLOT_HEIGHT, Texture.BORDER_HORIZONTAL_BOTTOM);
        }

        for (int row = 0; row < rows; ++row) {
            blit(guiGraphics, x, y + row * SLOT_HEIGHT + BORDER_SIZE, Texture.BORDER_VERTICAL);
            blit(guiGraphics, x + columns * SLOT_WIDTH + BORDER_SIZE, y + row * SLOT_HEIGHT + BORDER_SIZE, Texture.BORDER_VERTICAL);
        }

        blit(guiGraphics, x, y + rows * SLOT_HEIGHT, Texture.BORDER_CORNER_BOTTOM);
        blit(guiGraphics, x + columns * SLOT_WIDTH + BORDER_SIZE, y + rows * SLOT_HEIGHT, Texture.BORDER_CORNER_BOTTOM);
    }

    private void blit(GuiGraphics guiGraphics, int x, int y, Texture texture) {
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, TEXTURE_LOCATION);
        guiGraphics.blit(TEXTURE_LOCATION, x, y, 0, (float) texture.x, (float) texture.y, texture.w, texture.h, 128, 128);
    }

    private int gridColumns() {
        return (int) Math.ceil(Math.sqrt(size));
    }

    private int gridRows() {
        return (int) Math.ceil((float) size / gridColumns());
    }

    enum Texture {
        SLOT(0, 0, SLOT_WIDTH, SLOT_HEIGHT),
        BLOCKED_SLOT(0, 40, SLOT_WIDTH, SLOT_HEIGHT),
        BORDER_VERTICAL(0, SLOT_WIDTH, BORDER_SIZE, SLOT_HEIGHT),
        BORDER_HORIZONTAL_TOP(0, SLOT_HEIGHT, SLOT_WIDTH, BORDER_SIZE),
        BORDER_HORIZONTAL_BOTTOM(0, 60, SLOT_WIDTH, BORDER_SIZE),
        BORDER_CORNER_TOP(0, SLOT_HEIGHT, BORDER_SIZE, BORDER_SIZE),
        BORDER_CORNER_BOTTOM(0, 60, BORDER_SIZE, BORDER_SIZE);

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