package io.github.epats.rottentothestore.common;

import io.github.epats.rottentothestore.RottenToTheStore;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class CreativeModeTabRegistry {

    private static final DeferredRegister<CreativeModeTab> TABS = DeferredRegister
            .create(Registries.CREATIVE_MODE_TAB, RottenToTheStore.MOD_ID);

    public static final List<Supplier<? extends ItemLike>> BAG_TAB_ITEMS = new ArrayList<>();

    public static final RegistryObject<CreativeModeTab> BAG_TAB =
            TABS.register("bag_tab", () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup." + "bag_tab"))
                    .icon(ItemRegistry.GRASS_BUNDLE.get()::getDefaultInstance)
                    .displayItems((displayParams, output) -> BAG_TAB_ITEMS.forEach(itemLike -> output.accept(itemLike.get())))
                    .build());


    public static <T extends Item> RegistryObject<T> addItemToTab(RegistryObject<T> itemLike, List<Supplier<? extends ItemLike>> tabItems) {
        tabItems.add(itemLike);
        return itemLike;
    }

    public static void register(IEventBus modBus) {
        TABS.register(modBus);
    }

}

