package io.github.epats.rottentothestore.common.inventory.tooltip;


import net.minecraft.core.NonNullList;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;

public class CustomBundleTooltip implements TooltipComponent {
    private final NonNullList<ItemStack> items;
    private final int weight;
    private final int maxWeight;
    private final int size;

    public CustomBundleTooltip(NonNullList<ItemStack> items, int weight, int maxWeight, int size)
    {
        this.items = items;
        this.weight = weight;
        this.maxWeight = maxWeight;
        this.size = size;
    }

    public NonNullList<ItemStack> getItems()
    {
        return this.items;
    }

    public int getWeight()
    {
        return this.weight;
    }

    public int getSize()
    {
        return this.size;
    }

    public int getMaxWeight() {
        return this.maxWeight;
    }
}