package io.github.epats.rottentothestore.common;

import io.github.epats.rottentothestore.RottenToTheStore;
import io.github.epats.rottentothestore.common.item.ItemBlankSlot;
import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ItemRegistry {

    private static final DeferredRegister<Item> ITEMS = DeferredRegister
            .create(ForgeRegistries.ITEMS, RottenToTheStore.MOD_ID);

    public static final RegistryObject<Item> BLANK_SLOT = ITEMS.register("blank_slot",
            () -> new ItemBlankSlot(new Item.Properties().stacksTo(1)));

    public static void register(IEventBus modBus) {
        ITEMS.register(modBus);
    }
}

