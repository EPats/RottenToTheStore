package io.github.epats.rottentothestore.common.item;

import io.github.epats.rottentothestore.common.InventoryHelper;
import net.minecraft.world.item.ItemStack;

public interface StorageItem {

    int getNumberOfSlots();

    int getMaxWeight() ;

    boolean canBagTakeItem(ItemStack stackToInsert);

    default float getFullnessDisplay(ItemStack bagStack) {
        return (float) InventoryHelper.getWeightOfBagContents(bagStack) / ((float) getMaxWeight());
    }
}
