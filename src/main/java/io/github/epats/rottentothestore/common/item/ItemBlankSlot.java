package io.github.epats.rottentothestore.common.item;

import net.minecraft.world.item.Item;

public class ItemBlankSlot extends Item {

    public ItemBlankSlot(Properties prop)
    {
        super(prop);
    }

    public boolean canFitInsideContainerItems()
    {
        return false;
    }
}
