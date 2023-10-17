package io.github.epats.rottentothestore.common.item;

import io.github.epats.rottentothestore.common.ItemRegistry;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class ItemBlankSlot extends Item {

    public ItemBlankSlot(Properties prop)
    {
        super(prop);
    }

    public boolean canFitInsideContainerItems()
    {
        return false;
    }

    public static void fullRefreshBlankSlots(Player player) {
        removeBlankSlots(player);
        fillWithBlankSlots(player);
    }

    public static void fillOrRemoveBlankSlots(Player player) {
        fillOrRemoveBlankSlots(player, player.isCreative() || player.isSpectator());
    }

    public static void fillOrRemoveBlankSlots(Player player, boolean remove) {
        if(remove) {
            removeBlankSlots(player);
        } else {
            fillWithBlankSlots(player);
        }
    }

    private static void fillWithBlankSlots(Player player) {
        Inventory inventory = player.getInventory();
        int i = 0;

        for (int j = 9; j < 36 - i; j++) {
            ItemStack is = inventory.getItem(j);
            if (!is.isEmpty() && !(is.getItem() == ItemRegistry.BLANK_SLOT.get()))
                player.drop(is, true);

            inventory.setItem(j, new ItemStack(ItemRegistry.BLANK_SLOT.get()));
        }
    }

    private static void removeBlankSlots(Player player) {
        Inventory inventory = player.getInventory();
        int i = 0;

        for (int j = 9; j < 36 - i; j++) {
            ItemStack is = inventory.getItem(j);
            if (!is.isEmpty() && is.getItem() == ItemRegistry.BLANK_SLOT.get()) {
                inventory.setItem(j, ItemStack.EMPTY);
            }
        }
    }
}
