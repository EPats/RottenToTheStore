package io.github.epats.rottentothestore.common;

import io.github.epats.rottentothestore.RottenToTheStore;
import io.github.epats.rottentothestore.client.WearableStorageLayer;
import io.github.epats.rottentothestore.common.item.ItemBlankSlot;
import io.github.epats.rottentothestore.common.item.ItemBundle;
import io.github.epats.rottentothestore.common.item.WearableTest;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.List;
import java.util.function.Predicate;
import java.util.function.Supplier;

import static io.github.epats.rottentothestore.common.CreativeModeTabRegistry.addItemToTab;

public class ItemRegistry {

    private static final DeferredRegister<Item> ITEMS = DeferredRegister
            .create(ForgeRegistries.ITEMS, RottenToTheStore.MOD_ID);

    public static final RegistryObject<Item> BLANK_SLOT = ITEMS.register("blank_slot",
            () -> new ItemBlankSlot(new Item.Properties().stacksTo(1)));

    private static final Item.Properties DEFAULT_BAG_PROPERTIES = new Item.Properties().stacksTo(1);
    private static final Predicate<ItemStack> ACCEPT_ALL_ITEM_TYPES = (itemStack -> true);
    public static final RegistryObject<Item> GRASS_BUNDLE =
            createBag("grass_bundle", DEFAULT_BAG_PROPERTIES, 2, 32, ACCEPT_ALL_ITEM_TYPES);

    public static final RegistryObject<Item> LEATHER_BUNDLE =
            createBag("leather_bundle", DEFAULT_BAG_PROPERTIES, 2, 64, ACCEPT_ALL_ITEM_TYPES);

    public static final RegistryObject<Item> SHALLOW_BUNDLE =
            createBag("shallow_bundle", DEFAULT_BAG_PROPERTIES, 16, 32, ACCEPT_ALL_ITEM_TYPES);

    public static final RegistryObject<Item> ENDER_BUNDLE =
            createBag("ender_bundle", DEFAULT_BAG_PROPERTIES, 4, 64 * 4, ACCEPT_ALL_ITEM_TYPES);

    public static final RegistryObject<Item> ICE_BUNDLE =
            createBag("ice_bundle", DEFAULT_BAG_PROPERTIES, 4, 64 * 4,
                    (itemStack -> itemStack.isEmpty() || itemStack.getItem().isEdible()));

    public static final RegistryObject<WearableTest> TEST = addItemToTab(ITEMS.register("test",
            () -> new WearableTest(new Item.Properties()
                    , new EquipmentSlot[]{EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.HEAD}
                    , (slot -> {
                        if(slot == EquipmentSlot.CHEST) {
                            return new WearableStorageLayer.BagParts[]{
                                    WearableStorageLayer.BagParts.BUNDLE_BACK};
                        } else if(slot == EquipmentSlot.LEGS) {
                            return new WearableStorageLayer.BagParts[]{
                                    WearableStorageLayer.BagParts.BUNDLE_SIDE_STRAP,
                                    WearableStorageLayer.BagParts.BUNDLE_SIDE};
                        } else if(slot == EquipmentSlot.HEAD) {
                            return new WearableStorageLayer.BagParts[]{
                                    WearableStorageLayer.BagParts.BACKPACK_MAIN,
                                    WearableStorageLayer.BagParts.BACKPACK_TOP,
                                    WearableStorageLayer.BagParts.BACKPACK_FRONT
                            };
                        } else {
                            return WearableTest.EMPTY_BAG_PARTS_ARRAY;
                        }
                     })
                    , (slot -> {
                        if(slot == EquipmentSlot.HEAD) {
                            return new WearableStorageLayer.BagParts[]{
                                    WearableStorageLayer.BagParts.BACKPACK_MAIN_BUTTONS,
                                    WearableStorageLayer.BagParts.BACKPACK_TOP_BUTTONS,
                                    WearableStorageLayer.BagParts.BACKPACK_FRONT_BUTTONS
                            };
                        } else {
                            return WearableTest.EMPTY_BAG_PARTS_ARRAY;
                        }
                    })
                    , WearableStorageLayer.BACKPACK_TEXTURE, WearableStorageLayer.BACKPACK_TEXTURE_BUTTONS, true))
                    , CreativeModeTabRegistry.BAG_TAB_ITEMS);

    public static void register(IEventBus modBus) {
        ITEMS.register(modBus);
    }

    private static <T extends Item> RegistryObject<T> createItemWithTab(String name, Item.Properties properties,
                                                                        List<Supplier<? extends ItemLike>> creativeTab) {
        RegistryObject<T> itemLike = (RegistryObject<T>) ITEMS.register(name, () -> new Item(properties));
        return addItemToTab(itemLike, creativeTab);
    }

    private static <T extends Item> RegistryObject<T> createBag(String name, Item.Properties properties,
                                                                int slots, int maxWeight, Predicate<ItemStack> itemRestrictions) {
        RegistryObject<T> itemLike = (RegistryObject<T>) ITEMS.register(name, () -> new ItemBundle(properties, slots, maxWeight, itemRestrictions));
        return addItemToTab(itemLike, CreativeModeTabRegistry.BAG_TAB_ITEMS);
    }
}

