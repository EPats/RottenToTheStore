package io.github.epats.rottentothestore.common.inventory.tooltip;


import net.minecraft.core.NonNullList;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;

public record CustomBundleTooltip(NonNullList<ItemStack> items, int weight, int maxWeight,
                                  int size) implements TooltipComponent {
}